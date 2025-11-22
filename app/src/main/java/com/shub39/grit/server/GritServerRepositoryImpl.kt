package com.shub39.grit.server

import android.content.Context
import android.net.wifi.WifiManager
import android.util.Log
import com.shub39.grit.billing.BillingHandler
import com.shub39.grit.core.domain.GritDatastore
import com.shub39.grit.core.habits.domain.Habit
import com.shub39.grit.core.habits.domain.HabitRepo
import com.shub39.grit.core.habits.domain.HabitStatus
import com.shub39.grit.core.tasks.domain.TaskRepo
import com.shub39.grit.core.utils.ErrorResponse
import com.shub39.grit.core.utils.StateData
import com.shub39.grit.core.utils.SuccessResponse
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.install
import io.ktor.server.cio.CIO
import io.ktor.server.cio.CIOApplicationEngine
import io.ktor.server.engine.EmbeddedServer
import io.ktor.server.engine.embeddedServer
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.datetime.LocalDate
import kotlinx.serialization.json.Json
import java.net.NetworkInterface
import java.util.Locale
import kotlin.time.ExperimentalTime

typealias GritServer = EmbeddedServer<CIOApplicationEngine, CIOApplicationEngine.Configuration>?

@OptIn(ExperimentalTime::class)
class GritServerRepositoryImpl(
    private val context: Context,
    private val taskRepo: TaskRepo,
    private val habitRepo: HabitRepo,
    private val datastore: GritDatastore,
    private val billingHandler: BillingHandler
) : GritServerRepository {

    companion object {
        private const val TAG = "GritServer"
    }

    private var server: GritServer = null

    private val _stateData = MutableStateFlow(StateData())

    private val _isRunning = MutableStateFlow(false)
    override val isRunning: StateFlow<Boolean> = _isRunning.asStateFlow()

    private val _serverUrl = MutableStateFlow<String?>(null)
    override val serverUrl: StateFlow<String?> = _serverUrl.asStateFlow()

    private val _serverPort = MutableStateFlow(8080)
    override val serverPort: StateFlow<Int> = _serverPort.asStateFlow()

    override suspend fun startServer(port: Int) {
        if (isRunning.value) {
            Log.d(TAG, "Server already running")
            return
        }

        try {
            val ipAddress = getIpAddress()
            if (ipAddress == null) {
                Log.e("LocalServer", "Unable to get IP address")
                return
            }

            val port = port

            server = embeddedServer(CIO, host = "0.0.0.0", port = port) {
                install(ContentNegotiation) {
                    json(
                        Json {
                            prettyPrint = true
                            isLenient = true
                            ignoreUnknownKeys = true
                            allowStructuredMapKeys = true
                        }
                    )
                }

                routing {
                    get("/api/data") {
                        try {
                            val response = _stateData.value.copy(
                                isUserSubscribed = billingHandler.isPlusUser()
                            )

                            call.respond(HttpStatusCode.OK, response)
                        } catch (e: Exception) {
                            Log.e(TAG, "Error sending data", e)
                            call.respond(
                                HttpStatusCode.InternalServerError,
                                ErrorResponse("Error sending data: ${e.message}")
                            )
                        }
                    }

                    get("/api") {
                        try {
                            call.respond(HttpStatusCode.OK, SuccessResponse("Server OK"))
                        } catch (e: Exception) {
                            Log.e(TAG, "Error sending status", e)
                            call.respond(
                                HttpStatusCode.InternalServerError,
                                ErrorResponse("Error sending status: ${e.message}")
                            )
                        }
                    }

                    post("/api/habit/status") {
                        try {
                            val request = call.receive<Pair<Habit, LocalDate>>()

                            val isHabitCompleted =
                                _stateData.value.habitData.find { it.habit == request.first }?.statuses?.any { it.date == request.second }
                                    ?: false

                            if (isHabitCompleted) {
                                habitRepo.deleteHabitStatus(request.first.id, request.second)
                            } else {
                                habitRepo.insertHabitStatus(
                                    HabitStatus(habitId = request.first.id, date = request.second)
                                )
                            }

                        } catch (e: Exception) {
                            Log.e(TAG, "Error receiving status data", e)
                            call.respond(
                                HttpStatusCode.InternalServerError,
                                ErrorResponse("Error receiving status data: ${e.message}")
                            )
                        }
                    }
                }
            }

            server?.start(wait = false)

            _isRunning.update { true }
            _serverUrl.update { "http://$ipAddress:$port" }
            Log.d(TAG, "Server started at ${serverUrl.value}")

        } catch (e: Exception) {
            Log.e(TAG, "Error starting server", e)

            _isRunning.update { false }
            _serverUrl.update { null }
        }
    }

    override fun stopServer() {
        try {
            server?.stop(1000, 2000)
            server = null
            _isRunning.update { false }
            _serverUrl.update { null }
            Log.d(TAG, "Server stopped")
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping server", e)
        }
    }

    override suspend fun setServerPort(port: Int) {
        if (port in 1024..65535) {
            _serverPort.update { port }
            datastore.setServerPort(port)
        }
    }

    private fun getIpAddress(): String? {
        try {
            // Try to get WiFi IP first
            val wifiManager =
                context.applicationContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager
            wifiManager?.connectionInfo?.ipAddress?.let { ipInt ->
                if (ipInt != 0) {
                    return String.format(
                        Locale.US,
                        "%d.%d.%d.%d",
                        ipInt and 0xff,
                        ipInt shr 8 and 0xff,
                        ipInt shr 16 and 0xff,
                        ipInt shr 24 and 0xff,
                    )
                }
            }

            // Fallback to network interfaces
            val interfaces = NetworkInterface.getNetworkInterfaces()
            while (interfaces.hasMoreElements()) {
                val networkInterface = interfaces.nextElement()
                val addresses = networkInterface.inetAddresses
                while (addresses.hasMoreElements()) {
                    val address = addresses.nextElement()
                    if (!address.isLoopbackAddress && address.hostAddress?.contains(':') == false) {
                        return address.hostAddress
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting IP address", e)
        }

        return null
    }
}