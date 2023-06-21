package com.sildian.apps.togetrail.usecases.event

import com.sildian.apps.togetrail.common.core.geo.toGeoLocation
import com.sildian.apps.togetrail.common.network.AuthException
import com.sildian.apps.togetrail.common.network.DatabaseException
import com.sildian.apps.togetrail.common.search.SearchRequest
import com.sildian.apps.togetrail.features.entities.event.EventUI
import com.sildian.apps.togetrail.repositories.auth.AuthRepository
import com.sildian.apps.togetrail.repositories.database.event.main.EventRepository
import com.sildian.apps.togetrail.usecases.mappers.toUIModel
import javax.inject.Inject
import kotlin.jvm.Throws

interface SearchEventsUseCase {
    @Throws(AuthException::class, DatabaseException::class, IllegalStateException::class)
    suspend operator fun invoke(request: SearchRequest): List<EventUI>
}

class SearchEventsUseCaseImpl @Inject constructor(
    private val authRepository: AuthRepository,
    private val eventRepository: EventRepository,
) : SearchEventsUseCase {

    override suspend operator fun invoke(request: SearchRequest): List<EventUI> =
        when (request) {
            is SearchRequest.Default ->
                eventRepository.getNextEvents()
            is SearchRequest.FromAuthor ->
                eventRepository.getEventsFromAuthor(authorId = request.authorId)
            is SearchRequest.NearbyLocation ->
                eventRepository.getEventsNearbyLocation(location = request.location)
            is SearchRequest.AroundPosition ->
                eventRepository.getEventsAroundPosition(
                    position = request.position.toGeoLocation(),
                    radiusMeters = request.radiusMeters,
                )
        }.map {
            it.toUIModel(currentUserId = authRepository.getCurrentUser()?.id)
        }
}