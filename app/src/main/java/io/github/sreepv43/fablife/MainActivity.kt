package io.github.sreepv43.fablife

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.sreepv43.fablife.content.Ritual
import io.github.sreepv43.fablife.data.ThemeMode
import io.github.sreepv43.fablife.notifications.EXTRA_RITUAL
import io.github.sreepv43.fablife.ui.AppViewModel
import io.github.sreepv43.fablife.ui.FabLifeRoot
import io.github.sreepv43.fablife.ui.theme.FabTheme

class MainActivity : ComponentActivity() {
    private val vm: AppViewModel by viewModels { viewModelFactory { AppViewModel(it) } }
    private var openRitual by mutableStateOf<Ritual?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        if (savedInstanceState == null) handleIntent(intent)
        setContent {
            val prefs by vm.prefs.collectAsStateWithLifecycle()
            FabTheme(prefs?.theme ?: ThemeMode.SYSTEM) {
                FabLifeRoot(vm, openRitual, onRitualOpened = { openRitual = null })
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    override fun onResume() {
        super.onResume()
        vm.onResume()
    }

    /** Tapping a ritual reminder opens that ritual's player. */
    private fun handleIntent(intent: Intent?) {
        openRitual = intent?.getStringExtra(EXTRA_RITUAL)?.let { name -> Ritual.entries.firstOrNull { it.name == name } }
    }
}
