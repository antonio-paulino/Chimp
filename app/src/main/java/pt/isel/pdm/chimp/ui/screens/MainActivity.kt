package pt.isel.pdm.chimp.ui.screens

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import pt.isel.pdm.chimp.DependenciesContainer
import pt.isel.pdm.chimp.infrastructure.EntityReferenceManager
import pt.isel.pdm.chimp.infrastructure.services.interfaces.ChimpService
import pt.isel.pdm.chimp.infrastructure.session.SessionManager
import pt.isel.pdm.chimp.ui.navigation.navigateTo
import pt.isel.pdm.chimp.ui.screens.about.AboutActivity
import pt.isel.pdm.chimp.ui.theme.ChIMPTheme

private const val TAG = "ChIMP-MainActivity"

open class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.v(TAG, "MainActivity.onCreate")
        enableEdgeToEdge()
        setContent {
            ChIMPTheme {
                MainScreen(
                    onAboutNavigation = { navigateTo(AboutActivity::class.java) },
                )
            }
        }
    }

    inline fun <reified T : ViewModel> initializeViewModel(
        crossinline constructor: (
            chimpService: ChimpService,
            sessionManager: SessionManager,
            entityReferenceManager: EntityReferenceManager,
        ) -> T,
    ): Lazy<T> {
        val dependencies = application as DependenciesContainer
        val factory =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return constructor(
                        dependencies.chimpService,
                        dependencies.sessionManager,
                        dependencies.entityReferenceManager,
                    ) as T
                }
            }
        return viewModels<T>(factoryProducer = { factory })
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
