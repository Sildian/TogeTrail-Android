package com.sildian.apps.togetrail.common.views.circularSlider

import android.content.Context
import com.sildian.apps.togetrail.common.utils.MetricsHelper

/*************************************************************************************************
 * A set of value formaters to display values
 ************************************************************************************************/

object ValueFormaters {

    /**Displays durations**/

    class DurationValueFormater:ValueFormater{
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

    class DistanceValueFormater:ValueFormater{
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

    class AltitudeValueFormater:ValueFormater{
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