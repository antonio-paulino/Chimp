package pt.isel.pdm.chimp.ui.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import kotlinx.coroutines.delay

private const val AWAIT_NETWORK_DELAY = 1000L

/**
 * Checks if the device has network connectivity.
 *
 * @return true if the device has network connectivity, false otherwise.
 */
fun Context.isNetworkAvailable(): Boolean {
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = connectivityManager.activeNetwork ?: return false
    val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
    return when {
        activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
        activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
        activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
        else -> false
    }
}

/**
 * Suspends the coroutine until the device has network connectivity.
 */
suspend fun Context.awaitNetworkAvailable() {
    while (!isNetworkAvailable()) {
        delay(AWAIT_NETWORK_DELAY)
    }
}
