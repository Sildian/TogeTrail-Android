package com.sildian.apps.togetrail.common.core.geo

import android.os.Parcelable
import com.ibm.icu.text.MeasureFormat
import com.ibm.icu.util.Measure
import com.ibm.icu.util.MeasureUnit
import com.ibm.icu.util.ULocale
import kotlinx.parcelize.Parcelize

@Parcelize
data class Derivation(val meters: Int) : Parcelable {

    override fun toString(): String =
        MeasureFormat.getInstance(ULocale.getDefault(), MeasureFormat.FormatWidth.SHORT)
            .format(Measure(meters, MeasureUnit.METER))

    operator fun plus(derivation: Derivation): Derivation =
        Derivation(meters = meters + derivation.meters)

    operator fun minus(derivation: Derivation): Derivation =
        Derivation(meters = meters - derivation.meters)
}
