package com.sildian.apps.togetrail.repositories.database.conversation

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.sildian.apps.togetrail.repositories.database.DatabaseCollectionInfo
import com.sildian.apps.togetrail.repositories.database.entities.conversation.Message
import javax.inject.Inject

class ConversationMessageDatabaseService @Inject constructor(
    private val firebaseFirestore: FirebaseFirestore,
) {

    private val collectionInfo =
        DatabaseCollectionInfo
            .ConversationCollection
            .SubCollections
            .MessageSubCollection

    private fun collection(conversationId: String) =
        firebaseFirestore
            .collection(collectionInfo.collectionName)
            .document(conversationId)
            .collection(collectionInfo.subCollectionName)

    fun getMessages(conversationId: String): Query =
        collection(conversationId = conversationId)
            .orderBy("creationDate", Query.Direction.ASCENDING)

    fun addOrUpdateMessage(conversationId: String, message: Message): Task<Void>? =
        message.id?.let { messageId ->
            collection(conversationId = conversationId)
                .document(messageId)
                .set(message)
        }

    fun deleteMessage(conversationId: String, messageId: String): Task<Void> =
        collection(conversationId = conversationId)
            .document(messageId)
            .delete()
}