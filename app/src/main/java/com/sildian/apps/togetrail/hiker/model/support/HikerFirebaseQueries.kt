package com.sildian.apps.togetrail.hiker.model.support

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.sildian.apps.togetrail.event.model.core.Event
import com.sildian.apps.togetrail.hiker.model.core.Hiker
import com.sildian.apps.togetrail.hiker.model.core.HikerHistoryItem

/*************************************************************************************************
 * Provides with Firebase queries on Hiker
 ************************************************************************************************/

object HikerFirebaseQueries {

    /*********************************Collection references**************************************/

    private const val COLLECTION_NAME="hiker"
    private const val SUB_COLLECTION_HISTORY_ITEM="hikerHistoryItem"
    private const val SUB_COLLECTION_ATTENDED_EVENT_NAME="attendedEvent"
    private fun getCollection() = FirebaseFirestore.getInstance().collection(COLLECTION_NAME)
    private fun getHistoryItemSubCollection(hikerId:String) =
        getCollection().document(hikerId).collection(SUB_COLLECTION_HISTORY_ITEM)
    private fun getAttendedEventSubCollection(hikerId:String) =
        getCollection().document(hikerId).collection(SUB_COLLECTION_ATTENDED_EVENT_NAME)

    /*************************************Queries************************************************/

    /**
     * Gets a given hiker
     * @param id : the id of the hiker
     * @return a document reference
     */

    fun getHiker(id:String): DocumentReference =
        getCollection().document(id)

    /**
     * Creates or updates the given hiker
     * @param hiker : the hiker
     * @return a task result
     */

    fun createOrUpdateHiker(hiker: Hiker):Task<Void> =
        getCollection().document(hiker.id).set(hiker)

    /**
     * Deletes a hiker
     * @param hiker : the hiker
     * @return a task result
     */

    fun deleteHiker(hiker:Hiker) : Task<Void> =
        getCollection().document(hiker.id).delete()

    /**
     * Gets the last history items
     * @param hikerId : the id of the hiker
     * @return a query
     */

    fun getLastHistoryItems(hikerId: String) : Query =
        getHistoryItemSubCollection(hikerId)
            .orderBy("date", Query.Direction.DESCENDING)
            .limit(20)

    /**
     * Adds an item to the hiker's history
     * @param hikerId : the id of the hiker
     * @param historyItem : the history item
     */

    fun addHistoryItem(hikerId: String, historyItem: HikerHistoryItem) : Task<DocumentReference> =
        getHistoryItemSubCollection(hikerId).add(historyItem)

    /**
     * Gets all events for which the hiker attended
     * @param hikerId : the id of the hiker
     * @return a query
     */

    fun getAttendedEvents(hikerId:String): Query =
        getAttendedEventSubCollection(hikerId)

    /**
     * Updates an event for which the hiker attended
     * @param hikerId : the id of the hiker
     * @param event : the event to update
     * @return a task result
     */

    fun updateAttendedEvent(hikerId: String, event: Event):Task<Void> =
        getAttendedEventSubCollection(hikerId).document(event.id.toString()).set(event)

    /**
     * Deletes an event for which the hiker attended
     * @param hikerId : the id of the hiker
     * @param eventId : the id of the event
     * @return a task result
     */

    fun deleteAttendedEvent(hikerId:String, eventId:String):Task<Void> =
        getAttendedEventSubCollection(hikerId).document(eventId).delete()
}