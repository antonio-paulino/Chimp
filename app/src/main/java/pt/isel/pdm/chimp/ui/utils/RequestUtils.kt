package pt.isel.pdm.chimp.ui.utils

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pt.isel.pdm.chimp.ChimpApplication
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
 */
fun <T> ViewModel.launchRequest(
    noConnectionRequest: (suspend () -> Either<Problem, T>?)? = null,
    request: suspend () -> Either<Problem, T>,
    onError: suspend (Problem) -> Unit,
    onSuccess: suspend (Success<T>) -> Unit,
): Job {
    return viewModelScope.launch {
        executeRequest(
            noConnectionRequest = noConnectionRequest,
            request = request,
            onError = onError,
            onSuccess = onSuccess,
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
 */
fun <T> ViewModel.launchRequestRefreshing(
    noConnectionRequest: (suspend () -> Either<Problem, T>?)? = null,
    request: suspend () -> Either<Problem, T>,
    refresh: suspend () -> Unit,
    onError: suspend(Problem) -> Unit,
    onSuccess: suspend (Success<T>) -> Unit,
): Job {
    return viewModelScope.launch {
        executeRequestRefreshing(
            noConnectionRequest = noConnectionRequest,
            request = request,
            refresh = refresh,
            onError = onError,
            onSuccess = onSuccess,
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
 */
suspend fun <T> executeRequest(
    noConnectionRequest: (suspend () -> Either<Problem, T>?)? = null,
    request: suspend () -> Either<Problem, T>,
    onError: suspend(Problem) -> Unit,
    onSuccess: suspend(Success<T>) -> Unit,
) {
    val context = ChimpApplication.applicationContext()
    val res =
        if (context.isNetworkAvailable()) {
            request()
        } else {
            noConnectionRequest?.let { it() }
        }

    if (res == null) {
        showErrorToast("No connection available")
        return
    }

    if (res.isTooManyRequests()) {
        delay(TOO_MANY_REQUESTS_DELAY)
        executeRequest(noConnectionRequest, request, onError, onSuccess)
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
 */
suspend fun <T> executeRequestRefreshing(
    noConnectionRequest: (suspend () -> Either<Problem, T>?)? = null,
    request: suspend () -> Either<Problem, T>,
    refresh: (suspend() -> Unit)? = null,
    onError: suspend(Problem) -> Unit,
    onSuccess: suspend (Success<T>) -> Unit,
) {
    val context = ChimpApplication.applicationContext()
    val res =
        if (context.isNetworkAvailable()) {
            if (refresh != null) {
                refresh()
            }
            request()
        } else {
            noConnectionRequest?.let { it() }
        }

    if (res == null) {
        showErrorToast("No connection available")
        return
    }

    if (res.isTooManyRequests()) {
        delay(TOO_MANY_REQUESTS_DELAY)
        executeRequest(request, request, onError, onSuccess)
    }

    when (res) {
        is Success -> onSuccess(res)
        is Failure -> onError(res.value)
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

fun List<Either<Problem, *>>.allSuccessful(): Boolean {
    return all { it is Success }
}

fun List<TextFieldValue>.allValid(): Boolean {
    return all { it.text.isNotEmpty() }
}
