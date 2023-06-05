package com.sildian.apps.togetrail.common.utils

import org.junit.Assert.*
import org.junit.Test
import java.util.Calendar
import kotlin.random.Random

class DateUtilsTest {

    @Test
    fun `GIVEN date WHEN converting to LocalDateTime and to date THEN result is given date`() {
        // Given
        val date = Random.nextDate()

        // When
        val localDateTime = date.toLocalDateTime()
        val result = localDateTime.toDate()

        // Then
        val expectedResult = date
        assertEquals(expectedResult, result)
    }

    @Test
    fun `GIVEN date WHEN converting to LocalDate and to date THEN result is given date to start of day`() {
        // Given
        val date = Random.nextDate()

        // When
        val localDate = date.toLocalDate()
        val result = localDate.toDate()

        // Then
        val expectedResult = Calendar.getInstance().apply {
            time = date
            set(Calendar.MILLISECOND, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.HOUR_OF_DAY, 0)
        }.time
        assertEquals(expectedResult, result)
    }
}