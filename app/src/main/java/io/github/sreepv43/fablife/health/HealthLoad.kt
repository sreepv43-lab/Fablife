package io.github.sreepv43.fablife.health

import kotlinx.coroutines.CancellationException
import java.time.LocalDate

/** What the Health screen and the Progress card can show. */
sealed interface HealthUi {
    data object Loading : HealthUi
    data object Unavailable : HealthUi
    data object NotInstalled : HealthUi
    data object NeedsPermission : HealthUi
    data class Ready(val summary: HealthSummary) : HealthUi
    data object Failed : HealthUi
}

const val HEALTH_DAYS = 7

/**
 * Works out which state to show. Never throws: Health Connect can disappear, be updated or lose a permission at any
 * moment, and none of that should be able to crash the screen.
 */
suspend fun loadHealth(repo: HealthRepository, today: LocalDate): HealthUi {
    return try {
        when (repo.availability()) {
            HealthAvailability.UNAVAILABLE -> HealthUi.Unavailable
            HealthAvailability.NOT_INSTALLED -> HealthUi.NotInstalled
            HealthAvailability.AVAILABLE ->
                if (!repo.hasPermissions()) HealthUi.NeedsPermission else HealthUi.Ready(repo.summary(HEALTH_DAYS, today))
        }
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        HealthUi.Failed
    }
}

/** "7 h 05 min", "45 min". */
fun formatSleep(minutes: Int): String = if (minutes < 60) "$minutes min" else "%d h %02d min".format(minutes / 60, minutes % 60)
