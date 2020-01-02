package com.sildian.apps.togetrail.trail.model

import java.util.*

/*************************************************************************************************
 * A TrailPoint represents a gps point of a TrailTrack on a map
 * @param latitude : the latitude
 * @param longitude : the longitude
 * @param elevation : the elevation (in meters)
 * @param time : the registered time of the point
 ************************************************************************************************/

open class TrailPoint (
    var latitude:Double=0.0,
    var longitude:Double=0.0,
    var elevation:Int?=null,
    var time:Date?=null
)