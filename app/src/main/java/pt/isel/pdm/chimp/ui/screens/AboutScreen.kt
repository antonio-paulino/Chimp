package pt.isel.pdm.chimp.ui.screens

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(navController: NavController) {
    val context = LocalContext.current
    val groupMembers = listOf(
        GroupMember(
            "50512",
            "António Paulino",
            "https://github.com/antonio-paulino",
            "50512@alunos.isel.pt"
        ),
        GroupMember(
            "50493",
            "Bernardo Pereira",
            "https://github.com/BernardoPe",
            "50493@alunos.isel.pt"
        )
    )
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "About") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
        ) {
            groupMembers.forEach { member ->
                Text(text = member.toString())
            }
            Button(
                onClick = {
                    composeEmail(
                        context,
                        groupMembers.map { it.email }.toTypedArray(),
                        "ChIMP",
                        "Hello, we are ChIMP"
                    )
                },
                modifier = Modifier.padding(16.dp)
            ) {
                Text(text = "Send Email")
            }
        }
    }
}


fun composeEmail(context: Context, addresses: Array<String>, subject: String, text: String) {
    val intent = Intent(
        Intent.ACTION_SEND
    ).apply {
        type = "*/*"
        putExtra(Intent.EXTRA_EMAIL, addresses)
        putExtra(Intent.EXTRA_SUBJECT, subject)
        putExtra(Intent.EXTRA_TEXT, text)
    }
    context.startActivity(intent)
}