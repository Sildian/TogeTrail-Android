package com.sildian.apps.togetrail.domainLayer.mappers

import com.sildian.apps.togetrail.common.core.geo.toGeoHash
import com.sildian.apps.togetrail.common.core.geo.toGeoLocation
import com.sildian.apps.togetrail.common.core.geo.toPosition
import com.sildian.apps.togetrail.common.utils.toDate
import com.sildian.apps.togetrail.common.utils.toLocalDateTime
import com.sildian.apps.togetrail.uiLayer.entities.trail.TrailUI
import com.sildian.apps.togetrail.uiLayer.entities.trail.TrailUI.Level as LevelUI
import com.sildian.apps.togetrail.uiLayer.entities.trail.TrailUI.Measures as MeasuresUI
import com.sildian.apps.togetrail.dataLayer.database.entities.trail.Trail
import java.time.LocalDateTime
import com.sildian.apps.togetrail.dataLayer.database.entities.trail.Trail.Level as Level
import com.sildian.apps.togetrail.dataLayer.database.entities.trail.Trail.Measures as Measures

fun TrailUI.toDataModel(): Trail =
    Trail(
        id = id,
        name = name,
        mainPhotoUrl = mainPhotoUrl,
        source = source,
        position = position.toGeoLocation(),
        positionHash = position.toGeoHash(),
        location = location,
        description = description,
        level = level.toDataModel(),
        isLoop = isLoop,
        measures = measures.toDataModel(),
        creationDate = creationDate.toDate(),
        authorId = authorId,
        nbLikes = nbLikes,
    )

private fun LevelUI.toDataModel(): Level =
    when (this) {
        LevelUI.Easy -> Level.Easy
        LevelUI.Medium -> Level.Medium
        LevelUI.Hard -> Level.Hard
    }

private fun MeasuresUI.toDataModel(): Measures =
    Measures(
        duration = duration,
        distance = distance,
        ascent = ascent,
        descent = descent,
        maxElevation = maxElevation,
        minElevation = minElevation,
    )

@Throws(IllegalStateException::class)
fun Trail.toUIModel(currentUserId: String?): TrailUI {
    val id = id ?: throw IllegalStateException("Trail id should be provided")
    val name = name ?: throw IllegalStateException("Trail name should be provided")
    val mainPhotoUrl = mainPhotoUrl
    val source = source.orEmpty()
    val position = position?.toPosition() ?: throw IllegalStateException("Trail position should be provided")
    val location = location ?: throw IllegalStateException("Trail location should be provided")
    val description = description.orEmpty()
    val level = level?.toUIModel() ?: throw IllegalStateException("Trail level should be provided")
    val isLoop = isLoop ?: false
    val measures = measures?.toUIModel() ?: MeasuresUI()
    val creationDate = creationDate?.toLocalDateTime() ?: LocalDateTime.now()
    val authorId = authorId.orEmpty()
    val isCurrentUserAuthor = currentUserId.isNullOrBlank().not() && currentUserId == authorId
    val nbLikes = nbLikes ?: 0
    return TrailUI(
        id = id,
        name = name,
        mainPhotoUrl = mainPhotoUrl,
        source = source,
        position = position,
        location = location,
        description = description,
        level = level,
        isLoop = isLoop,
        measures = measures,
        creationDate = creationDate,
        authorId = authorId,
        isCurrentUserAuthor = isCurrentUserAuthor,
        nbLikes = nbLikes,
    )
}

private fun Level.toUIModel(): LevelUI =
    when (this) {
        Level.Easy -> LevelUI.Easy
        Level.Medium -> LevelUI.Medium
        Level.Hard -> LevelUI.Hard
    }

private fun Measures.toUIModel(): MeasuresUI =
    MeasuresUI(
        duration = duration,
        distance = distance,
        ascent = ascent,
        descent = descent,
        maxElevation = maxElevation,
        minElevation = minElevation,
    )