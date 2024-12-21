package pt.isel.pdm.chimp.ui.utils

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarVisuals
import pt.isel.pdm.chimp.ChimpApplication
import pt.isel.pdm.chimp.ChimpApplication.Companion.TAG
import pt.isel.pdm.chimp.R
import pt.isel.pdm.chimp.infrastructure.services.media.problems.Problem

class SnackBarVisuals(
    override val message: String,
) : SnackbarVisuals {
    override val actionLabel: String?
        get() = null

    override val duration: SnackbarDuration = SnackbarDuration.Short

    override val withDismissAction: Boolean
        get() = true
}

@SuppressLint("InflateParams")
@Suppress("DEPRECATION")
fun showSuccessToast(
    message: String,
    context: Context,
) {
    val inflater = LayoutInflater.from(context)
    val layout: View = inflater.inflate(R.layout.success_toast, null)

    val textView = layout.findViewById<TextView>(R.id.toast_message)
    textView.text = message

    with(Toast(context)) {
        duration = Toast.LENGTH_SHORT
        view = layout
        show()
    }
}

@SuppressLint("InflateParams")
@Suppress("DEPRECATION")
fun showErrorToast(message: String) {
    val context = ChimpApplication.applicationContext()
    val inflater = LayoutInflater.from(context)
    val layout: View = inflater.inflate(R.layout.error_toast, null)

    Log.e(TAG, "Error: $message")
    val textView = layout.findViewById<TextView>(R.id.toast_message)
    textView.text = message

    val toast = Toast(context)
    toast.duration = Toast.LENGTH_SHORT
    toast.view = layout
    toast.show()
}

fun Problem.getMessage(): String {
    return when (this) {
        is Problem.InputValidationProblem -> this.detail
        is Problem.ServiceProblem -> this.detail
        is Problem.TooManyRequestsProblem -> "Too many requests"
        is Problem.UnexpectedProblem -> "Unexpected problem"
        is Problem.NoConnection -> "No connection available"
    }
}
