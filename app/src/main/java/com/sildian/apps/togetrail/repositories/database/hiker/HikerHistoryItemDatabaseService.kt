package com.sildian.apps.togetrail.repositories.database.hiker

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.sildian.apps.togetrail.repositories.database.DatabaseCollectionInfo
import com.sildian.apps.togetrail.repositories.database.entities.hiker.HikerHistoryItem
import javax.inject.Inject

class HikerHistoryItemDatabaseService @Inject constructor(
    private val firebaseFirestore: FirebaseFirestore,
) {

    private val collectionInfo =
        DatabaseCollectionInfo
            .HikerCollection
            .SubCollections
            .HikerHistoryItemSubCollection

    private fun collection(hikerId: String) =
        firebaseFirestore
            .collection(collectionInfo.collectionName)
            .document(hikerId)
            .collection(collectionInfo.subCollectionName)

    fun getLastHistoryItems(hikerId: String): Query =
        collection(hikerId = hikerId)
            .orderBy("date", Query.Direction.DESCENDING)
            .limit(20)

    fun getHistoryItemsForTypeAndDetailsId(
        hikerId: String,
        type: HikerHistoryItem.Type,
        detailsId: String,
    ): Query =
        collection(hikerId = hikerId)
            .whereEqualTo("type", type)
            .whereEqualTo(FieldPath.of("details", "id"), detailsId)
            .orderBy("date", Query.Direction.DESCENDING)

    fun addHistoryItem(hikerId: String, historyItem: HikerHistoryItem): Task<DocumentReference> =
        collection(hikerId = hikerId)
            .add(historyItem)

    fun deleteHistoryItem(hikerId: String, historyItemId: String): Task<Void> =
        collection(hikerId = hikerId)
            .document(historyItemId)
            .delete()
}