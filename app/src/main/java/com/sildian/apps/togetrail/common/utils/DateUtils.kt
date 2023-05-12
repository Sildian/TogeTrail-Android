package com.sildian.apps.togetrail.common.utils

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

fun Date.toLocalDateTime(): LocalDateTime =
    this.toInstant()
        .atZone(ZoneId.systemDefault())
        .toLocalDateTime()

fun LocalDateTime.toDate(): Date =
    Date.from(
        this.atZone(ZoneId.systemDefault()).toInstant()
    )

fun Date.toLocalDate(): LocalDate =
    this.toInstant()
        .atZone(ZoneId.systemDefault())
        .toLocalDate()

fun LocalDate.toDate(): Date =
    Date.from(
        this.atStartOfDay(ZoneId.systemDefault()).toInstant()
    )
