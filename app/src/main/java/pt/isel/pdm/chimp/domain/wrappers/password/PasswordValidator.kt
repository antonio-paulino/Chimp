package pt.isel.pdm.chimp.domain.wrappers.password

import pt.isel.pdm.chimp.domain.Either
import pt.isel.pdm.chimp.domain.failure
import pt.isel.pdm.chimp.domain.success

class PasswordValidator {
    companion object {
        private const val MIN_LENGTH = 8
        private const val MAX_LENGTH = 80

        private const val MIN_LOWERCASE = 1
        private const val MIN_UPPERCASE = 1
        private const val MIN_DIGITS = 1
    }

    fun validate(value: String): Either<List<PasswordValidationError>, Unit> {
        val errors = mutableListOf<PasswordValidationError>()

        if (value.isBlank()) {
            errors.add(PasswordValidationError.Blank)
        }

        if (value.length !in MIN_LENGTH..MAX_LENGTH) {
            errors.add(PasswordValidationError.InvalidLength(MIN_LENGTH, MAX_LENGTH))
        }

        if (value.count { it.isLowerCase() } < MIN_LOWERCASE) {
            errors.add(PasswordValidationError.NotEnoughLowercaseLetters(MIN_LOWERCASE))
        }

        if (value.count { it.isUpperCase() } < MIN_UPPERCASE) {
            errors.add(PasswordValidationError.NotEnoughUppercaseLetters(MIN_UPPERCASE))
        }

        if (value.count { it.isDigit() } < MIN_DIGITS) {
            errors.add(PasswordValidationError.NotEnoughDigits(MIN_DIGITS))
        }

        if (errors.isNotEmpty()) {
            return failure(errors)
        }

        return success(Unit)
    }
}
