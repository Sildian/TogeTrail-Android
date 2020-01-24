package com.sildian.apps.togetrail.trail.model

import com.google.android.gms.maps.model.LatLng
import com.sildian.apps.togetrail.common.utils.GeoUtilities

/*************************************************************************************************
 * A TrailTrack is a path related to a trail
 * @param trailPoints : these points form the track
 * @param trailPointsOfInterest : in addition, some points of interest can add some information
 ************************************************************************************************/

class TrailTrack(
    val trailPoints:ArrayList<TrailPoint> = arrayListOf(),
    val trailPointsOfInterest:ArrayList<TrailPointOfInterest> = arrayListOf()
)
{

    /**
     * Gets the duration of the trail (in minutes)
     * @return the duration (in minutes), or null if no time data is available
     */

    fun getDuration():Int?{
        return if(this.trailPoints.isNotEmpty()) {
            val firstTrailPointTime = this.trailPoints.first().time
            val lastTrailPointTime = this.trailPoints.last().time
            if (firstTrailPointTime == null || lastTrailPointTime == null) {
                null
            } else {
                ((lastTrailPointTime.time - firstTrailPointTime.time) / 60000).toInt()
            }
        }else{
            null
        }
    }

    /**
     * Gets the total distance of the trail (in meters)
     * @return the total distance (in meters)
     */

    fun getDistance():Int{
        var distance=0
        for(i in this.trailPoints.indices){
            if(i>0){
                val previousPointLatLng=LatLng(this.trailPoints[i-1].latitude, this.trailPoints[i-1].longitude)
                val currentPointLatLng=LatLng(this.trailPoints[i].latitude, this.trailPoints[i].longitude)
                val currentDistance=GeoUtilities.getDistance(previousPointLatLng, currentPointLatLng)
                distance+=currentDistance
            }
        }
        return distance
    }

    /**
     * Gets the ascent of the trail (in meters)
     * @return the ascent (in meters), or null if one point at last has not elevation data
     */

    fun getAscent():Int?{
        if(this.trailPoints.isNotEmpty() && this.trailPoints.find { it.elevation==null } ==null){
            var ascent = 0
            for (i in this.trailPoints.indices) {
                if (i > 0) {
                    val previousPointElevation = this.trailPoints[i - 1].elevation!!
                    val currentPointElevation = this.trailPoints[i].elevation!!
                    val elevationDifference = currentPointElevation - previousPointElevation
                    if (elevationDifference > 0) {
                        ascent += elevationDifference
                    }
                }
            }
            return ascent
        }
        else {
            return null
        }
    }

    /**
     * Gets the descent of the trail (in meters)
     * @return the descent (in meters), or null if one point at last has not elevation data
     */

    fun getDescent():Int?{
        if(this.trailPoints.isNotEmpty() && this.trailPoints.find { it.elevation==null } ==null){
            var descent = 0
            for (i in this.trailPoints.indices) {
                if (i > 0) {
                    val previousPointElevation = this.trailPoints[i - 1].elevation!!
                    val currentPointElevation = this.trailPoints[i].elevation!!
                    val elevationDifference = currentPointElevation - previousPointElevation
                    if (elevationDifference < 0) {
                        descent -= elevationDifference
                    }
                }
            }
            return descent
        }
        else {
            return null
        }
    }

    /**
     * Gets the max elevation of the trail (in meters)
     * @return the max elevation (in meters), or null if one point at least has no elevation data
     */

    fun getMaxElevation():Int?{
        return if (this.trailPoints.find { it.elevation==null } ==null){
            this.trailPoints.maxBy { it.elevation!! }?.elevation
        }else{
            null
        }
    }

    /**
     * Gets the min elevation of the trail (in meters)
     * @return the min elevation (in meters), or null if one point at least has no elevation data
     */

    fun getMinElevation():Int?{
        return if (this.trailPoints.find { it.elevation==null } ==null){
            this.trailPoints.minBy { it.elevation!! }?.elevation
        }else{
            null
        }
    }

    /**
     * Finds a trailPointOfInterest matching the latitude and longitude of the given trailPoint
     * @param trailPoint : the given trailPoint
     * @return the index of the resulted trailPointOfInterest, or null if no one matches
     */

    fun findTrailPointOfInterest(trailPoint:TrailPoint):Int?{
        val index= this.trailPointsOfInterest.indexOfFirst { trailPoi->
            trailPoi.latitude==trailPoint.latitude
                    && trailPoi.longitude==trailPoint.longitude
        }
        return if(index!=-1) index else null
    }
}