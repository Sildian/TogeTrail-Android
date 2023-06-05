package com.sildian.apps.togetrail.trail.data.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

/*************************************************************************************************
 * A TrailPoint represents a gps point of a TrailTrack on a map
 * @param latitude : the latitude
 * @param longitude : the longitude
 * @param elevation : the elevation (in meters)
 * @param time : the registered time of the point
 ************************************************************************************************/

@Deprecated("Replaced by new TrailPoint and TrailPointUI")
@Parcelize
data class TrailPoint (
    var latitude:Double=0.0,
    var longitude:Double=0.0,
    var elevation:Int?=null,
    var time:Date?=null
) : Parcelable