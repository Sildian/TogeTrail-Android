package com.sildian.apps.togetrail.uiLayer.entities.trail

import android.os.Parcelable
import com.sildian.apps.togetrail.common.core.geo.Position
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime

@Parcelize
data class TrailPointUI(
    val number: Int,
    val position: Position,
    val registrationTime: LocalDateTime?,
) : Parcelable
