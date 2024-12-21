package pt.isel.pdm.chimp.ui.screens.about

import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings.Global.getString
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import pt.isel.pdm.chimp.R
import pt.isel.pdm.chimp.ui.navigation.navigateToNoAnimation
import pt.isel.pdm.chimp.ui.screens.about.components.Author
import pt.isel.pdm.chimp.ui.screens.about.components.Socials
import pt.isel.pdm.chimp.ui.screens.home.ChannelsActivity
import pt.isel.pdm.chimp.ui.screens.invitations.InvitationsActivity
import pt.isel.pdm.chimp.ui.screens.search.ChannelSearchActivity
import pt.isel.pdm.chimp.ui.theme.ChIMPTheme
import pt.isel.pdm.chimp.ui.utils.showErrorToast

/**
 * Activity that shows information about the application.
 */
class AboutActivity : ComponentActivity() {
    private lateinit var authors: List<Author>

    override fun onCreate(savedInstanceState: Bundle?) {
        authors =
            listOf(
                Author(
                    number = getString(R.string.bernardo_pereira_number),
                    name = getString(R.string.bernardo_pereira_author_title),
                    image = R.drawable.bernardo_pereira,
                    socials =
                        Socials(
                            linkedInUrl = R.string.linkedin_link_bernardo_pereira,
                            githubUrl = R.string.github_link_bernardo_pereira,
                            mailUrl = R.string.mail_link_bernardo_pereira,
                        ),
                ),
                Author(
                    number = getString(R.string.antonio_paulino_number),
                    name = getString(R.string.antonio_paulino_author_title),
                    image = R.drawable.antonio_paulino,
                    socials =
                        Socials(
                            linkedInUrl = R.string.linkedin_link_antonio_paulino,
                            githubUrl = R.string.github_link_antonio_paulino,
                            mailUrl = R.string.mail_link_antonio_paulino,
                        ),
                ),
            )

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ChIMPTheme {
                AboutScreen(
                    onSendMail = { sendMail(it) },
                    onOpenUrl = { openUrl(it) },
                    onHomeNavigation = { navigateToNoAnimation(ChannelsActivity::class.java) },
                    onSearchNavigation = { navigateToNoAnimation(ChannelSearchActivity::class.java) },
                    onInvitationsNavigation = { navigateToNoAnimation(InvitationsActivity::class.java) },
                    authors = authors,
                )
            }
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
            showErrorToast(getString(R.string.error_opening_url))
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
            showErrorToast(getString(R.string.error_sending_mail))
        }
    }

    companion object {
        private const val SUBJECT = "ChIMP"
    }
}
