package pt.isel.pdm.chimp.domain.wrappers.password

import pt.isel.pdm.chimp.domain.Failure
import pt.isel.pdm.chimp.domain.Success

/**
 * Password wrapper class that enforces password validation rules.
 *
 * A password must not be blank and must be between 8 and 80 characters.
 */
@JvmInline
value class Password(
    val value: String,
) {
    companion object {
        private val validator = PasswordValidator()
    }

    init {
        val validation = validator.validate(value)
        require(validation is Success) { (validation as Failure).value.toErrorMessage() }
    }

    override fun toString(): String = value
}

fun String.toPassword(): Password = Password(this)
