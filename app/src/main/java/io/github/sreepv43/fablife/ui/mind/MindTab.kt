package io.github.sreepv43.fablife.ui.mind

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import io.github.sreepv43.fablife.ui.AppState
import io.github.sreepv43.fablife.ui.AppViewModel
import io.github.sreepv43.fablife.ui.Routes

/** Explore → Mind. Stub list of entry points: replaced by the audio/mind module. */
@Composable
fun MindTab(vm: AppViewModel, state: AppState, nav: NavHostController) {
    val entries = listOf(
        "Focus timer" to Routes.focus(),
        "Journal" to Routes.journal(),
        "Top 3 for today" to Routes.top3(),
        "Sounds" to Routes.SOUNDS,
    )
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        items(entries.size) { i ->
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                modifier = Modifier.fillMaxWidth().widthIn(max = 640.dp).clickable(role = Role.Button) { nav.navigate(entries[i].second) },
            ) { Column(Modifier.padding(16.dp)) { Text(entries[i].first, style = MaterialTheme.typography.titleMedium) } }
        }
    }
}
