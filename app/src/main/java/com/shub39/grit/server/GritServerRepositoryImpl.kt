package com.shub39.grit.server

import android.content.Context
import android.net.wifi.WifiManager
import android.util.Log
import com.shub39.grit.core.domain.GritDatastore
import com.shub39.grit.habits.domain.HabitRepo
import com.shub39.grit.tasks.domain.TaskRepo
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.install
import io.ktor.server.cio.CIO
import io.ktor.server.cio.CIOApplicationEngine
import io.ktor.server.engine.EmbeddedServer
import io.ktor.server.engine.embeddedServer
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.net.NetworkInterface
import java.util.Locale

typealias GritServer = EmbeddedServer<CIOApplicationEngine, CIOApplicationEngine.Configuration>?

class GritServerRepositoryImpl(
    private val context: Context,
    private val taskRepo: TaskRepo,
    private val habitRepo: HabitRepo,
    private val datastore: GritDatastore
): GritServerRepository {

    companion object {
        private const val TAG = "GritServer"
    }

    private var server: GritServer = null

    private val _isRunning = MutableStateFlow(false)
    override val isRunning: StateFlow<Boolean> = _isRunning.asStateFlow()

    private val _serverUrl = MutableStateFlow<String?>(null)
    override val serverUrl: StateFlow<String?> = _serverUrl.asStateFlow()

    private val _serverPort = MutableStateFlow(8080)
    override val serverPort: StateFlow<Int> = _serverPort.asStateFlow()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            datastore.getServerPort().collect { port ->
                _serverPort.update { port }
            }
        }
    }

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
                        }
                    )
                }

                routing {
                    get("/") {
                        call.respondText(text = "Hello!!")
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