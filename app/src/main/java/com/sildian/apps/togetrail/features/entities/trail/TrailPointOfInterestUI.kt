package com.sildian.apps.togetrail.features.entities.trail

import android.os.Parcelable
import com.sildian.apps.togetrail.common.core.geo.Position
import kotlinx.android.parcel.Parcelize
import java.time.LocalDateTime

@Parcelize
data class TrailPointOfInterestUI(
    val number: Int,
    val position: Position,
    val registrationTime: LocalDateTime,
    val name: String,
    val description: String,
    val photoUrl: String?,
) : Parcelable
