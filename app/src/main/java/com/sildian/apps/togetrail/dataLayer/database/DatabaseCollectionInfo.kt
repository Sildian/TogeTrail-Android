package com.sildian.apps.togetrail.dataLayer.database

sealed interface DatabaseCollectionInfo {
    val collectionName: String

    sealed class HikerCollection : DatabaseCollectionInfo {
        override val collectionName: String = "hikers"
        data object Main : HikerCollection()
        sealed class SubCollections : HikerCollection() {
            abstract val subCollectionName: String
            data object HistoryItemSubCollection : SubCollections() {
                override val subCollectionName: String = "historyItems"
            }
            data object AttendedEventSubCollection : SubCollections() {
                override val subCollectionName: String = "attendedEvents"
            }
            data object LikedTrailSubCollection : SubCollections() {
                override val subCollectionName: String = "likedTrails"
            }
            data object MarkedTrailSubCollection : SubCollections() {
                override val subCollectionName: String = "markedTrails"
            }
            data object ConversationSubCollection : SubCollections() {
                override val subCollectionName: String = "conversations"
            }
        }
    }

    sealed class EventCollection : DatabaseCollectionInfo {
        override val collectionName: String = "events"
        data object Main : EventCollection()
        sealed class SubCollections : EventCollection() {
            abstract val subCollectionName: String
            data object AttachedTrailSubCollection : SubCollections() {
                override val subCollectionName: String = "attachedTrails"
            }
            data object RegisteredHikerSubCollection : SubCollections() {
                override val subCollectionName: String = "registeredHikers"
            }
            data object MessageSubCollection : SubCollections() {
                override val subCollectionName: String = "messages"
            }
        }
    }

    sealed class TrailCollection : DatabaseCollectionInfo {
        override val collectionName: String = "trails"
        data object Main : TrailCollection()
        sealed class SubCollections : TrailCollection() {
            abstract val subCollectionName: String
            data object TrailPointSubCollection : SubCollections() {
                override val subCollectionName: String = "trailPoints"
            }
            data object TrailPointOfInterestSubCollection : SubCollections() {
                override val subCollectionName: String = "trailPointsOfInterest"
            }
        }
    }

    sealed class ConversationCollection : DatabaseCollectionInfo {
        override val collectionName: String = "conversations"
        data object Main : ConversationCollection()
        sealed class SubCollections : ConversationCollection() {
            abstract val subCollectionName: String
            data object MessageSubCollection : SubCollections() {
                override val subCollectionName: String = "messages"
            }
        }
    }
}
