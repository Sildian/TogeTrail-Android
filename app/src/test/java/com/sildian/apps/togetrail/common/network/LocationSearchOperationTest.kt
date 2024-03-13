package com.sildian.apps.togetrail.common.network

import com.sildian.apps.togetrail.common.utils.nextString
import org.junit.Assert.*
import org.junit.Test
import kotlin.random.Random

class LocationSearchOperationTest {

    @Test(expected = LocationSearchException::class)
    fun `GIVEN any exception WHEN locationSearchOperation THEN throw LocationSearchException`() {
        // Given
        val exception = IllegalStateException()

        // When
        locationSearchOperation { throw exception }
    }

    @Test
    fun `GIVEN data WHEN locationSearchOperation THEN return data`() {
        // Given
        val data = Random.nextString()

        // When
        val result = locationSearchOperation { data }

        // Then
        assertEquals(data, result)
    }
}