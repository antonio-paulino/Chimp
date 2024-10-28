package pt.isel.pdm.chimp.domain.messages

import pt.isel.pdm.chimp.domain.Either
import pt.isel.pdm.chimp.domain.failure
import pt.isel.pdm.chimp.domain.success

class MessageValidator {
    companion object {
        private const val MIN_LENGTH = 1
        private const val MAX_LENGTH = 300
    }

    fun validate(message: String): Either<List<MessageValidationError>, Unit> {
        val errors = mutableListOf<MessageValidationError>()

        if (message.isBlank()) {
            errors.add(MessageValidationError.ContentBlank)
        }

        if (message.length !in MIN_LENGTH..MAX_LENGTH) {
            errors.add(MessageValidationError.ContentLength(MIN_LENGTH, MAX_LENGTH))
        }

        if (errors.isNotEmpty()) {
            return failure(errors)
        }

        return success(Unit)
    }
}
