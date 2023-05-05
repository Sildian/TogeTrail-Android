package com.sildian.apps.togetrail.repositories.database.event.registeredHiker

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.sildian.apps.togetrail.repositories.database.DatabaseCollectionInfo
import com.sildian.apps.togetrail.repositories.database.entities.event.EventHiker
import javax.inject.Inject

class EventRegisteredHikerDatabaseService @Inject constructor(
    private val firebaseFirestore: FirebaseFirestore,
) {

    private val collectionInfo =
        DatabaseCollectionInfo
            .EventCollection
            .SubCollections
            .RegisteredHikerSubCollection

    private fun collection(eventId: String) =
        firebaseFirestore
            .collection(collectionInfo.collectionName)
            .document(eventId)
            .collection(collectionInfo.subCollectionName)

    fun getRegisteredHikers(eventId: String): Query =
        collection(eventId = eventId)

    fun addOrUpdateRegisteredHiker(eventId: String, hiker: EventHiker): Task<Void>? =
        hiker.id?.let { hikerId ->
            collection(eventId = eventId)
                .document(hikerId)
                .set(hiker)
        }

    fun deleteRegisteredHiker(eventId: String, hikerId: String): Task<Void> =
        collection(eventId = eventId)
            .document(hikerId)
            .delete()
}