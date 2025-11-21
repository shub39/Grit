import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.call.body
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.request
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive

suspend inline fun <reified T> safeCall(
    execute: () -> HttpResponse
): Result<T, SourceError> {
    val response = try {
        execute()
    } catch (e: SocketTimeoutException) {
        return Result.Error(
            SourceError.Network.REQUEST_FAILED,
            "SocketTimeoutException: ${e.message}"
        )
    } catch (e: UnresolvedAddressException) {
        return Result.Error(
            SourceError.Network.NO_INTERNET,
            "UnresolvedAddressException: ${e.message}"
        )
    } catch (e: Exception) {
        currentCoroutineContext().ensureActive()
        return Result.Error(
            SourceError.Data.UNKNOWN,
            "Unexpected exception: ${e::class.simpleName} - ${e.message}\n${
                e.stackTraceToString().take(500)
            }"
        )
    }

    return responseToResult(response)
}

suspend inline fun <reified T> responseToResult(
    response: HttpResponse
): Result<T, SourceError> {
    return when (response.status.value) {
        in 200..299 -> {
            try {
                Result.Success(response.body<T>())
            } catch (e: NoTransformationFoundException) {
                Result.Error(
                    SourceError.Data.PARSE_ERROR,
                    "Parse error for ${T::class.simpleName}: ${e.message}"
                )
            }
        }

        else -> Result.Error(
            SourceError.Data.UNKNOWN,
            "HTTP ${response.status.value}: ${response.status.description}\nURL: ${response.request.url}"
        )
    }
}