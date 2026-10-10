package io.github.sreepv43.fablife.ui.mind

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import io.github.sreepv43.fablife.ui.AppState
import io.github.sreepv43.fablife.ui.AppViewModel
import io.github.sreepv43.fablife.data.JournalKind


/** Journal composer and history for [kind]. Stub: replaced by the audio/mind module. */
@Composable
fun JournalScreen(vm: AppViewModel, state: AppState, kind: JournalKind, habitId: String?, nav: NavHostController) =
    ToolStub("Journal", nav)
