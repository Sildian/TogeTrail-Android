package com.sildian.apps.togetrail.hiker.model.core

import android.os.Parcelable
import com.sildian.apps.togetrail.common.model.Location
import kotlinx.android.parcel.Parcelize
import java.util.*
import kotlin.math.floor

/*************************************************************************************************
 * Hiker
 ************************************************************************************************/

@Parcelize
data class Hiker (
    val id:String="",
    val email:String?=null,
    var name:String?=null,
    var photoUrl:String?=null,
    var birthday: Date?=null,
    var liveLocation:Location=Location(),
    var description:String?=null,
    val registrationDate:Date=Date(),
    /*Extra info*/
    var nbTrailsCreated:Int=0,  //TODO update this when creating a trail
    var nbEventsCreated:Int=0,  //TODO update this when creating an event
    var nbEventsAttended:Int=0
)
    :Parcelable
{
    /**
     * Gets the hiker's age in years
     * @param currentDate : the current date
     * @return the age (in years) or null if the birthDate is unknown
     */

    fun getAge(currentDate: Date):Int?{
        return if(this.birthday!=null)
            floor((currentDate.time.toDouble() - this.birthday!!.time.toDouble())
                    /60000/60/24/365).toInt()
        else null
    }
}