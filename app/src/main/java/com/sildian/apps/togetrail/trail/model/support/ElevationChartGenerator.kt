package com.sildian.apps.togetrail.trail.model.support

import android.content.Context
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.android.gms.maps.model.LatLng
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.common.utils.GeoUtilities
import com.sildian.apps.togetrail.common.utils.MetricsHelper
import com.sildian.apps.togetrail.trail.model.core.Trail

/*************************************************************************************************
 * Allows to generate a chart representing a trail's elevation
 * @param context : the context
 * @param trail : the trail
 ************************************************************************************************/

class ElevationChartGenerator(private val context: Context, private val trail: Trail?) {

    /****************************************Data************************************************/

    var chartData: LineData? = null ; private set       //The generated chart data

    /*************************************Generation*********************************************/

    /**
     * Generates the chart data
     */

    fun generateChartData() {

        if (!this.trail?.trailTrack?.trailPoints.isNullOrEmpty()) {
            this.trail?.trailTrack?.trailPoints?.let { trailPoints ->

                val lineEntries = arrayListOf<Entry>()

                /*X axis begins at 0*/

                var x = 0f
                for (i in trailPoints.indices) {

                    if (trailPoints[i].elevation != null) {

                        trailPoints[i].elevation?.let { elevation ->

                            /*Adds the distance between each point to X axis*/

                            if (i > 0) {
                                x += GeoUtilities.getDistance(
                                    LatLng(
                                        trailPoints[i].latitude,
                                        trailPoints[i].longitude
                                    ),
                                    LatLng(
                                        trailPoints[i - 1].latitude,
                                        trailPoints[i - 1].longitude
                                    )
                                ).toFloat()
                            }

                            /*Y axis takes each point's elevation*/

                            val y = elevation.toFloat()
                            lineEntries.add(Entry(x, y))
                        }
                    } else {

                        /*If at least one point as no elevation, sets the chart data to null*/

                        this.chartData = null
                        return
                    }
                }

                val lineDataSet = LineDataSet(lineEntries, "")
                lineDataSet.setDrawCircles(false)
                lineDataSet.setDrawFilled(true)
                lineDataSet.color = ContextCompat.getColor(this.context, R.color.colorGreen)
                lineDataSet.fillColor = ContextCompat.getColor(this.context, R.color.colorGreen)
                this.chartData = LineData(lineDataSet)
            }
        } else {
            this.chartData = null
        }
    }

    /********************Class allowing to format the chart's values to display*****************/

    class ElevationValueFormatter(private val context: Context): ValueFormatter() {

        override fun getFormattedValue(value: Float): String {
            return MetricsHelper.displayElevation(this.context, value.toInt(), false, false)
        }
    }
}