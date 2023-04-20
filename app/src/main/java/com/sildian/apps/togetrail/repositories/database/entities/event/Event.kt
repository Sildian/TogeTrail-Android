package com.sildian.apps.togetrail.repositories.database.entities.event

import com.firebase.geofire.GeoLocation
import com.sildian.apps.togetrail.common.core.location.Location
import java.util.*

data class Event(
    val id: String? = null,
    val name: String? = null,
    val mainPhotoUrl: String? = null,
    val position: GeoLocation? = null,
    val positionHash: String? = null,
    val meetingLocation: Location? = null,
    val startDate: Date? = null,
    val endDate: Date? = null,
    val description: String? = null,
    val isCanceled: Boolean = false,
    val creationDate: Date? = null,
    val authorId: String? = null,
    val nbHikersRegistered: Int = 0,
)
