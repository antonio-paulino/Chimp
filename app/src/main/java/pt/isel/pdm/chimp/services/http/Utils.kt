package pt.isel.pdm.chimp.services.http

import pt.isel.pdm.chimp.domain.Either
import pt.isel.pdm.chimp.domain.failure
import pt.isel.pdm.chimp.domain.success
import pt.isel.pdm.chimp.services.ApiResult
import pt.isel.pdm.chimp.services.media.problems.Problem

/**
 * Handles the [ApiResult] by transforming the value of the [ApiResult.Success] into a new value of type [T]
 * and returning it as a [Either] instance.
 *
 * @param transform the function that transforms the value of the [ApiResult.Success] into a new value of type [T]
 *
 * @return a [Either] instance with the transformed value or the [Problem] in case of [ApiResult.Failure] or [ApiResult.Error]
 */
fun <T, R> ApiResult<R, Problem>.handle(transform: (R) -> T): Either<Problem, T> =
    when (this) {
        is ApiResult.Success -> success(transform(this.value))
        is ApiResult.Failure -> failure(this.problem)
        ApiResult.Error -> failure(Problem.UnexpectedProblem)
    }
