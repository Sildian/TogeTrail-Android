package com.sildian.apps.togetrail.location.data.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/*************************************************************************************************
 * Region
 * @deprecated : Use [com.sildian.apps.togetrail.common.core.location.Location.Region]
 * @param code : the code is universal, no matter what language the device is using
 * @param name : the name depends on the language of the device
 ************************************************************************************************/

@Deprecated("Use [common.core.location.Location.Region]")
@Parcelize
data class Region (
    val code:String="",
    val name:String=""
)
    :Parcelable
{

    override fun equals(other: Any?): Boolean {
        return other is Region && other.code == this.code
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }

    override fun toString(): String {
        return this.name
    }
}