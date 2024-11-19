package pt.isel.pdm.chimp.infrastructure.services.http

import pt.isel.pdm.chimp.domain.Either
import pt.isel.pdm.chimp.domain.failure
import pt.isel.pdm.chimp.domain.pagination.PaginationRequest
import pt.isel.pdm.chimp.domain.pagination.SortRequest
import pt.isel.pdm.chimp.domain.success
import pt.isel.pdm.chimp.infrastructure.services.media.problems.Problem

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

/**
 * Builds a query string from the given parameters.
 *
 * @param name the name to search for
 * @param pagination the pagination parameters
 * @param sort the sort parameters
 *
 * @return the query string
 */
fun buildQuery(
    name: String?,
    pagination: PaginationRequest?,
    sort: SortRequest?,
    filterOwned: Boolean? = null,
): String {
    return listOfNotNull(
        name?.let { "name=$it" },
        pagination?.let { "limit=${it.limit}&offset=${it.offset}" },
        sort?.let { "sort=${it.direction.name}&sortBy=${it.sortBy}" },
        filterOwned?.let { "filterOwned=$it" },
    ).joinToString("&", prefix = "?")
}
