package com.sildian.apps.togetrail.common.utils

import java.text.DecimalFormat

/*************************************************************************************************
 * Provides with some functions allowing to manipulate numbers
 ************************************************************************************************/

object NumberUtilities {

    /**
     * Displays a number with the locale format
     * @param number : the number to display
     * @param fractionDigit : the number of decimal digits to display
     * @return the formated number
     */

    @JvmStatic
    fun displayNumber(number:Double, fractionDigit:Int):String{
        val format=DecimalFormat.getNumberInstance()
        format.minimumFractionDigits=fractionDigit
        format.maximumFractionDigits=fractionDigit
        return format.format(number)
    }

    /**
     * Displays a number with the locale format and with a metric
     * @param number : the number to display
     * @param fractionDigit : the number of decimal digits to display
     * @param metric : the metric
     * @return the formated number
     */

    @JvmStatic
    fun displayNumberWithMetric(number:Double, fractionDigit:Int, metric:String):String{
        val formatedNumber= StringBuilder(displayNumber(number, fractionDigit))
        formatedNumber.append(" $metric")
        return formatedNumber.toString()
    }

    /**
     * Displays a number with the locale format, with a metric and a suffix
     * @param number : the number to display
     * @param fractionDigit : the number of decimal digits to display
     * @param metric : the metric
     * @param suffix : the suffix to add at the end
     * @param suffixBottom : true if the suffix should appear at the bottom
     * @return the formated number
     */

    @JvmStatic
    fun displayNumberWithMetricAndSuffix(number:Double, fractionDigit:Int,
                                         metric:String, suffix:String, suffixBottom:Boolean):String{

        val formatedNumber= StringBuilder(displayNumberWithMetric(number, fractionDigit, metric))
        if(suffixBottom) {
            formatedNumber.append("\n")
        }else{
            formatedNumber.append(" ")
        }
        formatedNumber.append(suffix)
        return formatedNumber.toString()
    }
}