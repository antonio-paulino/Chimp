package pt.isel.pdm.chimp.domain.wrappers.identifier

import pt.isel.pdm.chimp.domain.Either
import pt.isel.pdm.chimp.domain.failure
import pt.isel.pdm.chimp.domain.success

class IdentifierValidator {
    fun validate(value: Long): Either<List<IdentifierValidationError>, Unit> {
        val errors = mutableListOf<IdentifierValidationError>()

        if (value < 0) {
            errors.add(IdentifierValidationError.NegativeValue)
        }

        if (errors.isNotEmpty()) {
            return failure(errors)
        }

        return success(Unit)
    }
}
