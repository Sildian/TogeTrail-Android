package com.sildian.apps.togetrail.uiLayer.entities.hiker

import android.os.Parcelable
import com.sildian.apps.togetrail.common.core.location.Location
import kotlinx.parcelize.Parcelize
import java.time.LocalDate
import java.time.LocalDateTime

@Parcelize
data class HikerUI(
    val id: String,
    val email: String,
    val name: String,
    val photoUrl: String?,
    val birthday: LocalDate?,
    val home: Location?,
    val description: String,
    val profileCreationDate: LocalDateTime,
    val isCurrentUser: Boolean,
    val nbTrailsCreated: Int,
    val nbEventsCreated: Int,
    val nbEventsAttended: Int,
) : Parcelable
