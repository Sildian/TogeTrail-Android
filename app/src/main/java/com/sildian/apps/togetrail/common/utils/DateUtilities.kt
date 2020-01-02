package com.sildian.apps.togetrail.common.utils

import java.util.*

/*************************************************************************************************
 * Provides with some functions allowing to manipulate dates
 ************************************************************************************************/

object DateUtilities {

    /**
     * Gets a date from given parameters
     * @param year : the year
     * @param month : the month
     * @param day : the day
     * @return the resulted date
     */

    fun getDate(year:Int, month:Int, day:Int): Date {
        val calendar=Calendar.getInstance()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, day)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.time
    }

    /**
     * Gets a date and time from given parameters
     * @param year : the year
     * @param month : the month
     * @param day : the day
     * @param hour : the hour
     * @param minute : the minute
     * @return the resulted date and time
     */

    fun getDateAndTime(year:Int, month:Int, day:Int, hour:Int, minute:Int):Date{
        val calendar=Calendar.getInstance()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, day)
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.time
    }
}