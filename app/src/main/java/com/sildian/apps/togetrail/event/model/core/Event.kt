package com.sildian.apps.togetrail.event.model.core

import android.os.Parcelable
import com.sildian.apps.togetrail.common.model.FineLocation
import com.sildian.apps.togetrail.common.model.Location
import kotlinx.android.parcel.Parcelize
import java.util.*

/*************************************************************************************************
 * An Event is organized by a hiker in order to trail with other hikers
 ************************************************************************************************/

@Parcelize
data class Event(
    var id:String?=null,
    var name:String="",
    var location: Location =Location(),
    var meetingPoint:FineLocation= FineLocation(),
    var beginDate:Date?=null,
    var endDate:Date?=null,
    var description:String="",
    var isCanceled:Boolean=false,
    var authorId:String?=null,
    val trailsIds: ArrayList<String> = arrayListOf(),
    val registeredHikersIds: ArrayList<String> = arrayListOf()
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
}