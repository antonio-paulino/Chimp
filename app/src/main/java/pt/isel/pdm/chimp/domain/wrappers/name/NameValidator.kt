package pt.isel.pdm.chimp.domain.wrappers.name

import pt.isel.pdm.chimp.domain.Either
import pt.isel.pdm.chimp.domain.failure
import pt.isel.pdm.chimp.domain.success

class NameValidator(
    private val maxNameLength: Int = MAX_NAME_LENGTH,
    private val minNameLength: Int = MIN_NAME_LENGTH,
) {
    companion object {
        private const val MAX_NAME_LENGTH = 30
        private const val MIN_NAME_LENGTH = 3
    }

    fun validate(value: String): Either<List<NameValidationError>, Unit> {
        val errors = mutableListOf<NameValidationError>()

        if (value.isBlank()) {
            errors.add(NameValidationError.Blank)
        }

        if (value.length !in minNameLength..maxNameLength) {
            errors.add(NameValidationError.InvalidLength(minNameLength, maxNameLength))
        }

        if (errors.isNotEmpty()) {
            return failure(errors)
        }

        return success(Unit)
    }
}
