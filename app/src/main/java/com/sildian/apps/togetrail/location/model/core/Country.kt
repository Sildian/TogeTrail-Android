package com.sildian.apps.togetrail.location.model.core

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/*************************************************************************************************
 * Country
 * @param code : the code is universal, no matter what language the device is using
 * @param name : the name depends on the language of the device
 ************************************************************************************************/

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