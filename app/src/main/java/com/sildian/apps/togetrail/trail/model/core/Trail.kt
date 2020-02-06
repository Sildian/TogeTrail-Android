package com.sildian.apps.togetrail.trail.model.core

import android.os.Parcelable
import com.sildian.apps.togetrail.common.model.Location
import kotlinx.android.parcel.Parcelize
import java.util.*

/*************************************************************************************************
 * Trail
 ************************************************************************************************/

@Parcelize
data class Trail (
    var id:String?=null,
    var name:String="",
    val source:String="",
    var location: Location = Location(),
    var description:String="",
    val creationDate:Date=Date(),
    var lastUpdate:Date=Date(),
    var type: TrailType = TrailType.HIKING,
    var level: TrailLevel = TrailLevel.MEDIUM,
    val photosUrls:ArrayList<String> = arrayListOf(),
    var loop:Boolean=true,
    val trailTrack: TrailTrack = TrailTrack(),
    var duration:Int?=null,
    var distance:Int=0,
    var ascent:Int?=null,
    var descent:Int?=null,
    var maxElevation:Int?=null,
    var minElevation:Int?=null,
    val autoCalculateMetrics:Boolean=false
)
    :Parcelable
{
    /**Init**/

    init{

        /*Auto-calculates metrics only if it is asked in the constructor*/

        if(this.autoCalculateMetrics) {
            autoCalculateMetrics()
        }
    }

    /**Calculates all metrics from the trailTrack info**/

    fun autoCalculateMetrics(){
        autoCalculateDuration()
        autoCalculateDistance()
        autoCalculateAscent()
        autoCalculateDescent()
        autoCalculateMaxElevation()
        autoCalculateMinElevation()
    }

    /**Calculates duration from the trailTrack info**/

    fun autoCalculateDuration(){
        this.duration=this.trailTrack.getDuration()
    }

    /**Calculates distance from the trailTrack info**/

    fun autoCalculateDistance(){
        this.distance=this.trailTrack.getDistance()
    }

    /**Calculates ascent from the trailTrack info**/

    fun autoCalculateAscent(){
        this.ascent=this.trailTrack.getAscent()
    }

    /**Calculates descent from the trailTrack info**/

    fun autoCalculateDescent(){
        this.descent=this.trailTrack.getDescent()
    }

    /**Calculates max elevation from the trailTrack info**/

    fun autoCalculateMaxElevation(){
        this.maxElevation=this.trailTrack.getMaxElevation()
    }

    /**Calculates min elevation from the trailTrack info**/

    fun autoCalculateMinElevation(){
        this.minElevation=this.trailTrack.getMinElevation()
    }
}