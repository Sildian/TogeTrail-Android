package com.sildian.apps.togetrail.common.search

import com.sildian.apps.togetrail.common.core.geo.Position
import com.sildian.apps.togetrail.common.core.location.Location

sealed interface SearchRequest {

    data object Default : SearchRequest

    data class FromAuthor(val authorId: String) : SearchRequest

    data class NearbyLocation(val location: Location) : SearchRequest

    data class AroundPosition(val position: Position, val radiusMeters: Double) : SearchRequest
}