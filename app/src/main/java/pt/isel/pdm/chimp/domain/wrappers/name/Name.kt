package pt.isel.pdm.chimp.domain.wrappers.name

import pt.isel.pdm.chimp.domain.Failure
import pt.isel.pdm.chimp.domain.Success

/**
 * Name wrapper class that enforces name validation rules.
 *
 * A name must not be blank and must be between 3 and 30 characters.
 */
@JvmInline
value class Name(
    val value: String,
) {
    companion object {
        private val validator = NameValidator()
    }

    init {
        val validation = validator.validate(value)
        require(validation is Success) { (validation as Failure).value.toErrorMessage() }
    }

    override fun toString(): String = value
}

fun String.toName(): Name = Name(this)
