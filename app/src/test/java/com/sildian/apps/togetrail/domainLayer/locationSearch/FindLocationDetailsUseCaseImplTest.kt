package com.sildian.apps.togetrail.domainLayer.locationSearch

import com.sildian.apps.togetrail.common.core.location.nextLocation
import com.sildian.apps.togetrail.common.network.LocationSearchException
import com.sildian.apps.togetrail.common.utils.nextString
import com.sildian.apps.togetrail.dataLayer.locationSearch.LocationSearchRepository
import com.sildian.apps.togetrail.dataLayer.locationSearch.LocationSearchRepositoryFake
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test
import kotlin.random.Random

class FindLocationDetailsUseCaseImplTest {

    private fun initUseCase(
        locationSearchRepository: LocationSearchRepository = LocationSearchRepositoryFake(),
    ): FindLocationDetailsUseCase =
        FindLocationDetailsUseCaseImpl(
            locationSearchRepository = locationSearchRepository,
        )

    @Test(expected = LocationSearchException::class)
    fun `GIVEN LocationSearchException error from LocationSearchRepository WHEN invoke THEN throw LocationSearchException`() = runTest {
        // Given
        val locationSearchRepository = LocationSearchRepositoryFake(
            error = LocationSearchException(),
        )
        val useCase = initUseCase(
            locationSearchRepository = locationSearchRepository,
        )

        // When
        useCase(locationId = Random.nextString())
    }

    @Test
    fun `GIVEN location WHEN invoke THEN result is given location`() = runTest {
        // Given
        val location = Random.nextLocation()
        val locationSearchRepository = LocationSearchRepositoryFake(
            location = location,
        )
        val useCase = initUseCase(
            locationSearchRepository = locationSearchRepository,
        )

        // When
        val result = useCase(locationId = Random.nextString())

        // Then
        assertEquals(location, result)
    }
}