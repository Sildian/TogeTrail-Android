package com.sildian.apps.togetrail.common.network

inline fun <T> locationSearchOperation(block: () -> T): T =
    try {
        block()
    } catch (e: Throwable) {
        throw LocationSearchException()
    }

class LocationSearchException : Exception("An unknown error occurred.")