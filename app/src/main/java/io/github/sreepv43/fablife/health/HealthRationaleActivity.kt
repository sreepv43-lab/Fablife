package io.github.sreepv43.fablife.health

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.sreepv43.fablife.data.ThemeMode
import io.github.sreepv43.fablife.ui.theme.FabTheme

/** Shown when Health Connect asks how Fab Life uses the data it was given access to. */
class HealthRationaleActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FabTheme(ThemeMode.SYSTEM) {
                Surface(Modifier.fillMaxSize()) {
                    Column(Modifier.verticalScroll(rememberScrollState()).padding(24.dp)) {
                        Text("How Fab Life uses your health data", style = MaterialTheme.typography.headlineSmall)
                        Text("What it reads", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 16.dp))
                        Text("Your step count and your sleep sessions, only when you allow it. It never writes or changes anything in Health Connect.")
                        Text("What it does with it", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 16.dp))
                        Text(
                            "Shows today's steps and last night's sleep on your phone. The numbers are not saved by Fab Life, not uploaded anywhere, " +
                                "and not shared with anyone, including the optional AI coach notes.",
                        )
                        Text("You are in control", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 16.dp))
                        Text("You can remove access at any time in Health Connect settings. Fab Life works fully without it. This is information, not medical advice.")
                        Button(onClick = { finish() }, modifier = Modifier.padding(top = 24.dp)) { Text("Got it") }
                    }
                }
            }
        }
    }
}
