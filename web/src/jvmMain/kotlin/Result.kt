sealed interface Error

typealias RootError = Error

sealed interface SourceError: Error {
    enum class Network : SourceError {
        NO_INTERNET,
        REQUEST_FAILED,
    }
    enum class Data: SourceError {
        NO_RESULTS,
        PARSE_ERROR,
        IO_ERROR,
        UNKNOWN
    }
}

sealed interface Result<out D, out E: RootError> {
    data class Success<out D, out E: RootError>(val data: D): Result<D, E>
    data class Error<out D, out E: RootError>(
        val error: E,
        val debugMessage: String? = null
    ): Result<D, E>
}