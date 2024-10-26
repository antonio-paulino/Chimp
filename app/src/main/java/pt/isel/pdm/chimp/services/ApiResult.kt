package pt.isel.pdm.chimp.services

/**
 * Represents an API request result.
 *
 * An API request can either succeed or fail.
 *
 * A Success result contains the value of the request.
 *
 * A Failure result contains the problem that occurred during the request.
 *
 * An Error result represents an unexpected error.
 *
 * @param S the success type
 * @param F the failure type
 *
 */
sealed class ApiResult<out S, out F> {
    data class Success<out S>(val value: S) : ApiResult<S, Nothing>()

    data class Failure<out F>(val problem: F) : ApiResult<Nothing, F>()

    data object Error : ApiResult<Nothing, Nothing>()
}

fun <S> success(value: S): ApiResult<S, Nothing> = ApiResult.Success(value)

fun <F> failure(problem: F): ApiResult<Nothing, F> = ApiResult.Failure(problem)

fun error(): ApiResult<Nothing, Nothing> = ApiResult.Error
