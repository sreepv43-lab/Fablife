package io.github.sreepv43.fablife

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import io.github.sreepv43.fablife.audio.SoundId
import io.github.sreepv43.fablife.audio.android.AndroidFocusGate
import io.github.sreepv43.fablife.audio.android.AndroidNoisyGate
import io.github.sreepv43.fablife.audio.android.AndroidServiceGate
import io.github.sreepv43.fablife.audio.android.AndroidTtsEngine
import io.github.sreepv43.fablife.audio.android.AndroidWakeLockGate
import io.github.sreepv43.fablife.audio.android.AudioController
import io.github.sreepv43.fablife.audio.android.AudioControllerImpl
import io.github.sreepv43.fablife.audio.android.AudioEngine
import io.github.sreepv43.fablife.audio.android.AudioTrackOutput
import io.github.sreepv43.fablife.audio.android.PlaybackMode
import io.github.sreepv43.fablife.audio.android.SpeechPlayer
import io.github.sreepv43.fablife.audio.android.SpeechVoice
import io.github.sreepv43.fablife.data.ContentRepository
import io.github.sreepv43.fablife.data.FabDatabase
import io.github.sreepv43.fablife.data.FabRepository
import io.github.sreepv43.fablife.data.PrefsRepository
import io.github.sreepv43.fablife.data.SessionKind
import io.github.sreepv43.fablife.notifications.ReminderScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Hand-wired dependencies. `open` so tests can subclass it and supply fakes (see `FabLifeApp.createContainer`);
 * feature modules expose their entry points as `open val`s for the same reason.
 */
open class AppContainer(private val context: Context) {
    /** Lives as long as the process; the work here is small and must not be tied to a screen. */
    protected val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    val database = FabDatabase.create(context)
    val prefs = PrefsRepository(context)
    val content = ContentRepository(context)
    val reminders = ReminderScheduler(context)
    val repository = FabRepository(database, prefs, content) { rescheduleReminders() }

    /** Generated sounds and guided sessions. Open so tests can supply a fake. */
    open val audio: AudioController by lazy {
        AudioControllerImpl(
            scope = appScope,
            player = AudioEngine({ AudioTrackOutput() }),
            voiceFactory = { SpeechVoice(SpeechPlayer(AndroidTtsEngine(context.applicationContext))) },
            focus = AndroidFocusGate(context),
            wakeLock = AndroidWakeLockGate(context),
            noisy = AndroidNoisyGate(context),
            service = AndroidServiceGate(context),
            soundTitle = { id -> content.audio.sounds.firstOrNull { it.id == id.key }?.name ?: id.key },
        )
    }

    /** Called once at start-up: records every finished or stopped playback as time spent. */
    fun startBackgroundWork() {
        appScope.launch {
            audio.ended.collect { e ->
                val kind = if (e.mode == PlaybackMode.GUIDED) SessionKind.GUIDED else SessionKind.SOUND
                repository.logSession(kind, e.ref, e.minutes)
            }
        }
    }

    suspend fun rescheduleReminders() {
        val enabled = prefs.current().onboardingCompleted
        reminders.scheduleAll(if (enabled) database.ritualSettings().all() else emptyList())
    }
}

open class FabLifeApp : Application() {
    lateinit var container: AppContainer
        private set

    /** Tests override this (via `@Config(application = ...)`) to inject fakes. */
    protected open fun createContainer(): AppContainer = AppContainer(this)

    override fun onCreate() {
        super.onCreate()
        container = createContainer()
        container.reminders.createChannel()
        container.startBackgroundWork()
    }
}

val Context.fabApp: FabLifeApp get() = applicationContext as FabLifeApp

/** Factory for ViewModels that need the [AppContainer]. */
fun <VM : ViewModel> viewModelFactory(create: (AppContainer) -> VM) = object : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        val app = extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as FabLifeApp
        return create(app.container) as T
    }
}
