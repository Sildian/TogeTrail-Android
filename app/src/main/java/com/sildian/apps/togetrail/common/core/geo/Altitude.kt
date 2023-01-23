package com.sildian.apps.togetrail.common.core.geo

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Altitude(val meters: Int) : Parcelable

fun Altitude.derivationTo(altitude: Altitude): Derivation =
    Derivation(meters = altitude.meters - meters)

fun Altitude.derivationFrom(altitude: Altitude): Derivation =
    Derivation(meters = meters - altitude.meters)