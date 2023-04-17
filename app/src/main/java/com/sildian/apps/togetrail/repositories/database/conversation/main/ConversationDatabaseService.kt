package com.sildian.apps.togetrail.repositories.database.conversation.main

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.sildian.apps.togetrail.repositories.database.DatabaseCollectionInfo
import com.sildian.apps.togetrail.repositories.database.entities.conversation.Conversation
import javax.inject.Inject

class ConversationDatabaseService @Inject constructor(
    firebaseFirestore: FirebaseFirestore,
) {

    private val collectionInfo =
        DatabaseCollectionInfo.ConversationCollection.Main

    private val collection =
        firebaseFirestore.collection(collectionInfo.collectionName)

    fun getConversation(id: String): DocumentReference =
        collection.document(id)

    fun addConversation(conversation: Conversation): Task<DocumentReference> =
        collection.add(conversation)

    fun deleteConversation(id: String): Task<Void> =
        collection.document(id).delete()
}