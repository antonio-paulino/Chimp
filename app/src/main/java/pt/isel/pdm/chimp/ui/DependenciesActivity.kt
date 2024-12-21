package pt.isel.pdm.chimp.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import pt.isel.pdm.chimp.DependenciesContainer

open class DependenciesActivity : ComponentActivity() {
    lateinit var dependencies: DependenciesContainer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dependencies = (application as DependenciesContainer)
    }

    @Suppress("UNCHECKED_CAST")
    inline fun <reified T : ViewModel> initializeViewModel(
        crossinline constructor: (
            dependencies: DependenciesContainer,
        ) -> T,
    ): Lazy<T> {
        val factory =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return constructor(
                        dependencies,
                    ) as T
                }
            }
        return viewModels<T>(factoryProducer = { factory })
    }
}