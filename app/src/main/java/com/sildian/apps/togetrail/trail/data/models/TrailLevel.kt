package com.sildian.apps.togetrail.trail.data.models

import android.os.Parcelable
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.sildian.apps.togetrail.R
import kotlinx.android.parcel.Parcelize

/*************************************************************************************************
 * The difficulty level of a trail
 ************************************************************************************************/

@Deprecated("Replaced by new Trail.Level and TrailUI.level")
@Parcelize
enum class TrailLevel (
    val value: Int,
    @StringRes val textResId: Int,
    @DrawableRes val drawableResId: Int
    )
    :
    Parcelable
{
    UNKNOWN(0, R.string.label_trail_level_unknown, R.drawable.ic_level_medium),
    EASY(1, R.string.label_trail_level_easy, R.drawable.ic_level_easy),
    MEDIUM(2, R.string.label_trail_level_medium, R.drawable.ic_level_medium),
    HARD(3, R.string.label_trail_level_hard, R.drawable.ic_level_hard);

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