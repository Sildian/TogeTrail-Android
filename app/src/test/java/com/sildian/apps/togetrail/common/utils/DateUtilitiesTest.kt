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
    fun given_18nov2019at9h30_when_mergeDateAndTime_then_checkResultIs18nov2019at9h30() {
        val date=DateUtilities.getDate(2019, 10, 18)
        val time=DateUtilities.getDateAndTime(2019, 10, 18, 0, 30)
        assertEquals(time, DateUtilities.mergeDateAndTime(date, time))
    }

    @Test
    fun given_18nov2019_when_displayDateShort_then_checkResultIs18nov2019() {
        Locale.setDefault(Locale.US)
        val date=DateUtilities.getDate(2019, 10, 18)
        val expectedResult="11/18/19"
        assertEquals(expectedResult, DateUtilities.displayDateShort(date))
    }

    @Test
    fun given_18nov2019_when_displayDateMedium_then_checkResultIs18nov2019() {
        Locale.setDefault(Locale.US)
        val date=DateUtilities.getDate(2019, 10, 18)
        val expectedResult="Nov 18, 2019"
        assertEquals(expectedResult, DateUtilities.displayDateMedium(date))
    }

    @Test
    fun given_18nov2019_when_displayDateFull_then_checkResultIs18nov2019() {
        Locale.setDefault(Locale.US)
        val date=DateUtilities.getDate(2019, 10, 18)
        val expectedResult="Monday, November 18, 2019"
        assertEquals(expectedResult, DateUtilities.displayDateFull(date))
    }

    @Test
    fun given_18nov2019at15h30_when_displayTime_then_checkResultIs18nov2019at15h30() {
        Locale.setDefault(Locale.US)
        val date=DateUtilities.getDateAndTime(2019, 10, 18, 15, 30)
        val expectedResult="3:30 PM"
        assertEquals(expectedResult, DateUtilities.displayTime(date))
    }

    @Test
    fun given_18nov2019at15h30_when_displayDateAndTimeShort_then_checkResultIs18nov2019at15h30() {
        Locale.setDefault(Locale.US)
        val date=DateUtilities.getDateAndTime(2019, 10, 18, 15, 30)
        val expectedResult="11/18/19 3:30 PM"
        assertEquals(expectedResult, DateUtilities.displayDateAndTimeShort(date))
    }

    @Test
    fun given_18nov2019at15h30_when_displayDateAndTimeFull_then_checkResultIs18nov2019at15h30() {
        Locale.setDefault(Locale.US)
        val date=DateUtilities.getDateAndTime(2019, 10, 18, 15, 30)
        val expectedResult="Monday, November 18, 2019 3:30 PM"
        assertEquals(expectedResult, DateUtilities.displayDateAndTimeFull(date))
    }

    @Test
    fun given_18nov2019_when_getDateFromString_then_checkResultIs18nov2019() {
        Locale.setDefault(Locale.US)
        val displayedDate="11/18/19"
        val expectedResult=DateUtilities.getDate(2019, 10, 18)
        assertEquals(expectedResult, DateUtilities.getDateFromString(displayedDate))
    }

    @Test
    fun given_15h30_when_getTimeFromString_then_checkResultIs15h30() {
        Locale.setDefault(Locale.US)
        val displayedTime="3:30 PM"
        val expectedResult=DateUtilities.getDateAndTime(1970, 0, 1, 15, 30)
        assertEquals(expectedResult, DateUtilities.getTimeFromString(displayedTime))
    }

    @Test
    fun given_150min_when_displayDuration_then_checkResultIs2h30(){
        Locale.setDefault(Locale.US)
        val hourMetric="h"
        val minuteMetric="min"
        assertEquals("2h30", DateUtilities.displayDuration(150, hourMetric, minuteMetric))
    }

    @Test
    fun given_10min_when_displayDuration_then_checkResultIs50min(){
        Locale.setDefault(Locale.US)
        val hourMetric="h"
        val minuteMetric="min"
        assertEquals("50min", DateUtilities.displayDuration(50, hourMetric, minuteMetric))
    }
}