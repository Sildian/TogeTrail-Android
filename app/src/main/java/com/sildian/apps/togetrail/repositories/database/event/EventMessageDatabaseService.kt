package com.sildian.apps.togetrail.repositories.database.event

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.sildian.apps.togetrail.repositories.database.DatabaseCollectionInfo
import com.sildian.apps.togetrail.repositories.database.entities.event.Message
import javax.inject.Inject

class EventMessageDatabaseService @Inject constructor(
    private val firebaseFirestore: FirebaseFirestore,
) {

    private val collectionInfo =
        DatabaseCollectionInfo
            .EventCollection
            .SubCollections
            .MessageSubCollection

    private fun collection(eventId: String) =
        firebaseFirestore
            .collection(collectionInfo.collectionName)
            .document(eventId)
            .collection(collectionInfo.subCollectionName)

    fun getMessages(eventId: String): Query =
        collection(eventId = eventId)
            .orderBy("creationDate", Query.Direction.ASCENDING)

    fun addOrUpdateMessage(eventId: String, message: Message): Task<Void>? =
        message.id?.let { messageId ->
            collection(eventId = eventId)
                .document(messageId)
                .set(message)
        }

    fun deleteMessage(eventId: String, messageId: String): Task<Void> =
        collection(eventId = eventId)
            .document(messageId)
            .delete()
}