package io.github.sreepv43.fablife.ui.mind

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import io.github.sreepv43.fablife.ui.AppState
import io.github.sreepv43.fablife.ui.AppViewModel


/** Sound player for [soundId], or the sound library when it is null. Stub: replaced by the audio/mind module. */
@Composable
fun SoundScreen(vm: AppViewModel, state: AppState, soundId: String?, habitId: String?, nav: NavHostController) =
    ToolStub("Sounds", nav)
