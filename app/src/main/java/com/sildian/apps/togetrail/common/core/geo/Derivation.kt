package com.sildian.apps.togetrail.common.core.geo

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Derivation(val meters: Int) : Parcelable {

    operator fun plus(derivation: Derivation): Derivation =
        Derivation(meters = meters + derivation.meters)

    operator fun minus(derivation: Derivation): Derivation =
        Derivation(meters = meters - derivation.meters)
}
