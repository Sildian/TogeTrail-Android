package com.sildian.apps.togetrail.event.model.core

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.firestore.GeoPoint
import com.sildian.apps.togetrail.common.model.FineLocation
import com.sildian.apps.togetrail.common.model.Location
import kotlinx.android.parcel.Parceler
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue
import org.joda.time.Duration
import java.util.*

/*************************************************************************************************
 * An Event is organized by a hiker in order to trail with other hikers
 ************************************************************************************************/

@Parcelize
data class Event(
    var id:String?=null,
    var name:String?=null,
    var position:@RawValue GeoPoint?=null,
    var location: Location =Location(),
    var meetingPoint:FineLocation= FineLocation(),
    var beginDate:Date?=null,
    var endDate:Date?=null,
    var description:String?=null,
    var isCanceled:Boolean=false,
    var authorId:String?=null,
    /*Extra info*/
    var nbHikersRegistered:Int=0
)
    :Parcelable
{
    /**
     * Gets the current status of the event
     * @param currentDate : the current date
     * @return the related status
     */

    fun getStatus(currentDate:Date):EventStatus{
        return when{
            this.isCanceled -> EventStatus.CANCELED
            this.beginDate==null || this.endDate==null -> EventStatus.UNKNOWN
            this.beginDate?.time!! > currentDate.time -> EventStatus.FUTURE
            this.endDate?.time!! < currentDate.time -> EventStatus.PAST
            else -> EventStatus.ON_GOING
        }
    }

    /**
     * Gets the duration in days of the event
     * @return the duration in days
     */

    fun getNbDays():Int?{
        return when{
            this.beginDate==null -> null
            this.endDate==null -> null
            else -> {
                val minutes=(this.endDate!!.time-this.beginDate!!.time) / 60000
                val duration=Duration.standardMinutes(minutes)
                val period=duration.toPeriod()
                period.plusDays(1).toStandardDays().days
            }
        }
    }

    /**Parcelable override**/

    companion object : Parceler<Event> {
        override fun Event.write(parcel: Parcel, flags: Int) {
            parcel.writeString(this.id)
            parcel.writeString(this.name)
            parcel.writeDouble(this.position?.latitude?:0.0)
            parcel.writeDouble(this.position?.longitude?:0.0)
            parcel.writeParcelable(this.location, Parcelable.CONTENTS_FILE_DESCRIPTOR)
            parcel.writeParcelable(this.meetingPoint, Parcelable.CONTENTS_FILE_DESCRIPTOR)
            parcel.writeLong(this.beginDate?.time?:0)
            parcel.writeLong(this.endDate?.time?:0)
            parcel.writeString(this.description)
            parcel.writeInt(if(this.isCanceled) 1 else 0)
            parcel.writeString(this.authorId)
            parcel.writeInt(this.nbHikersRegistered)
        }

        override fun create(parcel: Parcel): Event {
            val event=Event(
                parcel.readString(),
                parcel.readString(),
                GeoPoint(parcel.readDouble(), parcel.readDouble()),
                parcel.readParcelable(Location::class.java.classLoader)!!,
                parcel.readParcelable(FineLocation::class.java.classLoader)!!,
                Date(parcel.readLong()),
                Date(parcel.readLong()),
                parcel.readString(),
                parcel.readInt()==1,
                parcel.readString(),
                parcel.readInt()
            )
            if(event.position?.latitude==0.0 && event.position?.longitude==0.0){
                event.position=null
            }
            if(event.beginDate?.time==0.toLong()) event.beginDate=null
            if(event.endDate?.time==0.toLong()) event.endDate=null

            return event
        }
    }
}