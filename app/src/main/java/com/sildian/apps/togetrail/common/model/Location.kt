package com.sildian.apps.togetrail.common.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/*************************************************************************************************
 * Location
 ************************************************************************************************/

@Parcelize
open class Location (
    open val country:String?=null,
    open val region:String?=null,
    open val town:String?=null
)
    :Parcelable
{

    /**
     * Gets the full location to display on the screen.
     * The country at least must be known.
     * @return the full location to display, null if the country is unknown
     */

    open fun getFullLocation():String?{
        val fullLocation=StringBuilder()
        if(country.isNullOrEmpty()){
            return null
        }
        fullLocation.append(if(!town.isNullOrEmpty())"$town, " else "")
        fullLocation.append(if(!region.isNullOrEmpty())"$region, " else "")
        fullLocation.append(country)

        return fullLocation.toString()
    }

    /**Data override**/

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Location) return false

        if (country != other.country) return false
        if (region != other.region) return false
        if (town != other.town) return false

        return true
    }

    override fun hashCode(): Int {
        var result = country?.hashCode() ?: 0
        result = 31 * result + (region?.hashCode() ?: 0)
        result = 31 * result + (town?.hashCode() ?: 0)
        return result
    }
}