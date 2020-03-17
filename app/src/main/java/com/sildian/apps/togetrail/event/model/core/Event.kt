package com.sildian.apps.togetrail.event.model.core

import android.os.Parcelable
import com.sildian.apps.togetrail.common.model.FineLocation
import com.sildian.apps.togetrail.common.model.Location
import kotlinx.android.parcel.Parcelize
import org.joda.time.Duration
import java.util.*

/*************************************************************************************************
 * An Event is organized by a hiker in order to trail with other hikers
 ************************************************************************************************/

@Parcelize
data class Event(
    var id:String?=null,
    var name:String?=null,
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
}