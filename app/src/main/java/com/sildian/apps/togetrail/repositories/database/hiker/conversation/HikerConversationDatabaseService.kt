package com.sildian.apps.togetrail.repositories.database.hiker.conversation

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.sildian.apps.togetrail.repositories.database.DatabaseCollectionInfo
import com.sildian.apps.togetrail.repositories.database.entities.hiker.HikerConversation
import javax.inject.Inject

class HikerConversationDatabaseService @Inject constructor(
    private val firebaseFirestore: FirebaseFirestore,
) {

    private val collectionInfo =
        DatabaseCollectionInfo
            .HikerCollection
            .SubCollections
            .ConversationSubCollection

    private fun collection(hikerId: String) =
        firebaseFirestore
            .collection(collectionInfo.collectionName)
            .document(hikerId)
            .collection(collectionInfo.subCollectionName)

    fun getConversations(hikerId: String): Query =
        collection(hikerId = hikerId)
            .orderBy(
                FieldPath.of("lastMessage", "creationDate"), Query.Direction.DESCENDING
            )

    fun addOrUpdateConversation(hikerId: String, conversation: HikerConversation): Task<Void>? =
        conversation.id?.let { conversationId ->
            collection(hikerId = hikerId)
                .document(conversationId)
                .set(conversation)
        }

    fun deleteConversation(hikerId: String, conversationId: String): Task<Void> =
        collection(hikerId = hikerId)
            .document(conversationId)
            .delete()
}