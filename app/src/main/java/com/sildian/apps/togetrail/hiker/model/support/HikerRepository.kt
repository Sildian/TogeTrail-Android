package com.sildian.apps.togetrail.hiker.model.support

import com.google.firebase.firestore.DocumentReference
import com.sildian.apps.togetrail.event.model.core.Event
import com.sildian.apps.togetrail.hiker.model.core.Hiker
import com.sildian.apps.togetrail.hiker.model.core.HikerHistoryItem
import com.sildian.apps.togetrail.hiker.model.core.HikerHistoryType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

/*************************************************************************************************
 * Repository for Hiker
 ************************************************************************************************/

object HikerRepository {

    /**
     * Gets an hiker reference
     * @param hikerId : the hiker's id
     * @return the document reference
     */

    fun getHikerReference(hikerId: String): DocumentReference =
        HikerFirebaseQueries.getHiker(hikerId)

    /**
     * Gets an hiker
     * @param hikerId : the hiker's id
     * @return the obtained hiker
     */

    @Throws(Exception::class)
    suspend fun getHiker(hikerId: String): Hiker? =
        withContext(Dispatchers.IO) {
            try {
                HikerFirebaseQueries
                    .getHiker(hikerId)
                    .get()
                    .await()
                    ?.toObject(Hiker::class.java)
            } catch (e: Exception) {
                throw e
            }
        }

    /**
     * Updates an hiker
     * @param hiker : the hiker to update
     */

    @Throws(Exception::class)
    suspend fun updateHiker(hiker: Hiker) {
        withContext(Dispatchers.IO) {
            try {
                HikerFirebaseQueries
                    .createOrUpdateHiker(hiker)
                    .await()
            } catch (e: Exception) {
                throw e
            }
        }
    }

    /**
     * Deletes an hiker as well as the related history items and attended events
     * @param hiker : the hiker to delete
     */

    @Throws(Exception::class)
    suspend fun deleteHiker(hiker: Hiker) {
        withContext(Dispatchers.IO) {
            try {
                val historyItems = HikerFirebaseQueries
                    .getHistoryItems(hiker.id)
                    .get()
                    .await()
                historyItems.forEach { historyItem ->
                    HikerFirebaseQueries.deleteHistoryItem(hiker.id, historyItem.id)
                }
                val attendedEvents = HikerFirebaseQueries
                    .getAttendedEvents(hiker.id)
                    .get()
                    .await()
                attendedEvents.forEach { attendedEvent ->
                    HikerFirebaseQueries.deleteAttendedEvent(hiker.id, attendedEvent.id)
                }
                HikerFirebaseQueries
                    .deleteHiker(hiker)
                    .await()
            } catch (e: Exception) {
                throw e
            }
        }
    }

    /**
     * Adds an hiker's history item
     * @param hikerId : the hiker's id
     * @param historyItem : the history item to add
     */

    @Throws(Exception::class)
    suspend fun addHikerHistoryItem(hikerId: String, historyItem: HikerHistoryItem) {
        withContext(Dispatchers.IO) {
            try {
                HikerFirebaseQueries
                    .addHistoryItem(hikerId, historyItem)
                    .await()
            } catch (e: Exception) {
                throw e
            }
        }
    }

    /**
     * Deletes all history items matching the given type and item id
     * @param type : the type of history item to delete
     * @param relatedItemId : the id of the related item to delete (the related event or trail)
     */

    @Throws(Exception::class)
    suspend fun deleteHikerHistoryItems(hikerId: String, type: HikerHistoryType, relatedItemId: String) {
        withContext(Dispatchers.IO) {
            try {
                val historyItemsToDelete =
                    HikerFirebaseQueries
                        .getHistoryItemsToDelete(hikerId, type, relatedItemId)
                        .get()
                        .await()
                historyItemsToDelete.forEach { historyItem ->
                    HikerFirebaseQueries.deleteHistoryItem(hikerId, historyItem.id)
                        .await()
                }
            } catch (e: Exception) {
                throw e
            }
        }
    }

    /**
     * Creates or updates an hiker's attended event
     * @param hikerId : the hiker's id
     * @param event : the event to create or update
     */

    @Throws(Exception::class)
    suspend fun updateHikerAttendedEvent(hikerId:String, event: Event){
        withContext(Dispatchers.IO) {
            try {
                HikerFirebaseQueries
                    .updateAttendedEvent(hikerId, event)
                    .await()
            } catch (e: Exception) {
                throw e
            }
        }
    }

    /**
     * Deletes an hiker's attended event
     * @param hikerId : the hiker's id
     * @param eventId : the event's id to delete
     */

    @Throws(Exception::class)
    suspend fun deleteHikerAttendedEvent(hikerId: String, eventId: String){
        withContext(Dispatchers.IO) {
            try {
                HikerFirebaseQueries
                    .deleteAttendedEvent(hikerId, eventId)
                    .await()
            } catch (e: Exception) {
                throw e
            }
        }
    }
}