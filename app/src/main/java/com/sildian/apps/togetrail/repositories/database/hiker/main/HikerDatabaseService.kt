package com.sildian.apps.togetrail.repositories.database.hiker.main

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.sildian.apps.togetrail.repositories.database.DatabaseCollectionInfo
import com.sildian.apps.togetrail.repositories.database.entities.hiker.Hiker
import javax.inject.Inject

class HikerDatabaseService @Inject constructor(
    firebaseFirestore: FirebaseFirestore,
) {

    private val collectionInfo = DatabaseCollectionInfo.HikerCollection.Main
    private val collection = firebaseFirestore.collection(collectionInfo.collectionName)

    fun getHiker(id:String): DocumentReference =
        collection.document(id)

    fun getHikers(ids: List<String>): Query =
        collection.whereIn(FieldPath.documentId(), ids)

    fun getHikersWithNameContainingText(text: String, hikerNameToExclude: String = ""): Query =
        collection
            .whereNotEqualTo("name", hikerNameToExclude)
            .whereGreaterThanOrEqualTo("name", text)
            .whereLessThanOrEqualTo("name", "$text\uf8ff")
            .orderBy("name")

    fun addOrUpdateHiker(hiker: Hiker): Task<Void>? =
        hiker.id?.let { hikerId ->
            collection.document(hikerId).set(hiker)
        }

    fun deleteHiker(hikerId: String) : Task<Void> =
        collection.document(hikerId).delete()
}