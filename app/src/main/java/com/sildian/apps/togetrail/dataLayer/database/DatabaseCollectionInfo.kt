package com.sildian.apps.togetrail.dataLayer.database

sealed interface DatabaseCollectionInfo {
    val collectionName: String

    sealed class HikerCollection : DatabaseCollectionInfo {
        override val collectionName: String = "hiker"
        data object Main : HikerCollection()
        sealed class SubCollections : HikerCollection() {
            abstract val subCollectionName: String
            data object HistoryItemSubCollection : SubCollections() {
                override val subCollectionName: String = "historyItem"
            }
            data object AttendedEventSubCollection : SubCollections() {
                override val subCollectionName: String = "attendedEvent"
            }
            data object LikedTrailSubCollection : SubCollections() {
                override val subCollectionName: String = "likedTrail"
            }
            data object MarkedTrailSubCollection : SubCollections() {
                override val subCollectionName: String = "markedTrail"
            }
            data object ConversationSubCollection : SubCollections() {
                override val subCollectionName: String = "conversation"
            }
        }
    }

    sealed class EventCollection : DatabaseCollectionInfo {
        override val collectionName: String = "event"
        data object Main : EventCollection()
        sealed class SubCollections : EventCollection() {
            abstract val subCollectionName: String
            data object AttachedTrailSubCollection : SubCollections() {
                override val subCollectionName: String = "attachedTrail"
            }
            data object RegisteredHikerSubCollection : SubCollections() {
                override val subCollectionName: String = "registeredHiker"
            }
            data object MessageSubCollection : SubCollections() {
                override val subCollectionName: String = "message"
            }
        }
    }

    sealed class TrailCollection : DatabaseCollectionInfo {
        override val collectionName: String = "trail"
        data object Main : TrailCollection()
        sealed class SubCollections : TrailCollection() {
            abstract val subCollectionName: String
            data object TrailPointSubCollection : SubCollections() {
                override val subCollectionName: String = "trailPoint"
            }
            data object TrailPointOfInterestSubCollection : SubCollections() {
                override val subCollectionName: String = "trailPointOfInterest"
            }
        }
    }

    sealed class ConversationCollection : DatabaseCollectionInfo {
        override val collectionName: String = "conversation"
        data object Main : ConversationCollection()
        sealed class SubCollections : ConversationCollection() {
            abstract val subCollectionName: String
            data object MessageSubCollection : SubCollections() {
                override val subCollectionName: String = "message"
            }
        }
    }
}
