package pt.isel.pdm.chimp.infrastructure.services.media.problems

import kotlinx.serialization.Serializable

/**
 * Represents a problem that occurred during the processing of a request.
 *
 * [InputValidationProblem] represents a problem that occurred during the validation of the input data.
 *
 * [ServiceProblem] represents a problem that occurred during the processing of the request.
 */
sealed class Problem {
    @Serializable
    data class InputValidationProblem(
        val type: String,
        val title: String,
        val status: Int,
        val detail: String,
        val errors: List<String>,
    ) : Problem()

    @Serializable
    data class ServiceProblem(
        val type: String,
        val title: String,
        val status: Int,
        val detail: String,
    ) : Problem()

    data object UnexpectedProblem : Problem()
}
