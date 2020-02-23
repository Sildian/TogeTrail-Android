package com.sildian.apps.togetrail.trail.model.support

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import com.sildian.apps.togetrail.trail.model.core.Trail

/*************************************************************************************************
 * Provides with Firebase queries on Trail
 ************************************************************************************************/

object TrailFirebaseQueries {

    /****************************************Callbacks*******************************************/

    interface OnTrailsQueryResultListener{

        /**
         * This event is triggered when a list of trails is successfully returned from the query
         * @param trails : the resulted list of trails
         */

        fun onTrailsQueryResult(trails:List<Trail>)
    }

    /*********************************Collection references**************************************/

    private const val COLLECTION_NAME="trail"
    private fun getCollection() = FirebaseFirestore.getInstance().collection(COLLECTION_NAME)

    /*************************************Queries************************************************/

    /**
     * Gets the trails from the database
     * @return a task result
     */

    fun getTrails():Query =
        getCollection().orderBy("creationDate", Query.Direction.DESCENDING)

    /**
     * Gets a given trail
     * @param id : the id of the trail
     * @return a task result
     */

    fun getTrail(id:String):Task<DocumentSnapshot> =
        getCollection().document(id).get()

    /**
     * Creates a new trail in the database
     * @param trail : the trail
     * @return a task result
     */

    fun createTrail(trail:Trail):Task<DocumentReference> =
        getCollection().add(trail)

    /**
     * Updates an existing trail in the database
     * @param trail : the trail
     * @return a task result
     */

    fun updateTrail(trail:Trail):Task<Void> =
        getCollection().document(trail.id!!).set(trail)
}