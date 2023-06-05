package com.sildian.apps.togetrail.common.utils

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
    year: Year = Year.of(nextInt(from = 1970, until = 2100)),
    month: Month = Month.values().random(),
    day: Int = nextInt(from = 1, until = month.length(year.isLeap)),
    hour: Int = nextInt(from = 0, until = 24),
    minute: Int = nextInt(from = 0, until = 60),
    second: Int = nextInt(from = 0, until = 60),
): LocalDateTime =
    LocalDateTime.of(
        year.value,
        month.value,
        day,
        hour,
        minute,
        second,
    )

fun Random.nextLocalDate(
    year: Year = Year.of(nextInt(from = 1970, until = 2100)),
    month: Month = Month.values().random(),
    day: Int = nextInt(from = 1, until = month.length(year.isLeap)),
): LocalDate =
    LocalDate.of(
        year.value,
        month.value,
        day,
    )