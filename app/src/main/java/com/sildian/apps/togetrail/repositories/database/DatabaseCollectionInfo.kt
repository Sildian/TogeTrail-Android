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
        sealed class SubCollections : EventCollection() {
            abstract val subCollectionName: String
            object AttachedTrailSubCollection : SubCollections() {
                override val subCollectionName: String = "attachedTrail"
            }
            object RegisteredHikerSubCollection : SubCollections() {
                override val subCollectionName: String = "registeredHiker"
            }
            object MessageSubCollection : SubCollections() {
                override val subCollectionName: String = "message"
            }
        }
    }

    sealed class TrailCollection : DatabaseCollectionInfo {
        override val collectionName: String = "trail"
        object Main : TrailCollection()
        sealed class SubCollections : TrailCollection() {
            abstract val subCollectionName: String
            object TrailPointSubCollection : SubCollections() {
                override val subCollectionName: String = "trailPoint"
            }
            object TrailPointOfInterestSubCollection : SubCollections() {
                override val subCollectionName: String = "trailPointOfInterest"
            }
        }
    }
}
