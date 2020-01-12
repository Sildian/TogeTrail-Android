package com.sildian.apps.togetrail.common.utils

import org.junit.Test

import org.junit.Assert.*
import java.util.*

class NumberUtilitiesTest {

    @Test
    fun given_1500p5_when_displayNumber_then_checkResultIs1500p5() {
        Locale.setDefault(Locale.US)
        assertEquals("1,500.5", NumberUtilities.displayNumber(1500.5, 1))
    }

    @Test
    fun given_1500m_when_displayNumberWithMetric_then_checkResultIs1500m() {
        Locale.setDefault(Locale.US)
        assertEquals("1,500 m",
            NumberUtilities.displayNumberWithMetric(1500.0, 0, "m"))
    }

    @Test
    fun given_1500mElevation_when_displayNumberWithMetricAndSuffix_then_checkResultIs1500mElevation() {
        Locale.setDefault(Locale.US)
        assertEquals("1,500 m\nElevation",
            NumberUtilities.displayNumberWithMetricAndSuffix(
                1500.0, 0, "m", "Elevation", true))
    }
}