package pt.isel.pdm.chimp.ui.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pt.isel.pdm.chimp.domain.Either
import pt.isel.pdm.chimp.domain.Failure
import pt.isel.pdm.chimp.domain.Success
import pt.isel.pdm.chimp.infrastructure.services.media.problems.Problem

private const val TOO_MANY_REQUESTS_DELAY = 1000L

/**
 * Launches a coroutine, executing the request and handling the response.
 *
 * In case of a [Problem.TooManyRequestsProblem] the request is retried after a delay.
 *
 * @param T the expected type of the response.
 * @param noConnectionRequest the request to execute when there is no connection.
 * @param request the request to execute.
 * @param onError the function to execute when the request fails.
 * @param onSuccess the function to execute when the request succeeds.
 * @param context the context.
 */
fun <T> ViewModel.launchRequest(
    noConnectionRequest: (suspend () -> Either<Problem, T>?)? = null,
    request: suspend () -> Either<Problem, T>,
    onError: (Problem) -> Unit,
    onSuccess: Success<T>.() -> Unit,
    context: Context,
) {
    viewModelScope.launch {
        executeRequest(
            noConnectionRequest = noConnectionRequest,
            request = request,
            onError = onError,
            onSuccess = onSuccess,
            context = context,
        )
    }
}

/**
 * Launches a coroutine, executing the request and handling the response.
 *
 * In case of a [Problem.TooManyRequestsProblem] the request is retried after a delay.
 *
 * In case of a [Problem.ServiceProblem] with status 401 the request is retried once after refreshing
 *
 *
 * @param T the expected type of the response.
 * @param noConnectionRequest the request to execute when there is no connection.
 * @param request the request to execute.
 * @param refresh the function to refresh the token.
 * @param onError the function to execute when the request fails.
 * @param onSuccess the function to execute when the request succeeds.
 * @param context the context.
 */
fun <T> ViewModel.launchRequestRefreshing(
    noConnectionRequest: (suspend () -> Either<Problem, T>?)? = null,
    request: suspend () -> Either<Problem, T>,
    refresh: () -> Unit,
    onError: Problem.() -> Unit,
    onSuccess: Success<T>.() -> Unit,
    context: Context,
) {
    viewModelScope.launch {
        executeRequestRefreshing(
            noConnectionRequest = noConnectionRequest,
            request = request,
            refresh = refresh,
            onError = onError,
            onSuccess = onSuccess,
            context = context,
        )
    }
}

/**
 * Executes a request and handles the response.
 *
 * In case of a [Problem.TooManyRequestsProblem] the request is retried after a delay.
 *
 * @param T the expected type of the response.
 * @param noConnectionRequest the request to execute when there is no connection.
 * @param request the request to execute.
 * @param onError the function to execute when the request fails.
 * @param onSuccess the function to execute when the request succeeds.
 * @param context the context.
 */
suspend fun <T> executeRequest(
    noConnectionRequest: (suspend () -> Either<Problem, T>?)? = null,
    request: suspend () -> Either<Problem, T>,
    onError: Problem.() -> Unit,
    onSuccess: Success<T>.() -> Unit,
    context: Context,
) {
    val res =
        if (context.isNetworkAvailable()) {
            request()
        } else {
            noConnectionRequest?.let { it() }
        }

    if (res == null) {
        showErrorToast("No connection available", context)
        return
    }

    if (res.isTooManyRequests()) {
        delay(TOO_MANY_REQUESTS_DELAY)
        executeRequest(noConnectionRequest, request, onError, onSuccess, context)
    }

    when (res) {
        is Success -> onSuccess(res)
        is Failure -> onError(res.value)
    }
}

/**
 * Executes a request and handles the response.
 *
 * In case of a [Problem.TooManyRequestsProblem] the request is retried after a delay.
 *
 * In case of a [Problem.ServiceProblem] with status 401 the request is retried once after refreshing the token.
 *
 * @param T the expected type of the response.
 * @param noConnectionRequest the request to execute when there is no connection.
 * @param request the request to execute.
 * @param refresh the function to refresh the token.
 * @param onError the function to execute when the request fails.
 * @param onSuccess the function to execute when the request succeeds.
 * @param context the context.
 */
suspend fun <T> executeRequestRefreshing(
    noConnectionRequest: (suspend () -> Either<Problem, T>?)? = null,
    request: suspend () -> Either<Problem, T>,
    refresh: () -> Unit,
    onError: (Problem) -> Unit,
    onSuccess: Success<T>.() -> Unit,
    context: Context,
) {
    val res = if (context.isNetworkAvailable()) {
        request()
    } else {
        noConnectionRequest?.let { it() }
    }

    if (res == null) {
        showErrorToast("No connection available", context)
        return
    }

    if (res.isTooManyRequests()) {
        delay(TOO_MANY_REQUESTS_DELAY)
        executeRequestRefreshing(request, request, refresh, onError, onSuccess, context)
    }

    if (res.isUnauthorized()) {
        refresh()
        executeRequest(noConnectionRequest, request, onError, onSuccess, context)
    }

    when (res) {
        is Success -> onSuccess(res)
        is Failure -> onError(res.value)
    }
}

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

fun <T> Either<Problem, T>.isTooManyRequests(): Boolean {
    return when (this) {
        is Failure -> value is Problem.TooManyRequestsProblem
        else -> false
    }
}

fun <T> Either<Problem, T>.isUnauthorized(): Boolean {
    return when (this) {
        is Failure -> value is Problem.ServiceProblem && value.status == 401
        else -> false
    }
}
