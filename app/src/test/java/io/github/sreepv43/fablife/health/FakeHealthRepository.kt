package io.github.sreepv43.fablife.health

import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import java.time.LocalDate
import java.time.ZoneId

/** A Health Connect stand-in whose state the test sets. The permission contract answers at once, with no system dialog. */
class FakeHealthRepository : HealthRepository {
    var availability = HealthAvailability.AVAILABLE
    var granted = false
    var failWith: Exception? = null
    var days: Map<LocalDate, DayHealth> = emptyMap()
    var permissionRequests = 0

    override fun availability() = availability

    override val requiredPermissions = setOf("steps", "sleep")

    override val permissionContract = object : ActivityResultContract<Set<String>, Set<String>>() {
        override fun createIntent(context: Context, input: Set<String>) = Intent()
        override fun parseResult(resultCode: Int, intent: Intent?): Set<String> = if (granted) requiredPermissions else emptySet()
        override fun getSynchronousResult(context: Context, input: Set<String>): SynchronousResult<Set<String>> {
            permissionRequests++
            granted = true
            return SynchronousResult(requiredPermissions)
        }
    }

    override suspend fun hasPermissions(): Boolean {
        failWith?.let { throw it }
        return granted
    }

    override suspend fun summary(days: Int, today: LocalDate, zone: ZoneId): HealthSummary {
        failWith?.let { throw it }
        return HealthSummary((0 until days).map { i ->
            val day = today.minusDays(days - 1L - i)
            this.days[day] ?: DayHealth(day, null, null)
        })
    }
}
