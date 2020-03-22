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
        if(country.isNullOrEmpty()){
            return null
        }
        fullLocation.append(if(!address.isNullOrEmpty())"$address\n" else "")
        fullLocation.append(if(!town.isNullOrEmpty())"$town\n" else "")
        fullLocation.append(if(!region.isNullOrEmpty())"$region\n" else "")
        fullLocation.append(country)

        return fullLocation.toString()
    }

    /**Data override**/

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is FineLocation) return false
        if (!super.equals(other)) return false

        if (country != other.country) return false
        if (region != other.region) return false
        if (town != other.town) return false
        if (address != other.address) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + (country?.hashCode() ?: 0)
        result = 31 * result + (region?.hashCode() ?: 0)
        result = 31 * result + (town?.hashCode() ?: 0)
        result = 31 * result + (address?.hashCode() ?: 0)
        return result
    }
}