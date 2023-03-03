package com.sildian.apps.togetrail.repositories.database

sealed interface DatabaseCollectionInfo {
    val collectionName: String

    sealed class Hiker : DatabaseCollectionInfo {
        override val collectionName: String = "hiker"

        object Main : Hiker()
    }
}
