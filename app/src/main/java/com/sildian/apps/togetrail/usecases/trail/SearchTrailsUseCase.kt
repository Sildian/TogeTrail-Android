package com.sildian.apps.togetrail.usecases.trail

import com.sildian.apps.togetrail.common.core.geo.toGeoLocation
import com.sildian.apps.togetrail.common.network.AuthException
import com.sildian.apps.togetrail.common.network.DatabaseException
import com.sildian.apps.togetrail.common.search.SearchRequest
import com.sildian.apps.togetrail.features.entities.trail.TrailUI
import com.sildian.apps.togetrail.repositories.auth.AuthRepository
import com.sildian.apps.togetrail.repositories.database.trail.main.TrailRepository
import com.sildian.apps.togetrail.usecases.mappers.toUIModel
import javax.inject.Inject
import kotlin.jvm.Throws

interface SearchTrailsUseCase {
    @Throws(AuthException::class, DatabaseException::class, IllegalStateException::class)
    suspend operator fun invoke(request: SearchRequest): List<TrailUI>
}

class SearchTrailsUseCaseImpl @Inject constructor(
    private val authRepository: AuthRepository,
    private val trailRepository: TrailRepository,
) : SearchTrailsUseCase {

    override suspend operator fun invoke(request: SearchRequest): List<TrailUI> =
        when (request) {
            is SearchRequest.Default ->
                trailRepository.getLastTrails()
            is SearchRequest.FromAuthor ->
                trailRepository.getTrailsFromAuthor(authorId = request.authorId)
            is SearchRequest.NearbyLocation ->
                trailRepository.getTrailsNearbyLocation(location = request.location)
            is SearchRequest.AroundPosition ->
                trailRepository.getTrailsAroundPosition(
                    position = request.position.toGeoLocation(),
                    radiusMeters = request.radiusMeters,
                )
        }.map {
            it.toUIModel(currentUserId = authRepository.getCurrentUser()?.id)
        }
}