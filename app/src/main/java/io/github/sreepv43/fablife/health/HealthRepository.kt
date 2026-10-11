package io.github.sreepv43.fablife.health

import androidx.activity.result.contract.ActivityResultContract
import java.time.LocalDate
import java.time.ZoneId

/**
 * Read-only access to steps and sleep. Everything is read on demand and kept on the device: nothing here is stored by
 * the app, uploaded, or shared with anyone.
 */
interface HealthRepository {
    fun availability(): HealthAvailability

    /** The permission names to ask for. */
    val requiredPermissions: Set<String>

    /** Asks the system for [requiredPermissions]; the result is the set that was granted. */
    val permissionContract: ActivityResultContract<Set<String>, Set<String>>

    suspend fun hasPermissions(): Boolean

    /** The last [days] days up to and including [today] (oldest first). */
    suspend fun summary(days: Int, today: LocalDate, zone: ZoneId = ZoneId.systemDefault()): HealthSummary
}
