package com.sildian.apps.togetrail.repositories.database

sealed interface DatabaseCollectionInfo {
    val collectionName: String

    sealed class HikerCollection : DatabaseCollectionInfo {
        override val collectionName: String = "hiker"
        object Main : HikerCollection()
        sealed class SubCollections : HikerCollection() {
            abstract val subCollectionName: String
            object HistoryItemSubCollection : SubCollections() {
                override val subCollectionName: String = "historyItem"
            }
            object AttendedEventSubCollection : SubCollections() {
                override val subCollectionName: String = "attendedEvent"
            }
            object LikedTrailSubCollection : SubCollections() {
                override val subCollectionName: String = "likedTrail"
            }
            object MarkedTrailSubCollection : SubCollections() {
                override val subCollectionName: String = "markedTrail"
            }
            object ConversationSubCollection : SubCollections() {
                override val subCollectionName: String = "conversation"
            }
        }
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

    sealed class ConversationCollection : DatabaseCollectionInfo {
        override val collectionName: String = "conversation"
        object Main : ConversationCollection()
        sealed class SubCollections : ConversationCollection() {
            abstract val subCollectionName: String
            object MessageSubCollection : SubCollections() {
                override val subCollectionName: String = "message"
            }
        }
    }
}
