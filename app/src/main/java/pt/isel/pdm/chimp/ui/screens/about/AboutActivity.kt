package pt.isel.pdm.chimp.ui.screens.about

import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import pt.isel.pdm.chimp.R
import pt.isel.pdm.chimp.ui.screens.MainActivity
import pt.isel.pdm.chimp.ui.screens.about.components.Author
import pt.isel.pdm.chimp.ui.screens.about.components.Socials

/**
 * Activity that shows information about the application.
 */
class AboutActivity : MainActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AboutScreen(
                onSendMail = { sendMail(it) },
                onOpenUrl = { openUrl(it) },
                onNavIconClick = { finish() },
                authors = authors,
            )
        }
    }

    /**
     * Opens the given URL.
     */
    private fun openUrl(url: Uri) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, url)
            startActivity(intent)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to open URL: $url", e)
            Toast.makeText(
                this,
                R.string.error_opening_url,
                Toast.LENGTH_SHORT,
            ).show()
        }
    }

    /**
     * Opens the email client with the given email.
     */
    private fun sendMail(mail: String) {
        Log.v(TAG, "Sending email to: $mail")
        try {
            val intent =
                Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto:")
                    putExtra(Intent.EXTRA_EMAIL, arrayOf(mail.removePrefix("mailto:")))
                    putExtra(Intent.EXTRA_SUBJECT, SUBJECT)
                }
            startActivity(intent)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to send email to: $mail", e)
            Toast.makeText(
                this,
                R.string.error_sending_mail,
                Toast.LENGTH_SHORT,
            ).show()
        }
    }

    companion object {
        private const val SUBJECT = "ChIMP"
    }

    private val authors =
        listOf(
            Author(
                number = "50493",
                name = "Bernardo Pereira",
                image = R.drawable.bernardo_pereira,
                socials =
                    Socials(
                        linkedInUrl = R.string.linkedin_link_bernardo_pereira,
                        githubUrl = R.string.github_link_bernardo_pereira,
                        mailUrl = R.string.mail_link_bernardo_pereira,
                    ),
            ),
        )
}
