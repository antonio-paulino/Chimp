package pt.isel.pdm.chimp.infrastructure.services.media.problems

import kotlinx.serialization.Serializable
import pt.isel.pdm.chimp.infrastructure.services.media.problems.Problem.InputValidationProblem
import pt.isel.pdm.chimp.infrastructure.services.media.problems.Problem.ServiceProblem

/**
 * Represents a problem that occurred during the processing of a request.
 *
 * [InputValidationProblem] represents a problem that occurred during the validation of the input data.
 *
 * [ServiceProblem] represents a problem that occurred during the processing of the request at the service level.
 */
sealed class Problem(
    open val status: Int,
    open val type: String,
    open val title: String,
    open val detail: String,
) {
    constructor() : this(0, "", "", "")

    @Serializable
    data class InputValidationProblem(
        override val status: Int,
        override val type: String,
        override val title: String,
        override val detail: String,
        val errors: List<String>,
    ) : Problem(status, type, title, detail)

    @Serializable
    data class ServiceProblem(
        override val status: Int,
        override val type: String,
        override val title: String,
        override val detail: String,
    ) : Problem(status, type, title, detail)

    data object TooManyRequestsProblem : Problem(429, "too-many-requests", "Too many requests", "Too many requests, please try again later")

    data object NoConnection : Problem(0, "no-connection", "No connection", "No connection available")

    data object UnexpectedProblem : Problem(0, "unexpected", "Unexpected problem", "An issue occurred while processing the request")
}
