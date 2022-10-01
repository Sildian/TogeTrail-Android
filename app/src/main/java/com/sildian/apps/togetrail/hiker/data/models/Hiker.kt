package com.sildian.apps.togetrail.hiker.data.models

import android.os.Parcelable
import com.sildian.apps.togetrail.location.data.models.Location
import kotlinx.android.parcel.Parcelize
import java.util.*
import kotlin.math.floor

/*************************************************************************************************
 * Hiker
 ************************************************************************************************/

@Parcelize
data class Hiker (
    val id:String="",
    var email:String?=null,
    var name:String?=null,
    var photoUrl:String?=null,
    var birthday: Date?=null,
    var liveLocation: Location = Location(),
    var description:String?=null,
    val registrationDate:Date=Date(),
    /*Extra info*/
    var nbTrailsCreated:Int=0,
    var nbEventsCreated:Int=0,
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