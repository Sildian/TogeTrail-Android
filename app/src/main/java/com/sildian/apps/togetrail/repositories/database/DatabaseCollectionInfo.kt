package com.sildian.apps.togetrail.repositories.database

sealed interface DatabaseCollectionInfo {
    val collectionName: String

    sealed class HikerCollection : DatabaseCollectionInfo {
        override val collectionName: String = "hiker"

        object Main : HikerCollection()
    }

    sealed class EventCollection : DatabaseCollectionInfo {
        override val collectionName: String = "event"

        object Main : EventCollection()
    }

    sealed class TrailCollection : DatabaseCollectionInfo {
        override val collectionName: String = "trail"

        object Main : TrailCollection()
    }
}
