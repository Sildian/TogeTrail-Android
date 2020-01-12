package com.sildian.apps.togetrail.common.utils

import org.junit.Test
import org.junit.Assert.*
import java.util.*

class DateUtilitiesTest {

    @Test
    fun given_18nov2019_when_getDate_then_checkResultIs18nov2019() {
        val calendar=Calendar.getInstance()
        calendar.set(Calendar.YEAR, 2019)
        calendar.set(Calendar.MONTH, 10)
        calendar.set(Calendar.DAY_OF_MONTH, 18)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val expectedResult=calendar.time
        assertEquals(expectedResult, DateUtilities.getDate(2019, 10, 18))
    }

    @Test
    fun given_18nov2019at9h30_when_getDateAndTime_then_checkResultIs18nov2019at9h30() {
        val calendar=Calendar.getInstance()
        calendar.set(Calendar.YEAR, 2019)
        calendar.set(Calendar.MONTH, 10)
        calendar.set(Calendar.DAY_OF_MONTH, 18)
        calendar.set(Calendar.HOUR_OF_DAY, 9)
        calendar.set(Calendar.MINUTE, 30)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val expectedResult=calendar.time
        assertEquals(expectedResult, DateUtilities.getDateAndTime(2019, 10, 18, 9, 30))
    }

    @Test
    fun given_150min_when_displayDuration_then_checkResultIs2h30m(){
        Locale.setDefault(Locale.US)
        val hourMetric="h"
        val minuteMetric="min"
        assertEquals("2 h 30 min", DateUtilities.displayDuration(150, hourMetric, minuteMetric))
    }
}