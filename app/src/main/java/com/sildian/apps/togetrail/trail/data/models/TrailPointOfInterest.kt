package com.sildian.apps.togetrail.trail.data.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

/*************************************************************************************************
 * A TrailPointOfInterest is a specific TrailPoint including
 * some bonus information about the place around the point
 * @param latitude : the latitude
 * @param longitude : the longitude
 * @param elevation : the elevation (in meters)
 * @param time : the registered time of the point
 * @param name : the name of the point
 * @param description : the description of the place
 * @param photoUrl : the url of the photo representing the place
 ************************************************************************************************/

@Parcelize
data class TrailPointOfInterest(
    var latitude:Double=0.0,
    var longitude:Double=0.0,
    var elevation:Int?=null,
    var time:Date?=null,
    var name:String?=null,
    var description:String?=null,
    var photoUrl:String?=null
) : Parcelable {

    /**
     * Checks that data are valid
     * @return true if valid, false otherwise
     */

    fun isDataValid():Boolean{
        return this.name!=null
    }
}