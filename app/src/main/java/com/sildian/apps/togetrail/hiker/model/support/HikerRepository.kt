package com.sildian.apps.togetrail.hiker.model.support

import com.google.firebase.firestore.DocumentReference
import com.sildian.apps.togetrail.event.model.core.Event
import com.sildian.apps.togetrail.hiker.model.core.Hiker
import com.sildian.apps.togetrail.hiker.model.core.HikerHistoryItem
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

    fun getHikerReference(hikerId: String):DocumentReference =
        HikerFirebaseQueries.getHiker(hikerId)

    /**
     * Gets an hiker
     * @param hikerId : the hiker's id
     * @return the obtained hiker
     */

    @Throws(Exception::class)
    suspend fun getHiker(hikerId:String):Hiker? =
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
    suspend fun updateHiker(hiker:Hiker){
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
     * Deletes an hiker
     * @param hiker : the hiker to delete
     */

    @Throws(Exception::class)
    suspend fun deleteHiker(hiker:Hiker){
        withContext(Dispatchers.IO){
            try{
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
    suspend fun addHikerHistoryItem(hikerId:String, historyItem: HikerHistoryItem){
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