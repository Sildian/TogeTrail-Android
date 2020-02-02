package com.sildian.apps.togetrail.trail.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/*************************************************************************************************
 * The difficulty level of a trail
 ************************************************************************************************/

@Parcelize
enum class TrailLevel (val value:Int) :Parcelable {
    EASY(1),
    MEDIUM(2),
    HARD(3);

    /*********************************Static items***********************************************/

    companion object {

        /**
         * Creates an instance of TrailLevel from a value
         * @param value : the related value
         * @return the TrailLevel, MEDIUM by default
         */

        fun fromValue(value: Int): TrailLevel {
            return when (value) {
                1 -> EASY
                2 -> MEDIUM
                3 -> HARD
                else -> MEDIUM
            }
        }
    }
}