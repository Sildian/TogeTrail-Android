package com.sildian.apps.togetrail.hiker.model.support

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.sildian.apps.togetrail.event.model.core.Event
import com.sildian.apps.togetrail.hiker.model.core.Hiker
import com.sildian.apps.togetrail.hiker.model.core.HikerHistoryItem
import com.sildian.apps.togetrail.hiker.model.core.HikerHistoryType

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
     * Gets all history items
     * @param hikerId : the id of the hiker
     * @return a query
     */

    fun getHistoryItems(hikerId: String): Query =
        getHistoryItemSubCollection(hikerId)

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
     * Gets a list of history items to delete, matching the given type and itemsId
     * @param type : the type of history item to delete
     * @param relatedItemId : the id of the related item to delete (the related event or trail)
     * @return a query
     */

    fun getHistoryItemsToDelete(hikerId: String, type: HikerHistoryType, relatedItemId: String): Query =
        getHistoryItemSubCollection(hikerId)
            .whereEqualTo("type", type)
            .whereEqualTo("itemId", relatedItemId)

    /**
     * Adds an item to the hiker's history
     * @param hikerId : the id of the hiker
     * @param historyItem : the history item
     * @return a task result
     */

    fun addHistoryItem(hikerId: String, historyItem: HikerHistoryItem) : Task<DocumentReference> =
        getHistoryItemSubCollection(hikerId).add(historyItem)

    /**
     * Deletes an history item
     * @param hikerId : the id of the hiker
     * @param historyItemId : the id of history item to delete
     * @return a task result
     */

    fun deleteHistoryItem(hikerId: String, historyItemId: String): Task<Void> =
        getHistoryItemSubCollection(hikerId).document(historyItemId).delete()

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