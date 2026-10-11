package io.github.sreepv43.fablife.ui

import org.junit.Test

/** The on-device coach note appears on Today and says where it came from. */
class CoachUiTest : MindUiTestBase() {
    @Test
    fun todayShowsAnOnDeviceCoachNote() {
        waitFor("Coach note")
        waitFor("Written on your phone from your progress")
    }
}
