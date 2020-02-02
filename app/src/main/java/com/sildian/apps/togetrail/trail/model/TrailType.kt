package com.sildian.apps.togetrail.trail.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/*************************************************************************************************
 * The type of a trail
 ************************************************************************************************/

@Parcelize
enum class TrailType (val value:Int) : Parcelable {
    HIKING(1),
    BIKING(2),
    OTHER(3);

    /*********************************Static items***********************************************/

    companion object {

        /**
         * Creates an instance of TrailType from a value
         * @param value : the related value
         * @return the TrailType, HIKING by default
         */

        fun fromValue(value: Int): TrailType {
            return when (value) {
                1 -> HIKING
                2 -> BIKING
                3 -> OTHER
                else -> HIKING
            }
        }
    }
}