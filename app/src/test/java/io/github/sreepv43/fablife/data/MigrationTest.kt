package io.github.sreepv43.fablife.data

import androidx.room.testing.MigrationTestHelper
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/** Installs from before phase 2 (database version 1) must upgrade without losing anything. */
@RunWith(RobolectricTestRunner::class)
class MigrationTest {
    @get:Rule
    val helper = MigrationTestHelper(InstrumentationRegistry.getInstrumentation(), FabDatabase::class.java)

    @Test
    fun migrating1To2KeepsDataAndAddsTheNewTables() {
        helper.createDatabase("migration-test", 1).apply {
            execSQL(
                "INSERT INTO habits (id, title, description, icon, ritual, `order`, durationSeconds, smallerVersion, useSmaller, sourceStepId, createdAtMillis) " +
                    "VALUES ('morning_movement', 'Move for 5 minutes', 'Stretch', 'movement', 'MORNING', 3, 300, 'Move for 1 minute', 0, 'energy.move', 1000)",
            )
            execSQL("INSERT INTO check_ins (habitId, day, status, timestampMillis) VALUES ('morning_movement', 20000, 'DONE', 2000)")
            execSQL("INSERT INTO step_progress (stepId, journeyId, state, letterRead, activatedDay, completedDay) VALUES ('energy.move', 'energy', 'ACTIVE', 1, 20000, NULL)")
            close()
        }

        val db = helper.runMigrationsAndValidate("migration-test", 2, true)

        db.query("SELECT id, title, toolType, toolRef FROM habits").use { c ->
            assertTrue(c.moveToFirst())
            assertEquals("morning_movement", c.getString(0))
            assertEquals("Move for 5 minutes", c.getString(1))
            assertTrue("old habits have no tool until the app syncs it", c.isNull(2) && c.isNull(3))
        }
        db.query("SELECT COUNT(*) FROM check_ins").use { c -> c.moveToFirst(); assertEquals(1, c.getInt(0)) }
        db.query("SELECT state FROM step_progress WHERE stepId = 'energy.move'").use { c -> c.moveToFirst(); assertEquals("ACTIVE", c.getString(0)) }
        for (table in listOf("journal_entries", "top_tasks", "session_logs", "coach_notes")) {
            db.query("SELECT COUNT(*) FROM $table").use { c -> c.moveToFirst(); assertEquals("$table starts empty", 0, c.getInt(0)) }
        }
    }
}
