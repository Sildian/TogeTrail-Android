package com.sildian.apps.togetrail.common.utils

import android.content.Context
import com.sildian.apps.togetrail.R

/*************************************************************************************************
 * Provides with some functions allowing to display Strings with specific metrics
 ************************************************************************************************/

object MetricsHelper {

    /**
     * Displays a duration with the appropriated metrics
     * @param context : the context
     * @param duration : the duration (in minutes)
     * @return a string containing the duration to display
     */

    @JvmStatic
    fun displayDuration(context: Context, duration:Long?):String{
        val hourMetric=context.resources.getString(R.string.metric_hour)
        val minuteMetric=context.resources.getString(R.string.metric_minute)
        val unkownText=context.resources.getString(R.string.message_unknown_short)
        return if(duration!=null)
                DateUtilities.displayDuration(duration, hourMetric, minuteMetric)
            else unkownText
    }

    /**
     * Displays a distance with the appropriated metrics
     * @param context : the context
     * @param distance : the distance (in meters)
     * @param showSuffix : true if a suffix should be displayed
     * @param suffixBottom : true if the suffix should be displayed at the bottom of the string
     * @return a string containing the distance to display
     */

    @JvmStatic
    fun displayDistance(context:Context, distance:Int?, showSuffix:Boolean, suffixBottom:Boolean):String {
        val metric = context.resources.getString(R.string.metric_kilometer)
        val suffix = context.resources.getString(R.string.label_trail_distance_short)
        val unknownText = context.resources.getString(R.string.message_unknown_short)
        return if (distance != null) {
            if (showSuffix) {
                NumberUtilities.displayNumberWithMetricAndSuffix(
                    distance.toDouble() / 1000, 1, metric, suffix, suffixBottom)
            } else {
                NumberUtilities.displayNumberWithMetric(
                    distance.toDouble() / 1000, 1, metric)
            }
        }
        else unknownText
    }

    /**
     * Displays an altitude with the appropriated metrics
     * @param context : the context
     * @param altitude : the altitude (in meters)
     * @param showSuffix : true if a suffix should be displayed
     * @param suffix : the suffix
     * @param suffixBottom : true if the suffix should be displayed at the bottom of the string
     * @return a string containing the altitude to display
     */

    @JvmStatic
    private fun displayAltitude(context:Context, altitude:Int?, showSuffix:Boolean, suffix:String, suffixBottom: Boolean):String{
        val metric=context.resources.getString(R.string.metric_meter)
        val unknownText=context.resources.getString(R.string.message_unknown_short)
        return if(altitude!=null) {
            if (showSuffix) {
                NumberUtilities.displayNumberWithMetricAndSuffix(
                    altitude.toDouble(), 0, metric, suffix, suffixBottom
                )
            } else {
                NumberUtilities.displayNumberWithMetric(
                    altitude.toDouble(), 0, metric
                )
            }
        }
        else unknownText
    }

    /**
     * Displays an ascent with the appropriated metrics
     * @param context : the context
     * @param altitude : the altitude (in meters)
     * @param showSuffix : true if a suffix should be displayed
     * @param suffixBottom : true if the suffix should be displayed at the bottom of the string
     * @return a string containing the ascent to display
     */

    @JvmStatic
    fun displayAscent(context:Context, altitude: Int?, showSuffix:Boolean, suffixBottom: Boolean):String{
        val suffix=context.resources.getString(R.string.label_trail_ascent_short)
        return displayAltitude(
            context,
            altitude,
            showSuffix,
            suffix,
            suffixBottom
        )
    }

    /**
     * Displays an descent with the appropriated metrics
     * @param context : the context
     * @param altitude : the altitude (in meters)
     * @param showSuffix : true if a suffix should be displayed
     * @param suffixBottom : true if the suffix should be displayed at the bottom of the string
     * @return a string containing the descent to display
     */

    @JvmStatic
    fun displayDescent(context:Context, altitude: Int?, showSuffix:Boolean, suffixBottom: Boolean):String{
        val suffix=context.resources.getString(R.string.label_trail_descent_short)
        return displayAltitude(
            context,
            altitude,
            showSuffix,
            suffix,
            suffixBottom
        )
    }

    /**
     * Displays a max elevation with the appropriated metrics
     * @param context : the context
     * @param altitude : the altitude (in meters)
     * @param showSuffix : true if a suffix should be displayed
     * @param suffixBottom : true if the suffix should be displayed at the bottom of the string
     * @return a string containing the max elevation to display
     */

    @JvmStatic
    fun displayMaxElevation(context:Context, altitude: Int?, showSuffix:Boolean, suffixBottom: Boolean):String{
        val suffix=context.resources.getString(R.string.label_trail_max_elevation_short)
        return displayAltitude(
            context,
            altitude,
            showSuffix,
            suffix,
            suffixBottom
        )
    }

    /**
     * Displays a min elevation with the appropriated metrics
     * @param context : the context
     * @param altitude : the altitude (in meters)
     * @param showSuffix : true if a suffix should be displayed
     * @param suffixBottom : true if the suffix should be displayed at the bottom of the string
     * @return a string containing the min elevation to display
     */

    @JvmStatic
    fun displayMinElevation(context:Context, altitude: Int?, showSuffix:Boolean, suffixBottom: Boolean):String{
        val suffix=context.resources.getString(R.string.label_trail_min_elevation_short)
        return displayAltitude(
            context,
            altitude,
            showSuffix,
            suffix,
            suffixBottom
        )
    }

    /**
     * Displays an elevation with the appropriated metrics
     * @param context : the context
     * @param altitude : the altitude (in meters)
     * @param showSuffix : true if a suffix should be displayed
     * @param suffixBottom : true if the suffix should be displayed at the bottom of the string
     * @return a string containing the elevation to display
     */

    @JvmStatic
    fun displayElevation(context:Context, altitude: Int?, showSuffix:Boolean, suffixBottom: Boolean):String{
        val suffix=context.resources.getString(R.string.label_trail_poi_elevation)
        return displayAltitude(
            context,
            altitude,
            showSuffix,
            suffix,
            suffixBottom
        )
    }
}