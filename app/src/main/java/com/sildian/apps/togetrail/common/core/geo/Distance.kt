package com.sildian.apps.togetrail.common.core.geo

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Distance(val meters: Int) : Parcelable {

    val kilometers: Double get() = meters.toDouble() / 1000

    operator fun plus(distance: Distance): Distance =
        Distance(meters = meters + distance.meters)

    operator fun minus(distance: Distance): Distance =
        Distance(meters = meters - distance.meters)
}
