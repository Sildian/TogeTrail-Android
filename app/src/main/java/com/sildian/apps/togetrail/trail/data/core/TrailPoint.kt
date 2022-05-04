package com.sildian.apps.togetrail.trail.data.core

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

@Parcelize
open class TrailPoint (
    open var latitude:Double=0.0,
    open var longitude:Double=0.0,
    open var elevation:Int?=null,
    open var time:Date?=null
)
    :Parcelable