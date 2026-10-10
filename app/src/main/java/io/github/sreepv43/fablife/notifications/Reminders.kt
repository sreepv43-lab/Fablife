package io.github.sreepv43.fablife.notifications

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import io.github.sreepv43.fablife.MainActivity
import io.github.sreepv43.fablife.R
import io.github.sreepv43.fablife.content.Ritual
import io.github.sreepv43.fablife.data.CheckInStatus
import io.github.sreepv43.fablife.data.FabDatabase
import io.github.sreepv43.fablife.data.RitualSettingsEntity
import io.github.sreepv43.fablife.data.StepState
import io.github.sreepv43.fablife.engine.JourneyRules
import io.github.sreepv43.fablife.engine.logicalDay
import io.github.sreepv43.fablife.fabApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

const val EXTRA_RITUAL = "ritual"
private const val CHANNEL_ID = "rituals"

/** Schedules one daily reminder per enabled ritual (at most one notification per ritual per day). */
class ReminderScheduler(private val context: Context) {
    private val alarms = context.getSystemService(AlarmManager::class.java)

    fun createChannel() {
        val channel = NotificationChannel(CHANNEL_ID, context.getString(R.string.channel_rituals), NotificationManager.IMPORTANCE_DEFAULT)
        context.getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    fun scheduleAll(settings: List<RitualSettingsEntity>) {
        for (ritual in Ritual.entries) {
            val pending = pendingIntent(ritual)
            alarms.cancel(pending)
            val s = settings.firstOrNull { it.ritual == ritual } ?: continue
            if (!s.reminderEnabled) continue
            var next = LocalDateTime.now().with(s.reminderTime).withSecond(0).withNano(0)
            if (!next.isAfter(LocalDateTime.now())) next = next.plusDays(1)
            val millis = next.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
            // Inexact but Doze-friendly; avoids the special exact-alarm permission.
            alarms.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, millis, pending)
        }
    }

    private fun pendingIntent(ritual: Ritual): PendingIntent = PendingIntent.getBroadcast(
        context, ritual.ordinal,
        Intent(context, ReminderReceiver::class.java).putExtra(EXTRA_RITUAL, ritual.name),
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
    )
}

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val ritual = intent.getStringExtra(EXTRA_RITUAL)?.let { Ritual.valueOf(it) } ?: return
        val app = context.fabApp
        val result = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                notifyIfNeeded(context, app.container.database, ritual, app.container.prefs.current().dayStartOffsetHours)
                app.container.rescheduleReminders()
            } finally {
                result.finish()
            }
        }
    }

    private suspend fun notifyIfNeeded(context: Context, db: FabDatabase, ritual: Ritual, offset: Int) {
        val habits = db.habits().all().filter { it.ritual == ritual }
        if (habits.isEmpty()) return
        val today = logicalDay(LocalDateTime.now(), offset)
        val doneToday = db.checkIns().all().filter { it.day == today && it.status == CheckInStatus.DONE }.map { it.habitId }.toSet()
        if (habits.all { it.id in doneToday }) return
        if (Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) return

        val text = reminderText(context, ritual, habits.size, db, today)
        val tap = PendingIntent.getActivity(
            context, 100 + ritual.ordinal,
            Intent(context, MainActivity::class.java).putExtra(EXTRA_RITUAL, ritual.name)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(context.getString(R.string.app_name))
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .setContentIntent(tap)
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(context).notify(ritual.ordinal, notification)
    }

    /** Morning reminders turn into a gentle nudge after 3 days without check-ins on the active challenge. */
    private suspend fun reminderText(context: Context, ritual: Ritual, count: Int, db: FabDatabase, today: LocalDate): String {
        if (ritual == Ritual.MORNING) {
            val active = db.steps().all().firstOrNull { it.state == StepState.ACTIVE }
            val doneDays = db.checkIns().all().filter { it.status == CheckInStatus.DONE }.map { it.day }.toSet()
            if (active?.activatedDay != null && JourneyRules.needsNudge(doneDays, active.activatedDay, today)) {
                return context.getString(R.string.reminder_nudge)
            }
        }
        val name = context.getString(
            when (ritual) {
                Ritual.MORNING -> R.string.ritual_morning
                Ritual.AFTERNOON -> R.string.ritual_afternoon
                Ritual.EVENING -> R.string.ritual_evening
            },
        ).lowercase()
        return context.resources.getQuantityString(R.plurals.reminder_ritual, count, name, count)
    }
}

/** Alarms are cleared on reboot and drift on time/zone changes, so reschedule them. */
class RescheduleReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val result = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                context.fabApp.container.rescheduleReminders()
            } finally {
                result.finish()
            }
        }
    }
}
