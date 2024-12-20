package pt.isel.pdm.chimp.ui.screens.home.createChannel

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import pt.isel.pdm.chimp.R
import pt.isel.pdm.chimp.domain.channel.ChannelRole
import pt.isel.pdm.chimp.domain.wrappers.name.Name
import pt.isel.pdm.chimp.ui.components.LoadingSpinner
import pt.isel.pdm.chimp.ui.components.TopBar
import pt.isel.pdm.chimp.ui.components.buttons.BackButton
import pt.isel.pdm.chimp.ui.screens.home.createChannel.views.ChannelForm
import pt.isel.pdm.chimp.ui.utils.SnackBarVisuals
import pt.isel.pdm.chimp.ui.utils.getMessage

@Composable
fun CreateChannelScreen(
    state: CreateChannelScreenState,
    onCreateChannel: (Name, Boolean, ChannelRole) -> Unit,
    onBack: () -> Unit,
) {
    val snackBarHostState = remember { SnackbarHostState() }
    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) },
        topBar = {
            TopBar(
                content = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        BackButton(onBack)
                        Text(stringResource(id = R.string.create_channel))
                    }
                },
            )
        },
    ) { innerPadding ->
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize().padding(innerPadding).background(Color.Transparent),
        ) {
            when (state) {
                is CreateChannelScreenState.CreatingChannel -> {
                    ChannelForm(
                        initialName = state.name,
                        initialIsPublic = state.isPublic,
                        initialRole = state.defaultRole,
                        onSubmit = onCreateChannel,
                    )
                }
                is CreateChannelScreenState.CreatingChannelError -> {
                    ChannelForm(
                        initialName = state.name,
                        initialIsPublic = state.isPublic,
                        initialRole = state.defaultRole,
                        onSubmit = onCreateChannel,
                    )
                }
                is CreateChannelScreenState.Loading -> {
                    LoadingSpinner(stringResource(id = R.string.creating_channel))
                }
                CreateChannelScreenState.Success -> onBack()
            }
        }
    }

    LaunchedEffect(state) {
        if (state is CreateChannelScreenState.CreatingChannelError) {
            snackBarHostState.showSnackbar(
                SnackBarVisuals(
                    message = state.problem.getMessage(),
                ),
            )
        }
    }
}
