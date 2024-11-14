package pt.isel.pdm.chimp.domain.wrappers.email

import pt.isel.pdm.chimp.domain.Either
import pt.isel.pdm.chimp.domain.failure
import pt.isel.pdm.chimp.domain.success

class EmailValidator(
    private val maxEmailLength: Int = MAX_EMAIL_LENGTH,
    private val minEmailLength: Int = MIN_EMAIL_LENGTH,
    private val emailRegex: String = EMAIL_REGEX,
) {
    companion object {
        private const val MAX_EMAIL_LENGTH = 50
        private const val MIN_EMAIL_LENGTH = 8
        private const val EMAIL_REGEX = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}\$"
    }

    fun validate(email: String): Either<List<EmailValidationError>, Unit> {
        val errors = mutableListOf<EmailValidationError>()

        if (email.isBlank()) {
            errors.add(EmailValidationError.Blank)
        }

        if (!email.matches(Regex(emailRegex))) {
            errors.add(EmailValidationError.InvalidFormat)
        }

        if (email.length !in minEmailLength..maxEmailLength) {
            errors.add(EmailValidationError.InvalidLength(minEmailLength, maxEmailLength))
        }

        if (errors.isNotEmpty()) {
            return failure(errors)
        }

        return success(Unit)
    }
}
