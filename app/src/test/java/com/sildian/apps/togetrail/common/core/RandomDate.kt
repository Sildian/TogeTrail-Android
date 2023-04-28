package com.sildian.apps.togetrail.common.core

import java.util.Date
import kotlin.random.Random

fun Random.nextDate(
    millis: Long = nextLong(from = 0, until = Long.MAX_VALUE),
): Date =
    Date(millis)