package io.github.sreepv43.fablife.audio.android

import android.app.Application
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import io.github.sreepv43.fablife.AppContainer
import io.github.sreepv43.fablife.FabLifeApp
import io.github.sreepv43.fablife.audio.SoundId
import io.github.sreepv43.fablife.data.SessionKind
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowLooper

/** An app whose audio is a recording fake and whose timer clock the test moves by hand. */
class TestAudioApp : FabLifeApp() {
    val fake = FakeAudioController()

    @Volatile
    var clockMs = 1_000L

    override fun createContainer(): AppContainer = object : AppContainer(this) {
        override val audio: AudioController = fake
        override val clock: () -> Long = { clockMs }
    }
}

@RunWith(RobolectricTestRunner::class)
@Config(application = TestAudioApp::class)
class AudioServiceTest {
    private val app = ApplicationProvider.getApplicationContext<Application>() as TestAudioApp
    private val fake get() = app.fake

    private fun start(action: String? = null): Pair<org.robolectric.android.controller.ServiceController<AudioService>, AudioService> {
        val controller = Robolectric.buildService(AudioService::class.java, action?.let { Intent(app, AudioService::class.java).setAction(it) } ?: Intent(app, AudioService::class.java))
        val service = controller.create().startCommand(0, 1).get()
        return controller to service
    }

    @Test
    fun showsAForegroundNotificationWithTheTitleAndControlsWhilePlaying() {
        fake.playSound(SoundId.RAIN)
        val (_, service) = start()
        val notification = shadowOf(service).lastForegroundNotification
        assertNotNull(notification)
        assertEquals(AudioService.NOTIFICATION_ID, shadowOf(service).lastForegroundNotificationId)
        assertEquals("rain", notification.extras.getString("android.title"))
        assertEquals("Playing", notification.extras.getString("android.text"))
        assertEquals(listOf("Pause", "Stop"), notification.actions.map { it.title.toString() })
    }

    @Test
    fun theNotificationFollowsPauseAndResume() {
        fake.playSound(SoundId.RAIN)
        val (_, service) = start()
        fake.pause()
        ShadowLooper.idleMainLooper()
        val manager = shadowOf(app.getSystemService(android.app.NotificationManager::class.java))
        val paused = manager.getNotification(AudioService.NOTIFICATION_ID)
        assertEquals("Paused", paused.extras.getString("android.text"))
        assertEquals(listOf("Resume", "Stop"), paused.actions.map { it.title.toString() })
        assertFalse(shadowOf(service).isStoppedBySelf)
    }

    @Test
    fun theNotificationButtonsDriveTheController() {
        fake.playSound(SoundId.RAIN)
        val (_, service) = start()
        // The notification's buttons are PendingIntents that start the service with an action.
        val buttons = shadowOf(shadowOf(service).lastForegroundNotification.actions[0].actionIntent)
        assertTrue(buttons.isService)
        assertEquals(AudioService.ACTION_PAUSE, buttons.savedIntent.action)

        fun deliver(action: String) =
            Robolectric.buildService(AudioService::class.java, Intent(app, AudioService::class.java).setAction(action)).create().startCommand(0, 2)
        deliver(AudioService.ACTION_PAUSE)
        assertTrue(fake.calls.contains("pause"))
        deliver(AudioService.ACTION_RESUME)
        assertTrue(fake.calls.contains("resume"))
        deliver(AudioService.ACTION_STOP)
        assertTrue(fake.calls.contains("stop"))
    }

    @Test
    fun theServiceStopsItselfWhenPlaybackEnds() {
        fake.playSound(SoundId.RAIN)
        val (_, service) = start()
        assertFalse(shadowOf(service).isStoppedBySelf)
        fake.stop()
        ShadowLooper.idleMainLooper()
        assertTrue(shadowOf(service).isStoppedBySelf)
    }

    @Test
    fun startedWithNothingPlayingItStopsRightAway() {
        val (_, service) = start()
        assertTrue(shadowOf(service).isStoppedBySelf)
    }

    @Test
    fun finishedPlaybackStopsTheService() {
        fake.playSound(SoundId.RAIN, timerMinutes = 15)
        val (_, service) = start()
        fake.mutableState.value = fake.mutableState.value.copy(isPlaying = false, finished = true)
        ShadowLooper.idleMainLooper()
        assertTrue(shadowOf(service).isStoppedBySelf)
    }

    @Test
    fun finishedPlaybackIsLoggedAsTimeSpent() = runBlocking {
        fake.mutableEnded.emit(PlaybackEnded(PlaybackMode.SOUND, "rain", 12, completed = true))
        fake.mutableEnded.emit(PlaybackEnded(PlaybackMode.GUIDED, "body_scan", 10, completed = true))
        val deadline = System.nanoTime() + 5_000_000_000L
        while (app.container.database.sessionLogs().all().size < 2 && System.nanoTime() < deadline) {
            ShadowLooper.idleMainLooper()
            Thread.sleep(20)
        }
        val logs = app.container.database.sessionLogs().all().sortedBy { it.id }
        assertEquals(listOf(SessionKind.SOUND to "rain", SessionKind.GUIDED to "body_scan"), logs.map { it.kind to it.ref })
        assertEquals(listOf(12, 10), logs.map { it.minutes })
    }
}
