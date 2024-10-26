package pt.isel.pdm.chimp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import pt.isel.pdm.chimp.ui.theme.ChIMPTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onAboutNavigation: () -> Unit
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ChIMPTheme {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                Column (
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(240.dp)
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box (
                        modifier = Modifier
                            .height(100.dp)
                            .fillMaxWidth()
                            .padding(2.dp)
                    ) {
                        Text(
                            text = "ChIMP",
                            style = TextStyle(
                                fontSize = MaterialTheme.typography.titleLarge.fontSize,
                                color = MaterialTheme.colorScheme.onSurface
                            ),
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))
                    TileDrawerContent("About", Icons.Filled.Info) { onAboutNavigation() }
                }
            },
            scrimColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.32f)
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text(text = "ChIMP", color = MaterialTheme.colorScheme.onPrimary) },
                        navigationIcon = {
                            IconButton(
                                onClick = {
                                    scope.launch {
                                        if (drawerState.isClosed) drawerState.open() else drawerState.close()
                                    } },
                                content = {
                                    Icon(
                                        imageVector = Icons.Filled.Menu,
                                        contentDescription = "Menu",
                                        tint = MaterialTheme.colorScheme.onPrimary
                                    )
                                }
                            )
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                        ),
                    )
                },
                modifier = Modifier.fillMaxSize()
            ) { innerPadding ->
                Text(
                    text = "Hello, ChIMP!",
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}


@Composable
fun TileDrawerContent(title:String, icon: ImageVector, onClick:()->Unit){
    Box(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(16.dp),
    ){
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = title,
            fontSize = MaterialTheme.typography.bodyLarge.fontSize,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(start = 30.dp)
        )
    }
}