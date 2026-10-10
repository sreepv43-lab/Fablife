package io.github.sreepv43.fablife.audio.android

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import io.github.sreepv43.fablife.MainActivity
import io.github.sreepv43.fablife.R
import io.github.sreepv43.fablife.fabApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/**
 * Keeps playback alive with the screen off and shows pause/stop controls. It owns no audio itself: the
 * [AudioController] does, and this service just mirrors its state and stops when nothing is active.
 */
class AudioService : Service() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private lateinit var controller: AudioController
    private var foreground = false

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        controller = fabApp.container.audio
        createChannel(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_PAUSE -> controller.pause()
            ACTION_RESUME -> controller.resume()
            ACTION_STOP -> controller.stop()
        }
        val state = controller.state.value
        if (!state.active) {
            // Android requires startForeground after startForegroundService, even if we are about to stop.
            enterForeground(buildNotification(state.title ?: getString(R.string.app_name), playing = false))
            leave()
            return START_NOT_STICKY
        }
        enterForeground(buildNotification(state.title.orEmpty(), state.isPlaying))
        scope.launch {
            controller.state.map { Triple(it.active, it.title, it.isPlaying) }.distinctUntilChanged().collect { (active, title, playing) ->
                if (!active) leave() else notifyUpdate(buildNotification(title.orEmpty(), playing))
            }
        }
        return START_NOT_STICKY
    }

    private fun enterForeground(notification: Notification) {
        // Playback carries on without the service if Android refuses a foreground start, so this must not throw.
        runCatching {
            val type = if (Build.VERSION.SDK_INT >= 29) ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK else 0
            ServiceCompat.startForeground(this, NOTIFICATION_ID, notification, type)
            foreground = true
        }
    }

    private fun notifyUpdate(notification: Notification) {
        if (!foreground) return
        runCatching { getSystemService(NotificationManager::class.java).notify(NOTIFICATION_ID, notification) }
    }

    private fun leave() {
        scope.coroutineContext[Job]?.cancelChildren()
        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
        foreground = false
        stopSelf()
    }

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }

    private fun buildNotification(title: String, playing: Boolean): Notification {
        val open = PendingIntent.getActivity(
            this, 0, Intent(this, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        val toggle = if (playing) {
            NotificationCompat.Action(0, getString(R.string.audio_pause), action(ACTION_PAUSE))
        } else {
            NotificationCompat.Action(0, getString(R.string.audio_resume), action(ACTION_RESUME))
        }
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(getString(if (playing) R.string.audio_playing else R.string.audio_paused))
            .setContentIntent(open)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setSilent(true)
            .setCategory(NotificationCompat.CATEGORY_TRANSPORT)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .addAction(toggle)
            .addAction(0, getString(R.string.audio_stop), action(ACTION_STOP))
            .build()
    }

    private fun action(name: String): PendingIntent = PendingIntent.getService(
        this, name.hashCode(), Intent(this, AudioService::class.java).setAction(name),
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
    )

    companion object {
        const val CHANNEL_ID = "playback"
        const val NOTIFICATION_ID = 4001
        const val ACTION_PAUSE = "io.github.sreepv43.fablife.audio.PAUSE"
        const val ACTION_RESUME = "io.github.sreepv43.fablife.audio.RESUME"
        const val ACTION_STOP = "io.github.sreepv43.fablife.audio.STOP"

        fun createChannel(context: Context) {
            val channel = NotificationChannel(CHANNEL_ID, context.getString(R.string.channel_playback), NotificationManager.IMPORTANCE_LOW)
            channel.setSound(null, null)
            context.getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }
}
