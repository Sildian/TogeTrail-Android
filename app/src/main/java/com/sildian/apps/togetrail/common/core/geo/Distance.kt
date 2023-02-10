package com.sildian.apps.togetrail.common.core.geo

import android.os.Parcelable
import com.ibm.icu.text.DecimalFormat
import com.ibm.icu.text.MeasureFormat
import com.ibm.icu.util.Measure
import com.ibm.icu.util.MeasureUnit
import com.ibm.icu.util.ULocale
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Distance(val meters: Int) : Parcelable {

    @IgnoredOnParcel
    val kilometers: Double get() = meters.toDouble() / 1000

    override fun toString(): String =
        MeasureFormat.getInstance(
            ULocale.getDefault(),
            MeasureFormat.FormatWidth.SHORT,
            DecimalFormat.getInstance().apply { maximumFractionDigits = 1 }
        ).format(
            Measure(
                if (meters < 1000) meters else kilometers,
                if (meters < 1000) MeasureUnit.METER else MeasureUnit.KILOMETER
            )
        )

    operator fun plus(distance: Distance): Distance =
        Distance(meters = meters + distance.meters)

    operator fun minus(distance: Distance): Distance =
        Distance(meters = meters - distance.meters)
}
