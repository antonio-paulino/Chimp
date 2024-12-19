package pt.isel.pdm.chimp.ui.navigation

import android.content.Intent
import androidx.activity.ComponentActivity

/**
 * Navigates to the specified activity class with no animation
 *
 * @param clazz The activity class to navigate
 * @receiver The context to navigate from
 */
fun <T> ComponentActivity.navigateToNoAnimation(
    clazz: Class<T>,
) {
    val intent = Intent(this, clazz)
    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
    startActivity(intent)
}

/**
 * Navigates to the specified activity class with animation
 *
 * @param clazz The activity class to navigate
 * @receiver The context to navigate from
 */
fun <T> ComponentActivity.navigateTo(
    clazz: Class<T>,
) {
    val intent = Intent(this, clazz)
    startActivity(intent)
}