package com.sildian.apps.togetrail.domainLayer.locationSearch

import com.sildian.apps.togetrail.common.core.location.nextLocationPredictionsList
import com.sildian.apps.togetrail.common.network.LocationSearchException
import com.sildian.apps.togetrail.common.search.LocationSearchPrecision
import com.sildian.apps.togetrail.common.utils.nextString
import com.sildian.apps.togetrail.dataLayer.locationSearch.LocationSearchRepository
import com.sildian.apps.togetrail.dataLayer.locationSearch.LocationSearchRepositoryFake
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test
import kotlin.random.Random

class FindAutoCompleteLocationPredictionsUseCaseImplTest {

    private fun initUseCase(
        locationSearchRepository: LocationSearchRepository = LocationSearchRepositoryFake(),
    ): FindAutocompleteLocationPredictionsUseCase =
        FindAutoCompleteLocationPredictionsUseCaseImpl(
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
        useCase(
            query = Random.nextString(),
            precision = LocationSearchPrecision.entries.random(),
        )
    }

    @Test
    fun `GIVEN locationPredictions WHEN invoke THEN result is given locationPredictions`() = runTest {
        // Given
        val locationPredictions = Random.nextLocationPredictionsList()
        val locationSearchRepository = LocationSearchRepositoryFake(
            locationPredictions = locationPredictions,
        )
        val useCase = initUseCase(
            locationSearchRepository = locationSearchRepository,
        )

        // When
        val result = useCase(
            query = Random.nextString(),
            precision = LocationSearchPrecision.entries.random(),
        )

        // Then
        assertEquals(locationPredictions, result)
    }
}