package com.sildian.apps.togetrail.trail.model.core

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.firestore.GeoPoint
import com.sildian.apps.togetrail.location.model.core.Location
import kotlinx.android.parcel.Parceler
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue
import java.util.*

/*************************************************************************************************
 * Trail
 ************************************************************************************************/

@Parcelize
data class Trail (
    var id:String?=null,
    var name:String?=null,
    val source:String="",
    var position:@RawValue GeoPoint?=null,
    var location: Location = Location(),
    var description:String?=null,
    val creationDate:Date=Date(),
    var lastUpdate:Date=Date(),
    var type: TrailType = TrailType.HIKING,
    var level: TrailLevel = TrailLevel.MEDIUM,
    var loop:Boolean=true,
    val trailTrack: TrailTrack = TrailTrack(),
    var duration:Int?=null,
    var distance:Int?=null,
    var ascent:Int?=null,
    var descent:Int?=null,
    var maxElevation:Int?=null,
    var minElevation:Int?=null,
    var authorId:String?=null
)
    :Parcelable
{

    /**Populates the position with the first trailPoint's coordinates**/

    fun autoPopulatePosition(){
        val firstPoint=this.trailTrack.getFirstTrailPoint()
        if(firstPoint!=null) {
            this.position = GeoPoint(firstPoint.latitude, firstPoint.longitude)
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

    /**Parcelable override**/

    companion object : Parceler<Trail> {
        override fun Trail.write(parcel: Parcel, flags: Int) {
            parcel.writeString(this.id)
            parcel.writeString(this.name)
            parcel.writeString(this.source)
            parcel.writeDouble(this.position?.latitude?:0.0)
            parcel.writeDouble(this.position?.longitude?:0.0)
            parcel.writeParcelable(this.location, Parcelable.CONTENTS_FILE_DESCRIPTOR)
            parcel.writeString(this.description)
            parcel.writeLong(this.creationDate.time)
            parcel.writeLong(this.lastUpdate.time)
            parcel.writeParcelable(this.type, Parcelable.CONTENTS_FILE_DESCRIPTOR)
            parcel.writeParcelable(this.level, Parcelable.CONTENTS_FILE_DESCRIPTOR)
            parcel.writeInt(if(this.loop) 1 else 0)
            parcel.writeParcelable(this.trailTrack, Parcelable.CONTENTS_FILE_DESCRIPTOR)
            parcel.writeInt(this.duration?:-1)
            parcel.writeInt(this.distance?:-1)
            parcel.writeInt(this.ascent?:-1)
            parcel.writeInt(this.descent?:-1)
            parcel.writeInt(this.maxElevation?:-1)
            parcel.writeInt(this.minElevation?:-1)
            parcel.writeString(this.authorId)
        }

        override fun create(parcel: Parcel): Trail {
            val trail=Trail(
                parcel.readString(),
                parcel.readString(),
                parcel.readString() ?: "",
                GeoPoint(parcel.readDouble(), parcel.readDouble()),
                parcel.readParcelable(Location::class.java.classLoader)!!,
                parcel.readString(),
                Date(parcel.readLong()),
                Date(parcel.readLong()),
                parcel.readParcelable(TrailType::class.java.classLoader)!!,
                parcel.readParcelable(TrailLevel::class.java.classLoader)!!,
                parcel.readInt() == 1,
                parcel.readParcelable(TrailTrack::class.java.classLoader)!!,
                parcel.readInt(),
                parcel.readInt(),
                parcel.readInt(),
                parcel.readInt(),
                parcel.readInt(),
                parcel.readInt(),
                parcel.readString()
            )
            if(trail.position?.latitude==0.0 && trail.position?.longitude==0.0){
                trail.position=null
            }
            if(trail.duration==-1) trail.duration=null
            if(trail.distance==-1) trail.distance=null
            if(trail.ascent==-1) trail.ascent=null
            if(trail.descent==-1) trail.descent=null
            if(trail.maxElevation==-1) trail.maxElevation=null
            if(trail.minElevation==-1) trail.minElevation=null

            return trail
        }
    }
}