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
    val email:String="",
    var name:String="",
    var photoUrl:String?=null,
    var birthDate: Date?=null,
    var liveLocation:Location=Location(),
    var description:String="",
    val registrationDate:Date=Date(),
    var password:String="",
    val createdTrailsIds:ArrayList<String> = arrayListOf()
)
    :Parcelable
{
    /**
     * Gets the hiker's age in years
     * @param currentDate : the current date
     * @return the age (in years) or null if the birthDate is unknown
     */

    fun getAge(currentDate: Date):Int?{
        return if(this.birthDate!=null)
            floor((currentDate.time.toDouble() - this.birthDate!!.time.toDouble())
                    /60000/60/24/365).toInt()
        else null
    }
}