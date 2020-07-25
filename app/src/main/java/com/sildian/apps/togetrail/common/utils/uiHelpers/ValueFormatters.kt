package com.sildian.apps.togetrail.common.utils.uiHelpers

import android.content.Context
import com.sildian.apps.circularsliderlibrary.ValueFormatter
import com.sildian.apps.togetrail.common.utils.MetricsHelper

/*************************************************************************************************
 * A set of value formaters to display values
 ************************************************************************************************/

object ValueFormatters {

    /**Displays durations**/

    class DurationValueFormatter: ValueFormatter {
        override fun formatValue(value: Int, context: Context?): String {
            return if(context!=null) {
                MetricsHelper.displayDuration(context, value)
            }
            else{
                value.toString()
            }
        }
    }

    /**Displays distances**/

    class DistanceValueFormatter:ValueFormatter{
        override fun formatValue(value: Int, context: Context?): String {
            return if(context!=null){
                MetricsHelper.displayDistance(context, value, false, false)
            }
            else{
                value.toString()
            }
        }
    }

    /**Displays altitudes**/

    class AltitudeValueFormatter:ValueFormatter{
        override fun formatValue(value: Int, context: Context?): String {
            return if(context!=null){
                MetricsHelper.displayElevation(context, value, false, false)
            }
            else{
                value.toString()
            }
        }
    }
}