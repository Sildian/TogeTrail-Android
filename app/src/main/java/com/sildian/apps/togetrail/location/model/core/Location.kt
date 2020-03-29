package com.sildian.apps.togetrail.location.model.core

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/*************************************************************************************************
 * Location
 ************************************************************************************************/

@Parcelize
data class Location (
    val country:Country?=null,
    val region:Region?=null,
    val fullAddress:String?=null
)
    :Parcelable
{

    override fun toString(): String {
        return when {
            country != null && region != null -> "$region, $country"
            country != null -> "$country"
            else -> ""
        }
    }
}