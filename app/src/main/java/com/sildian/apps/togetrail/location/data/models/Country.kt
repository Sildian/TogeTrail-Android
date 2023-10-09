package com.sildian.apps.togetrail.location.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/*************************************************************************************************
 * Country
 * @deprecated : Use [com.sildian.apps.togetrail.common.core.location.Location.Country]
 * @param code : the code is universal, no matter what language the device is using
 * @param name : the name depends on the language of the device
 ************************************************************************************************/

@Deprecated("Use [common.core.location.Location.Country]")
@Parcelize
data class Country (
    val code:String="",
    val name:String=""
)
    :Parcelable
{

    override fun equals(other: Any?): Boolean {
        return other is Country && other.code == this.code
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }

    override fun toString(): String {
        return this.name
    }
}