package com.sildian.apps.togetrail.common.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/*************************************************************************************************
 * FineLocation inherits from Location and adds more details
 ************************************************************************************************/

@Parcelize
class FineLocation(
    override val country:String?=null,
    override val region:String?=null,
    override val town:String?=null,
    val address:String?=null
)
    : Location(country, region, town),
    Parcelable
{

    /**
     * Gets the full location to display on the screen.
     * The country at least must be known.
     * @return the full location to display, null if the country is unknown
     */

    override fun getFullLocation(): String? {
        val fullLocation=StringBuilder()
        if(country==null){
            return null
        }
        fullLocation.append(if(address!=null)"$address, " else "")
        fullLocation.append(if(town!=null)"$town, " else "")
        fullLocation.append(if(region!=null)"$region, " else "")
        fullLocation.append(country)

        return fullLocation.toString()
    }
}