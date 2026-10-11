package io.github.sreepv43.fablife.health

import androidx.health.connect.client.records.SleepSessionRecord
import androidx.health.connect.client.records.metadata.Metadata
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant
import java.time.ZoneOffset

class HealthConnectMappingTest {
    private val t0 = Instant.parse("2026-10-10T00:00:00Z")

    @Test
    fun everyHealthConnectStageMapsToTheRightKind() {
        val expected = mapOf(
            SleepSessionRecord.STAGE_TYPE_AWAKE to SleepStageType.AWAKE,
            SleepSessionRecord.STAGE_TYPE_AWAKE_IN_BED to SleepStageType.AWAKE,
            SleepSessionRecord.STAGE_TYPE_OUT_OF_BED to SleepStageType.AWAKE,
            SleepSessionRecord.STAGE_TYPE_SLEEPING to SleepStageType.SLEEPING,
            SleepSessionRecord.STAGE_TYPE_LIGHT to SleepStageType.LIGHT,
            SleepSessionRecord.STAGE_TYPE_DEEP to SleepStageType.DEEP,
            SleepSessionRecord.STAGE_TYPE_REM to SleepStageType.REM,
            SleepSessionRecord.STAGE_TYPE_UNKNOWN to SleepStageType.UNKNOWN,
        )
        expected.forEach { (hc, ours) -> assertEquals(ours, HealthConnectRepository.stageType(hc)) }
        assertEquals(SleepStageType.UNKNOWN, HealthConnectRepository.stageType(999))
    }

    @Test
    fun aRecordKeepsItsTimesAndStages() {
        val record = SleepSessionRecord(
            startTime = t0, startZoneOffset = ZoneOffset.UTC, endTime = t0.plusSeconds(3600), endZoneOffset = ZoneOffset.UTC,
            stages = listOf(
                SleepSessionRecord.Stage(t0, t0.plusSeconds(1800), SleepSessionRecord.STAGE_TYPE_LIGHT),
                SleepSessionRecord.Stage(t0.plusSeconds(1800), t0.plusSeconds(3600), SleepSessionRecord.STAGE_TYPE_AWAKE),
            ),
            metadata = Metadata.manualEntry(),
        )
        val raw = HealthConnectRepository.toRaw(record)
        assertEquals(t0, raw.start)
        assertEquals(t0.plusSeconds(3600), raw.end)
        assertEquals(listOf(SleepStageType.LIGHT, SleepStageType.AWAKE), raw.stages.map { it.type })
    }

    @Test
    fun aRecordWithoutStagesHasNone() {
        val record = SleepSessionRecord(t0, ZoneOffset.UTC, t0.plusSeconds(600), ZoneOffset.UTC, metadata = Metadata.manualEntry())
        assertTrue(HealthConnectRepository.toRaw(record).stages.isEmpty())
    }
}
