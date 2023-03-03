package com.sildian.apps.togetrail.repositories.database.hiker

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.sildian.apps.togetrail.repositories.database.DatabaseCollectionInfo
import com.sildian.apps.togetrail.repositories.database.entities.hiker.Hiker
import javax.inject.Inject

class HikerDatabaseService @Inject constructor(
    firebaseFirestore: FirebaseFirestore,
) {

    private val collectionInfo = DatabaseCollectionInfo.Hiker.Main
    private val collection = firebaseFirestore.collection(collectionInfo.collectionName)

    fun getHiker(id:String): DocumentReference =
        collection.document(id)

    fun getHikersWithNameContainingText(text: String, hikerNameToExclude: String = ""): Query =
        collection
            .whereNotEqualTo("name", hikerNameToExclude)
            .whereGreaterThanOrEqualTo("name", text)
            .whereLessThanOrEqualTo("name", "$text\uf8ff")
            .orderBy("name")
            .limit(20)

    fun createOrUpdateHiker(hiker: Hiker): Task<Void> =
        collection.document(hiker.id).set(hiker)

    fun deleteHiker(hiker: Hiker) : Task<Void> =
        collection.document(hiker.id).delete()
}