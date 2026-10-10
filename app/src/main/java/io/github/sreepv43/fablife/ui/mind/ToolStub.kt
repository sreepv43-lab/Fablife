package io.github.sreepv43.fablife.ui.mind

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

/** Temporary screen for tools that are not built yet. Every tool module replaces its own stub. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToolStub(title: String, nav: NavHostController) {
    Column(Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text(title) },
            navigationIcon = { IconButton(onClick = { nav.popBackStack() }) { Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back") } },
        )
        Text("This tool isn't available in this build yet.", modifier = Modifier.padding(24.dp))
    }
}
