package pt.isel.pdm.chimp.ui.navigation

import android.content.Intent
import androidx.activity.ComponentActivity

/**
 * Navigates to the specified activity.
 * @param clazz The activity class to navigate
 * @receiver The context to navigate from
 */
fun <T> ComponentActivity.navigateTo(
    clazz: Class<T>,
    animate: Boolean = true,
) {
    val intent = Intent(this, clazz)
    if (!animate) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
    }
    startActivity(intent)
}
