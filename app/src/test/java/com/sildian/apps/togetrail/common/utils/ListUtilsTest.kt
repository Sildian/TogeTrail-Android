package com.sildian.apps.togetrail.common.utils

import org.junit.Assert.*
import org.junit.Test
import kotlin.math.ceil
import kotlin.random.Random

class ListUtilsTest {

    @Test
    fun `GIVEN list containing less than max size WHEN invoking split THEN result is one list`() {
        // Given
        val maxSize = Random.nextInt(from = 1, until = 10)
        val itemsCount = maxSize - Random.nextInt(from = 0, until = maxSize)
        val list: List<Int> = List(size = itemsCount) { it }

        // When
        val result = list.split(maxSize = maxSize)

        // Then
        val expectedNumberOfLists = 1
        assertEquals(expectedNumberOfLists, result.size)
        assertEquals(itemsCount, result.sumOf { it.count() })
    }

    @Test
    fun `GIVEN list containing more than max size WHEN invoking split THEN result is many lists`() {
        // Given
        val maxSize = Random.nextInt(from = 1, until = 10)
        val itemsCount = maxSize + Random.nextInt(from = 0, until = maxSize * 10)
        val list: List<Int> = List(size = itemsCount) { it }

        // When
        val result = list.split(maxSize = maxSize)

        // Then
        val expectedNumberOfLists = ceil((itemsCount.toDouble() / maxSize.toDouble())).toInt()
        assertEquals(expectedNumberOfLists, result.size)
        assertEquals(itemsCount, result.sumOf { it.count() })
    }
}