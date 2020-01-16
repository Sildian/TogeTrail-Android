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
    val trailTrack:TrailTrack= TrailTrack(),
    var duration:Int?=null,
    var distance:Int=0,
    var ascent:Int?=null,
    var descent:Int?=null,
    var maxElevation:Int?=null,
    var minElevation:Int?=null
)
{
    init{

        /*Populates the following fields with the data from trailTracks.
        * These fields still can be modified by the user.*/

        this.duration=this.trailTrack.getDuration()
        this.distance=this.trailTrack.getDistance()
        this.ascent=this.trailTrack.getAscent()
        this.descent=this.trailTrack.getDescent()
        this.maxElevation=this.trailTrack.getMaxElevation()
        this.minElevation=this.trailTrack.getMinElevation()
    }
}