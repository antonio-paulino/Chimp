package im.domain.wrappers.email

import pt.isel.pdm.chimp.domain.Failure
import pt.isel.pdm.chimp.domain.Success

/**
 * Email wrapper class that enforces email validation rules.
 *
 * An email must not be blank, must be between 5 and 50 characters, and follow the email format.
 */
@JvmInline
value class Email(
    val value: String,
) {
    companion object {
        private val validator = EmailValidator()
    }

    init {
        val validation = validator.validate(value)
        require(validation is Success) { (validation as Failure).value.toErrorMessage() }
    }

    override fun toString(): String = value
}

fun String.toEmail(): Email = Email(this)
