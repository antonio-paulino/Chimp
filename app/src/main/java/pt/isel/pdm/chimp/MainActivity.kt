package pt.isel.pdm.chimp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import pt.isel.pdm.chimp.ui.navigation.AppNavigation
import pt.isel.pdm.chimp.ui.theme.ChIMPTheme

private const val TAG = "ChIMP-MainActivity"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.v(TAG, "MainActivity.onCreate")
        enableEdgeToEdge()
        setContent {
            ChIMPTheme {
                AppNavigation()
            }
        }
    }
    override fun onStart() {
        super.onStart()
        Log.v(TAG, "MainActivity.onStart")
    }

    override fun onStop() {
        super.onStop()
        Log.v(TAG, "MainActivity.onStop")
    }
    override fun onDestroy() {
        super.onDestroy()
        Log.v(TAG, "MainActivity.onDestroy")
    }
}

