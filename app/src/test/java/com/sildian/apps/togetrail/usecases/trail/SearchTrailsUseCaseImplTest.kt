package com.sildian.apps.togetrail.usecases.trail

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
import com.sildian.apps.togetrail.repositories.database.entities.trail.Trail
import com.sildian.apps.togetrail.repositories.database.entities.trail.nextTrail
import com.sildian.apps.togetrail.repositories.database.trail.main.TrailRepository
import com.sildian.apps.togetrail.repositories.database.trail.main.TrailRepositoryFake
import com.sildian.apps.togetrail.usecases.mappers.toUIModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test
import kotlin.random.Random

@OptIn(ExperimentalCoroutinesApi::class)
class SearchTrailsUseCaseImplTest {

    private fun initUseCase(
        authRepository: AuthRepository = AuthRepositoryFake(),
        trailRepository: TrailRepository = TrailRepositoryFake(),
    ): SearchTrailsUseCase = SearchTrailsUseCaseImpl(
        authRepository = authRepository,
        trailRepository = trailRepository,
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
            trailRepository = TrailRepositoryFake(error = DatabaseException.UnknownException())
        )

        // When
        useCase(SearchRequest.Default)
    }

    @Test
    fun `GIVEN trails WHEN invoking default trails THEN result is default trails`() = runTest {
        // Given
        val trails = Random.nextTrailsMap()
        val useCase = initUseCase(
            trailRepository = TrailRepositorySearchFake(trails = trails)
        )

        // When
        val result = useCase(SearchRequest.Default)

        // Then
        val expectedResult = listOf(
            trails[SearchRequestType.DEFAULT]?.toUIModel(currentUserId = Random.nextString())
        )
        assertEquals(expectedResult, result)
    }

    @Test
    fun `GIVEN trails WHEN invoking trails from author THEN result is trails from author`() = runTest {
        // Given
        val trails = Random.nextTrailsMap()
        val useCase = initUseCase(
            trailRepository = TrailRepositorySearchFake(trails = trails)
        )

        // When
        val result = useCase(SearchRequest.FromAuthor(authorId = Random.nextString()))

        // Then
        val expectedResult = listOf(
            trails[SearchRequestType.FROM_AUTHOR]?.toUIModel(currentUserId = Random.nextString())
        )
        assertEquals(expectedResult, result)
    }

    @Test
    fun `GIVEN trails WHEN invoking trails nearby location THEN result is trails nearby location`() = runTest {
        // Given
        val trails = Random.nextTrailsMap()
        val useCase = initUseCase(
            trailRepository = TrailRepositorySearchFake(trails = trails)
        )

        // When
        val result = useCase(SearchRequest.NearbyLocation(location = Random.nextLocation()))

        // Then
        val expectedResult = listOf(
            trails[SearchRequestType.NEARBY_LOCATION]?.toUIModel(currentUserId = Random.nextString())
        )
        assertEquals(expectedResult, result)
    }

    @Test
    fun `GIVEN trails WHEN invoking trails around position THEN result is trails around position`() = runTest {
        // Given
        val trails = Random.nextTrailsMap()
        val useCase = initUseCase(
            trailRepository = TrailRepositorySearchFake(trails = trails)
        )

        // When
        val result = useCase(
            SearchRequest.AroundPosition(position = Random.nextPosition(), radiusMeters = Random.nextDouble())
        )

        // Then
        val expectedResult = listOf(
            trails[SearchRequestType.AROUND_POSITION]?.toUIModel(currentUserId = Random.nextString())
        )
        assertEquals(expectedResult, result)
    }

    private fun Random.nextTrailsMap(): Map<SearchRequestType, Trail> =
        mapOf(
            SearchRequestType.DEFAULT to nextTrail(),
            SearchRequestType.FROM_AUTHOR to nextTrail(),
            SearchRequestType.NEARBY_LOCATION to nextTrail(),
            SearchRequestType.AROUND_POSITION to nextTrail(),
        )

    private enum class SearchRequestType {
        DEFAULT,
        FROM_AUTHOR,
        NEARBY_LOCATION,
        AROUND_POSITION,
    }

    private class TrailRepositorySearchFake(
        private val error: DatabaseException? = null,
        private val trails: Map<SearchRequestType, Trail>,
    ) : TrailRepository {

        override suspend fun getTrail(id: String): Trail =
            error?.let { throw it } ?: trails.values.random()

        override suspend fun getTrails(ids: List<String>): List<Trail> =
            error?.let { throw it } ?: trails.values.toList()

        override suspend fun getLastTrails(): List<Trail> {
            if (error != null) throw error
            val trail = trails[SearchRequestType.DEFAULT] ?: return emptyList()
            return listOf(trail)
        }

        override suspend fun getTrailsFromAuthor(authorId: String): List<Trail> {
            if (error != null) throw error
            val trail = trails[SearchRequestType.FROM_AUTHOR] ?: return emptyList()
            return listOf(trail)
        }

        override suspend fun getTrailsNearbyLocation(location: Location): List<Trail> {
            if (error != null) throw error
            val trail = trails[SearchRequestType.NEARBY_LOCATION] ?: return emptyList()
            return listOf(trail)
        }

        override suspend fun getTrailsAroundPosition(
            position: GeoLocation,
            radiusMeters: Double
        ): List<Trail> {
            if (error != null) throw error
            val trail = trails[SearchRequestType.AROUND_POSITION] ?: return emptyList()
            return listOf(trail)
        }

        override suspend fun addTrail(trail: Trail) = Unit

        override suspend fun updateTrail(trail: Trail) = Unit
    }
}