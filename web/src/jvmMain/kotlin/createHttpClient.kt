import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

fun createHttpClent(): HttpClient {
    return HttpClient(OkHttp) {

        install(ContentNegotiation) {
            json(
                json = Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                    allowStructuredMapKeys = true
                }
            )
        }

        install(HttpTimeout) {
            socketTimeoutMillis = 20_000
            requestTimeoutMillis = 20_000
        }

        defaultRequest {
            contentType(ContentType.Application.Json)
        }
    }
}