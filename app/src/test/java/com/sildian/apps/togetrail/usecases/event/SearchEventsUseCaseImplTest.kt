package com.sildian.apps.togetrail.usecases.event

import com.firebase.geofire.GeoLocation
import com.sildian.apps.togetrail.common.core.geo.nextPosition
import com.sildian.apps.togetrail.common.core.location.Location
import com.sildian.apps.togetrail.common.core.location.nextLocation
import com.sildian.apps.togetrail.common.network.AuthException
import com.sildian.apps.togetrail.common.network.DatabaseException
import com.sildian.apps.togetrail.common.search.SearchRequest
import com.sildian.apps.togetrail.common.utils.nextString
import com.sildian.apps.togetrail.repositories.auth.AuthRepository
import com.sildian.apps.togetrail.repositories.auth.AuthRepositoryFake
import com.sildian.apps.togetrail.repositories.database.entities.event.Event
import com.sildian.apps.togetrail.repositories.database.entities.event.nextEvent
import com.sildian.apps.togetrail.repositories.database.event.main.EventRepository
import com.sildian.apps.togetrail.repositories.database.event.main.EventRepositoryFake
import com.sildian.apps.togetrail.usecases.mappers.toUIModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test
import kotlin.random.Random

@OptIn(ExperimentalCoroutinesApi::class)
class SearchEventsUseCaseImplTest {

    private fun initUseCase(
        authRepository: AuthRepository = AuthRepositoryFake(),
        eventRepository: EventRepository = EventRepositoryFake(),
    ): SearchEventsUseCase = SearchEventsUseCaseImpl(
        authRepository = authRepository,
        eventRepository = eventRepository,
    )

    @Test(expected = AuthException::class)
    fun `GIVEN AuthException error WHEN invoke THEN throws AuthException`() = runTest {
        // Given
        val useCase = initUseCase(
            authRepository = AuthRepositoryFake(error = AuthException.UnknownException())
        )

        // When
        useCase(SearchRequest.Default)
    }

    @Test(expected = DatabaseException::class)
    fun `GIVEN DatabaseException error WHEN invoke THEN throws DatabaseException`() = runTest {
        // Given
        val useCase = initUseCase(
            eventRepository = EventRepositoryFake(error = DatabaseException.UnknownException())
        )

        // When
        useCase(SearchRequest.Default)
    }

    @Test
    fun `GIVEN events WHEN invoking default events THEN result is default events`() = runTest {
        // Given
        val events = Random.nextEventsMap()
        val useCase = initUseCase(
            eventRepository = EventRepositorySearchFake(events = events)
        )

        // When
        val result = useCase(SearchRequest.Default)

        // Then
        val expectedResult = listOf(
            events[SearchRequestType.DEFAULT]?.toUIModel(currentUserId = Random.nextString())
        )
        assertEquals(expectedResult, result)
    }

    @Test
    fun `GIVEN events WHEN invoking events from author THEN result is events from author`() = runTest {
        // Given
        val events = Random.nextEventsMap()
        val useCase = initUseCase(
            eventRepository = EventRepositorySearchFake(events = events)
        )

        // When
        val result = useCase(SearchRequest.FromAuthor(authorId = Random.nextString()))

        // Then
        val expectedResult = listOf(
            events[SearchRequestType.FROM_AUTHOR]?.toUIModel(currentUserId = Random.nextString())
        )
        assertEquals(expectedResult, result)
    }

    @Test
    fun `GIVEN events WHEN invoking events nearby location THEN result is events nearby location`() = runTest {
        // Given
        val events = Random.nextEventsMap()
        val useCase = initUseCase(
            eventRepository = EventRepositorySearchFake(events = events)
        )

        // When
        val result = useCase(SearchRequest.NearbyLocation(location = Random.nextLocation()))

        // Then
        val expectedResult = listOf(
            events[SearchRequestType.NEARBY_LOCATION]?.toUIModel(currentUserId = Random.nextString())
        )
        assertEquals(expectedResult, result)
    }

    @Test
    fun `GIVEN events WHEN invoking events around position THEN result is events around position`() = runTest {
        // Given
        val events = Random.nextEventsMap()
        val useCase = initUseCase(
            eventRepository = EventRepositorySearchFake(events = events)
        )

        // When
        val result = useCase(
            SearchRequest.AroundPosition(position = Random.nextPosition(), radiusMeters = Random.nextDouble())
        )

        // Then
        val expectedResult = listOf(
            events[SearchRequestType.AROUND_POSITION]?.toUIModel(currentUserId = Random.nextString())
        )
        assertEquals(expectedResult, result)
    }

    private fun Random.nextEventsMap(): Map<SearchRequestType, Event> =
        mapOf(
            SearchRequestType.DEFAULT to nextEvent(),
            SearchRequestType.FROM_AUTHOR to nextEvent(),
            SearchRequestType.NEARBY_LOCATION to nextEvent(),
            SearchRequestType.AROUND_POSITION to nextEvent(),
        )

    private enum class SearchRequestType {
        DEFAULT,
        FROM_AUTHOR,
        NEARBY_LOCATION,
        AROUND_POSITION,
    }

    private class EventRepositorySearchFake(
        private val error: DatabaseException? = null,
        private val events: Map<SearchRequestType, Event>,
    ) : EventRepository {

        override suspend fun getEvent(id: String): Event =
            error?.let { throw it } ?: events.values.random()

        override suspend fun getEvents(ids: List<String>): List<Event> =
            error?.let { throw it } ?: events.values.toList()

        override suspend fun getNextEvents(): List<Event> {
            if (error != null) throw error
            val event = events[SearchRequestType.DEFAULT] ?: return emptyList()
            return listOf(event)
        }

        override suspend fun getEventsFromAuthor(authorId: String): List<Event> {
            if (error != null) throw error
            val event = events[SearchRequestType.FROM_AUTHOR] ?: return emptyList()
            return listOf(event)
        }

        override suspend fun getEventsNearbyLocation(location: Location): List<Event> {
            if (error != null) throw error
            val event = events[SearchRequestType.NEARBY_LOCATION] ?: return emptyList()
            return listOf(event)
        }

        override suspend fun getEventsAroundPosition(
            position: GeoLocation,
            radiusMeters: Double
        ): List<Event> {
            if (error != null) throw error
            val event = events[SearchRequestType.AROUND_POSITION] ?: return emptyList()
            return listOf(event)
        }

        override suspend fun addEvent(event: Event) = Unit

        override suspend fun updateEvent(event: Event) = Unit
    }
}