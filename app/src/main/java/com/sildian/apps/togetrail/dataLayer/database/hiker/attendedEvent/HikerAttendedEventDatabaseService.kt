package com.sildian.apps.togetrail.dataLayer.database.hiker.attendedEvent

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.sildian.apps.togetrail.dataLayer.database.DatabaseCollectionInfo
import com.sildian.apps.togetrail.dataLayer.database.entities.hiker.HikerEvent
import javax.inject.Inject

class HikerAttendedEventDatabaseService @Inject constructor(
    private val firebaseFirestore: FirebaseFirestore,
) {

    private val collectionInfo =
        DatabaseCollectionInfo
            .HikerCollection
            .SubCollections
            .AttendedEventSubCollection

    private fun collection(hikerId: String) =
        firebaseFirestore
            .collection(collectionInfo.collectionName)
            .document(hikerId)
            .collection(collectionInfo.subCollectionName)

    fun getAttendedEvents(hikerId: String): Query =
        collection(hikerId = hikerId)
            .whereEqualTo("canceled", false)
            .orderBy("startDate", Query.Direction.ASCENDING)

    fun addOrUpdateAttendedEvent(hikerId: String, event: HikerEvent): Task<Void>? =
        event.id?.let { eventId ->
            collection(hikerId = hikerId)
                .document(eventId)
                .set(event)
        }

    fun deleteAttendedEvent(hikerId: String, eventId: String): Task<Void> =
        collection(hikerId = hikerId)
            .document(eventId)
            .delete()
}