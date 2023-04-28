package com.sildian.apps.togetrail.common.core

import java.time.Duration
import kotlin.random.Random

fun Random.nextDuration(
    seconds: Int = nextInt(from = 0, until = Int.MAX_VALUE),
): Duration =
    Duration.ofSeconds(seconds.toLong())