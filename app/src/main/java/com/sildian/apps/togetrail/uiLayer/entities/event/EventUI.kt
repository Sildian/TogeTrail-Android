package com.sildian.apps.togetrail.uiLayer.entities.event

import android.os.Parcelable
import com.sildian.apps.togetrail.common.core.geo.Position
import com.sildian.apps.togetrail.common.core.location.Location
import kotlinx.android.parcel.Parcelize
import java.time.LocalDate
import java.time.LocalDateTime

@Parcelize
data class EventUI(
    val id: String,
    val name: String,
    val mainPhotoUrl: String?,
    val position: Position,
    val meetingLocation: Location,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val description: String,
    val isCanceled: Boolean,
    val creationDate: LocalDateTime,
    val authorId: String,
    val isCurrentUserAuthor: Boolean,
    val nbHikersRegistered: Int,
) : Parcelable
