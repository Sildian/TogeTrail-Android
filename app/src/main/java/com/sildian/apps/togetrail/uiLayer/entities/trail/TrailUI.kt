package com.sildian.apps.togetrail.uiLayer.entities.trail

import android.os.Parcelable
import com.sildian.apps.togetrail.common.core.geo.Altitude
import com.sildian.apps.togetrail.common.core.geo.Derivation
import com.sildian.apps.togetrail.common.core.geo.Distance
import com.sildian.apps.togetrail.common.core.geo.Position
import com.sildian.apps.togetrail.common.core.location.Location
import kotlinx.android.parcel.Parcelize
import java.time.Duration
import java.time.LocalDateTime

@Parcelize
data class TrailUI(
    val id: String,
    val name: String,
    val mainPhotoUrl: String?,
    val source: String,
    val position: Position,
    val location: Location,
    val description: String,
    val level: Level,
    val isLoop: Boolean,
    val measures: Measures,
    val creationDate: LocalDateTime,
    val authorId: String,
    val isCurrentUserAuthor: Boolean,
    val nbLikes: Int,
) : Parcelable {

    @Parcelize
    enum class Level(val level: Int) : Parcelable {
        Easy(level = 1),
        Medium(level = 2),
        Hard(level = 3),
    }

    @Parcelize
    data class Measures(
        val duration: Duration? = null,
        val distance: Distance? = null,
        val ascent: Derivation? = null,
        val descent: Derivation? = null,
        val maxElevation: Altitude? = null,
        val minElevation: Altitude? = null,
    ) : Parcelable
}