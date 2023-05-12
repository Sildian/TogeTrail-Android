package com.sildian.apps.togetrail.common.core

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Month
import java.time.Year
import java.util.*
import kotlin.random.Random

fun Random.nextDate(
    millis: Long = nextLong(from = 0, until = Long.MAX_VALUE),
): Date =
    Date(millis)

fun Random.nextLocalDateTime(
    year: Int = nextInt(from = Year.MIN_VALUE, until = Year.MAX_VALUE + 1),
    month: Month = Month.values().random(),
    day: Int = nextInt(from = 1, until = 32),
    hour: Int = nextInt(from = 0, until = 24),
    minute: Int = nextInt(from = 0, until = 60),
    second: Int = nextInt(from = 0, until = 60),
): LocalDateTime =
    LocalDateTime.of(
        year,
        month,
        day,
        hour,
        minute,
        second,
    )

fun Random.nextLocalDate(
    year: Int = nextInt(from = Year.MIN_VALUE, until = Year.MAX_VALUE + 1),
    month: Month = Month.values().random(),
    day: Int = nextInt(from = 1, until = 32),
): LocalDate =
    LocalDate.of(
        year,
        month,
        day,
    )