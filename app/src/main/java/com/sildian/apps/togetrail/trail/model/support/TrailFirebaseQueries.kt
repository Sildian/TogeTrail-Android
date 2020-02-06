package com.sildian.apps.togetrail.trail.model.support

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.sildian.apps.togetrail.trail.model.core.Trail

/*************************************************************************************************
 * Provides with Firebase queries on Trail
 ************************************************************************************************/

object TrailFirebaseQueries {

    /*********************************Collection references**************************************/

    private const val COLLECTION_NAME="trail"
    private fun getCollection() = FirebaseFirestore.getInstance().collection(COLLECTION_NAME)

    /*************************************Queries************************************************/

    /**Creates a new trail in the database**/

    fun createTrail(trail:Trail):Task<DocumentReference> =
        getCollection().add(trail)

    /**Updates an existing trail in the database**/

    fun updateTrail(trail:Trail):Task<Void> =
        getCollection().document(trail.id!!).set(trail)
}