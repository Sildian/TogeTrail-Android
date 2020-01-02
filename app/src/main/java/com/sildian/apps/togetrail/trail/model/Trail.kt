package com.sildian.apps.togetrail.trail.model

import com.sildian.apps.togetrail.common.model.Location
import java.util.*

/*************************************************************************************************
 * Trail
 ************************************************************************************************/

data class Trail (
    var name:String="",
    val source:String="",
    val location: Location = Location(),
    var description:String="",
    val creationDate:Date=Date(),
    var lastUpdate:Date=Date(),
    var type:TrailType=TrailType.HIKING,
    var level:TrailLevel=TrailLevel.MEDIUM,
    val photosUrls:ArrayList<String> = arrayListOf(),
    var loop:Boolean=true,
    val trailTrack:TrailTrack= TrailTrack()
)