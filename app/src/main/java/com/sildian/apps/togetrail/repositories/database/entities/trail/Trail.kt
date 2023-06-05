package com.sildian.apps.togetrail.repositories.database.entities.trail

import com.firebase.geofire.GeoLocation
import com.sildian.apps.togetrail.common.core.geo.Altitude
import com.sildian.apps.togetrail.common.core.geo.Derivation
import com.sildian.apps.togetrail.common.core.geo.Distance
import com.sildian.apps.togetrail.common.core.location.Location
import java.time.Duration
import java.util.Date

data class Trail(
    val id: String? = null,
    val name: String? = null,
    val mainPhotoUrl: String? = null,
    val source: String? = null,
    val position: GeoLocation? = null,
    val positionHash: String? = null,
    val location: Location? = null,
    val description: String? = null,
    val level: Level? = null,
    val isLoop: Boolean? = null,
    val measures: Measures? = null,
    val creationDate: Date? = null,
    val authorId: String? = null,
    val nbLikes: Int? = null,
) {

    enum class Level(val level: Int) {
        Easy(level = 1),
        Medium(level = 2),
        Hard(level = 3),
    }

    data class Measures(
        val duration: Duration? = null,
        val distance: Distance? = null,
        val ascent: Derivation? = null,
        val descent: Derivation? = null,
        val maxElevation: Altitude? = null,
        val minElevation: Altitude? = null,
    )
}
