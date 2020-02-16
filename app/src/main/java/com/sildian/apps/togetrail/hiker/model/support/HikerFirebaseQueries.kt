package com.sildian.apps.togetrail.hiker.model.support

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.sildian.apps.togetrail.hiker.model.core.Hiker

/*************************************************************************************************
 * Provides with Firebase queries on Hiker
 ************************************************************************************************/

object HikerFirebaseQueries {

    /*********************************Collection references**************************************/

    private const val COLLECTION_NAME="hiker"
    private fun getCollection() = FirebaseFirestore.getInstance().collection(COLLECTION_NAME)

    /*************************************Queries************************************************/

    /**
     * Gets a given hiker
     * @param id : the id of the hiker
     * @return a task result
     */

    fun getHiker(id:String): Task<DocumentSnapshot> =
        getCollection().document(id).get()

    /**
     * Creates or updates the given hiker
     * @param hiker : the hiker
     * @return a task result
     */

    fun createOrUpdateHiker(hiker: Hiker):Task<Void> =
        getCollection().document(hiker.id).set(hiker)
}