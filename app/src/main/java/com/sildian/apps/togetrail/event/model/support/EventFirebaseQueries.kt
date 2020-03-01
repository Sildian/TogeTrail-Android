package com.sildian.apps.togetrail.event.model.support

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.sildian.apps.togetrail.event.model.core.Event

/*************************************************************************************************
 * Provides with Firebase queries on Event
 ************************************************************************************************/

object EventFirebaseQueries {

    /****************************************Callbacks*******************************************/

    interface OnEventsQueryResultListener{

        /**
         * This event is triggered when a list of events is successfully returned from the query
         * @param events : the resulted list of events
         */

        fun onEventsQueryResult(events:List<Event>)
    }

    /*********************************Collection references**************************************/

    private const val COLLECTION_NAME="event"
    private fun getCollection() = FirebaseFirestore.getInstance().collection(COLLECTION_NAME)

    /*************************************Queries************************************************/

    /**
     * Gets the events from the database
     * @return a task result
     */

    fun getEvents(): Query =
        getCollection().orderBy("beginDate", Query.Direction.ASCENDING)

    /**
     * Gets a given event
     * @param id : the id of the event
     * @return a task result
     */

    fun getEvent(id:String): Task<DocumentSnapshot> =
        getCollection().document(id).get()

    /**
     * Creates a new event in the database
     * @param event : the event
     * @return a task result
     */

    fun createEvent(event:Event):Task<DocumentReference> =
        getCollection().add(event)

    /**
     * Updates an existing event in the database
     * @param event : the event
     * @return a task result
     */

    fun updateEvent(event:Event):Task<Void> =
        getCollection().document(event.id!!).set(event)
}