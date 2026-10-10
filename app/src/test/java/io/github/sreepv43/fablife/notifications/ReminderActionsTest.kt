package io.github.sreepv43.fablife.notifications

import android.app.AlarmManager
import androidx.test.core.app.ApplicationProvider
import io.github.sreepv43.fablife.FabLifeApp
import io.github.sreepv43.fablife.content.Ritual
import io.github.sreepv43.fablife.data.CheckInStatus
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf

@RunWith(RobolectricTestRunner::class)
class ReminderActionsTest {
    private val app = ApplicationProvider.getApplicationContext<FabLifeApp>()
    private val container get() = app.container

    @Test
    fun doneChecksInTheOnlyHabitOfARitual() = runBlocking {
        container.repository.addCustomHabit("Stretch", Ritual.EVENING)
        handleReminderAction(app, container, ACTION_DONE, Ritual.EVENING)

        val checkIns = container.database.checkIns().all()
        assertEquals(1, checkIns.size)
        assertEquals(CheckInStatus.DONE, checkIns.single().status)
        assertEquals(container.repository.today(), checkIns.single().day)
    }

    @Test
    fun doneDoesNothingWhenTheRitualHasSeveralHabits() = runBlocking {
        container.repository.addCustomHabit("Stretch", Ritual.EVENING)
        container.repository.addCustomHabit("Read", Ritual.EVENING)
        handleReminderAction(app, container, ACTION_DONE, Ritual.EVENING)

        assertTrue(container.database.checkIns().all().isEmpty())
    }

    @Test
    fun snoozeSchedulesAReminderThirtyMinutesAhead() = runBlocking {
        val alarms = shadowOf(app.getSystemService(AlarmManager::class.java))
        val before = System.currentTimeMillis()
        handleReminderAction(app, container, ACTION_SNOOZE, Ritual.MORNING)

        val alarm = alarms.peekNextScheduledAlarm()
        assertNotNull(alarm)
        val delayMinutes = (alarm!!.triggerAtMs - before) / 60_000
        assertTrue("snooze was $delayMinutes min", delayMinutes in SNOOZE_MINUTES - 1..SNOOZE_MINUTES + 1)
    }
}
