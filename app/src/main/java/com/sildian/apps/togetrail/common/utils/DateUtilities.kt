package com.sildian.apps.togetrail.common.utils

import org.joda.time.Duration
import org.joda.time.format.PeriodFormatterBuilder
import java.text.ParseException
import java.text.SimpleDateFormat
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

    @JvmStatic
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

    @JvmStatic
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

    /**
     * Merges separated date and time to a single one
     * @param date : the date
     * @param time : the time
     * @return a single date and time
     */

    @JvmStatic
    fun mergeDateAndTime(date:Date, time:Date):Date{
        val dateCalendar= Calendar.getInstance()
        dateCalendar.time=date
        val timeCalendar=Calendar.getInstance()
        timeCalendar.time=time
        return getDateAndTime(
            dateCalendar.get(Calendar.YEAR),
            dateCalendar.get(Calendar.MONTH),
            dateCalendar.get(Calendar.DAY_OF_MONTH),
            timeCalendar.get(Calendar.HOUR_OF_DAY),
            timeCalendar.get(Calendar.MINUTE)
        )
    }

    /**
     * Displays a date (short format)
     * @param date : the date
     * @return a string
     */

    @JvmStatic
    fun displayDateShort(date:Date):String{
        val format=SimpleDateFormat.getDateInstance(SimpleDateFormat.SHORT)
        return format.format(date)
    }

    /**
     * Displays a date (medium format)
     * @param date : the date
     * @return a string
     */

    @JvmStatic
    fun displayDateMedium(date:Date):String{
        val format=SimpleDateFormat.getDateInstance(SimpleDateFormat.MEDIUM)
        return format.format(date)
    }

    /**
     * Displays a date (full format)
     * @param date : the date
     * @return a string
     */

    @JvmStatic
    fun displayDateFull(date:Date):String{
        val format=SimpleDateFormat.getDateInstance(SimpleDateFormat.FULL)
        return format.format(date)
    }

    /**
     * Displays a time (short format)
     * @param date : the date
     * @return a string
     */

    @JvmStatic
    fun displayTime(date:Date):String{
        val format=SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT)
        return format.format(date)
    }

    /**
     * Displays a date and time (short format)
     * @param date : the date
     * @return a string
     */

    @JvmStatic
    fun displayDateAndTimeShort(date:Date):String{
        val format=SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.SHORT, SimpleDateFormat.SHORT)
        return format.format(date)
    }

    /**
     * Displays a date and time (full format)
     * @param date : the date
     * @return a string
     */

    @JvmStatic
    fun displayDateAndTimeFull(date:Date):String{
        val format=SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.FULL, SimpleDateFormat.SHORT)
        return format.format(date)
    }

    /**
     * Gets a date from a string (short format)
     * @param displayedDate : the string
     * @return the date, or null if the string is not recognized
     */

    @JvmStatic
    fun getDateFromString(displayedDate:String):Date?{
        return try {
            val format = SimpleDateFormat.getDateInstance(SimpleDateFormat.SHORT)
            format.parse(displayedDate)
        }
        catch(e:ParseException){
            e.printStackTrace()
            null
        }
    }

    /**
     * Gets a time from a string (short format)
     * @param displayedTime : the string
     * @return the time, or null if the string is not recognized
     */

    @JvmStatic
    fun getTimeFromString(displayedTime:String):Date?{
        return try {
            val format = SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT)
            format.parse(displayedTime)
        }
        catch(e:ParseException){
            e.printStackTrace()
            null
        }
    }

    /**
     * Displays a duration as a string
     * @param minutes : the total number of minutes
     * @param hourMetric : the metric used to display hours
     * @param minuteMetric : the metric used to display minutes
     * @return a string to display the duration
     */

    @JvmStatic
    fun displayDuration(minutes:Int, hourMetric:String, minuteMetric:String):String{
        val duration = Duration.standardMinutes(minutes.toLong())
        val period = duration.toPeriod()
        val periodFormat =
            if(minutes>60) {
                PeriodFormatterBuilder()
                    .appendHours()
                    .appendSuffix(hourMetric)
                    .appendMinutes()
            }
            else{
                PeriodFormatterBuilder()
                    .appendMinutes()
                    .appendSuffix(minuteMetric)
            }
        return periodFormat.toFormatter().print(period)
    }
}