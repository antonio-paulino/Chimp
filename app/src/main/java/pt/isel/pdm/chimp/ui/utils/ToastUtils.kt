package pt.isel.pdm.chimp.ui.utils

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import pt.isel.pdm.chimp.ChimpApplication.Companion.TAG
import pt.isel.pdm.chimp.R

@SuppressLint("InflateParams")
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
fun showErrorToast(
    message: String,
    context: Context,
) {
    val inflater = LayoutInflater.from(context)
    val layout: View = inflater.inflate(R.layout.error_toast, null)

    Log.e(TAG, "Failed to open URL: $message")
    val textView = layout.findViewById<TextView>(R.id.toast_message)
    textView.text = message

    val toast = Toast(context)
    toast.duration = Toast.LENGTH_SHORT
    toast.view = layout
    toast.show()
}
