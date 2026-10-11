package io.github.sreepv43.fablife.health

import android.content.Context
import androidx.activity.result.contract.ActivityResultContract
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.SleepSessionRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.request.AggregateGroupByPeriodRequest
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Period
import java.time.ZoneId

/** Health Connect on the device. Only called when [availability] is [HealthAvailability.AVAILABLE]. */
class HealthConnectRepository(private val context: Context) : HealthRepository {
    private val client by lazy { HealthConnectClient.getOrCreate(context) }

    override fun availability(): HealthAvailability = when (HealthConnectClient.getSdkStatus(context)) {
        HealthConnectClient.SDK_AVAILABLE -> HealthAvailability.AVAILABLE
        HealthConnectClient.SDK_UNAVAILABLE_PROVIDER_UPDATE_REQUIRED -> HealthAvailability.NOT_INSTALLED
        else -> HealthAvailability.UNAVAILABLE
    }

    override val requiredPermissions: Set<String> = setOf(
        HealthPermission.getReadPermission(StepsRecord::class),
        HealthPermission.getReadPermission(SleepSessionRecord::class),
    )

    override val permissionContract: ActivityResultContract<Set<String>, Set<String>> =
        PermissionController.createRequestPermissionResultContract()

    override suspend fun hasPermissions(): Boolean = client.permissionController.getGrantedPermissions().containsAll(requiredPermissions)

    override suspend fun summary(days: Int, today: LocalDate, zone: ZoneId): HealthSummary {
        val first = today.minusDays(days - 1L)
        val steps = stepsByDay(first, today)
        // Sleep that ends on the first day started the evening before, so read from the day before.
        val sleepStart = first.minusDays(1).atStartOfDay(zone).toInstant()
        val sleepEnd = today.plusDays(1).atStartOfDay(zone).toInstant()
        val sessions = readAllSleep(sleepStart, sleepEnd).map(::toRaw)
        val nights = SleepMath.perDay(sessions, zone).associateBy { it.day }
        return HealthSummary(
            (0 until days).map { i ->
                val day = first.plusDays(i.toLong())
                DayHealth(day, steps[day], nights[day]?.nightMinutes?.takeIf { it > 0 })
            },
        )
    }

    /** Steps per day via Health Connect's own aggregation, which already removes duplicates from overlapping sources. */
    private suspend fun stepsByDay(first: LocalDate, today: LocalDate): Map<LocalDate, Long> {
        val result = client.aggregateGroupByPeriod(
            AggregateGroupByPeriodRequest(
                metrics = setOf(StepsRecord.COUNT_TOTAL),
                timeRangeFilter = TimeRangeFilter.between(first.atStartOfDay(), LocalDateTime.of(today, java.time.LocalTime.MAX)),
                timeRangeSlicer = Period.ofDays(1),
            ),
        )
        return result.mapNotNull { r ->
            r.result[StepsRecord.COUNT_TOTAL]?.let { count -> r.startTime.toLocalDate() to count }
        }.toMap()
    }

    private suspend fun readAllSleep(start: java.time.Instant, end: java.time.Instant): List<SleepSessionRecord> {
        val out = ArrayList<SleepSessionRecord>()
        var token: String? = null
        do {
            val response = client.readRecords(ReadRecordsRequest(SleepSessionRecord::class, TimeRangeFilter.between(start, end), pageToken = token))
            out += response.records
            token = response.pageToken?.takeIf { it.isNotEmpty() && response.records.isNotEmpty() }
        } while (token != null)
        return out
    }

    companion object {
        fun toRaw(record: SleepSessionRecord) = RawSleepSession(
            start = record.startTime,
            end = record.endTime,
            stages = record.stages.map { RawStage(it.startTime, it.endTime, stageType(it.stage)) },
        )

        fun stageType(stage: Int): SleepStageType = when (stage) {
            SleepSessionRecord.STAGE_TYPE_AWAKE, SleepSessionRecord.STAGE_TYPE_AWAKE_IN_BED, SleepSessionRecord.STAGE_TYPE_OUT_OF_BED -> SleepStageType.AWAKE
            SleepSessionRecord.STAGE_TYPE_SLEEPING -> SleepStageType.SLEEPING
            SleepSessionRecord.STAGE_TYPE_LIGHT -> SleepStageType.LIGHT
            SleepSessionRecord.STAGE_TYPE_DEEP -> SleepStageType.DEEP
            SleepSessionRecord.STAGE_TYPE_REM -> SleepStageType.REM
            else -> SleepStageType.UNKNOWN
        }
    }
}
