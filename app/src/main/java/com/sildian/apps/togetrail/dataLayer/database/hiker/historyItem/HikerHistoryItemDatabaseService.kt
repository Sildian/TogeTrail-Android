package com.sildian.apps.togetrail.dataLayer.database.hiker.historyItem

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.sildian.apps.togetrail.dataLayer.database.DatabaseCollectionInfo
import com.sildian.apps.togetrail.dataLayer.database.entities.hiker.HikerHistoryItem
import javax.inject.Inject

class HikerHistoryItemDatabaseService @Inject constructor(
    private val firebaseFirestore: FirebaseFirestore,
) {

    private val collectionInfo =
        DatabaseCollectionInfo
            .HikerCollection
            .SubCollections
            .HistoryItemSubCollection

    private fun collection(hikerId: String) =
        firebaseFirestore
            .collection(collectionInfo.collectionName)
            .document(hikerId)
            .collection(collectionInfo.subCollectionName)

    fun getLastHistoryItems(hikerId: String): Query =
        collection(hikerId = hikerId)
            .orderBy("date", Query.Direction.DESCENDING)
            .limit(20)

    fun getHistoryItemsForActionAndItemId(
        hikerId: String,
        action: HikerHistoryItem.Action,
        itemId: String,
    ): Query =
        collection(hikerId = hikerId)
            .whereEqualTo("action", action)
            .whereEqualTo(FieldPath.of("item", "id"), itemId)
            .orderBy("date", Query.Direction.DESCENDING)

    fun addOrUpdateHistoryItem(hikerId: String, historyItem: HikerHistoryItem): Task<Void>? =
        historyItem.id?.let { historyItemId ->
            collection(hikerId = hikerId)
                .document(historyItemId)
                .set(historyItem)
        }

    fun deleteHistoryItem(hikerId: String, historyItemId: String): Task<Void> =
        collection(hikerId = hikerId)
            .document(historyItemId)
            .delete()
}