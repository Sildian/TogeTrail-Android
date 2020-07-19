package com.sildian.apps.togetrail.common.views.circularSlider

import android.content.Context

/*************************************************************************************************
 * Formats a value in order to display it
 ************************************************************************************************/

interface ValueFormatter {

    /**
     * Formats a value
     * @param value : the value
     * @param context : in case string resources are needed
     * @return a formated string
     */

    fun formatValue(value:Int, context: Context?):String
}