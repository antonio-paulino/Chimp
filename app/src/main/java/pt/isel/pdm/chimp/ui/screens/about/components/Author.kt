package pt.isel.pdm.chimp.ui.screens.about.components

/**
 * Represents an author of the application.
 * @property number The author's number.
 * @property name The author's name.
 * @property image The author's image resource.
 * @property socials The author's socials.
 */
data class Author(
    val number: String,
    val name: String,
    val image: Int,
    val socials: Socials,
)
