package com.sildian.apps.togetrail.hiker.model.dataRepository

import com.google.firebase.firestore.DocumentReference
import com.sildian.apps.togetrail.chat.model.core.Duo
import com.sildian.apps.togetrail.chat.model.core.Message
import com.sildian.apps.togetrail.event.model.core.Event
import com.sildian.apps.togetrail.hiker.model.core.Hiker
import com.sildian.apps.togetrail.hiker.model.core.HikerHistoryItem
import com.sildian.apps.togetrail.hiker.model.core.HikerHistoryType
import com.sildian.apps.togetrail.trail.model.core.Trail
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

/*************************************************************************************************
 * Repository for Hiker
 ************************************************************************************************/

@ViewModelScoped
class HikerRepository @Inject constructor() {

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
     * @throws Exception if the request fails
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
            }
            catch (e: Exception) {
                throw e
            }
        }

    /**
     * Updates an hiker
     * @param hiker : the hiker to update
     * @throws Exception if the request fails
     */

    @Throws(Exception::class)
    suspend fun updateHiker(hiker: Hiker) {
        withContext(Dispatchers.IO) {
            try {
                HikerFirebaseQueries
                    .createOrUpdateHiker(hiker)
                    .await()
            }
            catch (e: Exception) {
                throw e
            }
        }
    }

    /**
     * Deletes an hiker as well as the related sub items
     * @param hiker : the hiker to delete
     * @throws Exception if the request fails
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
                val likedTrails = HikerFirebaseQueries
                    .getLikedTrails(hiker.id)
                    .get()
                    .await()
                likedTrails.forEach { likedTrail ->
                    HikerFirebaseQueries.deleteLikedTrail(hiker.id, likedTrail.id)
                }
                val markedTrails = HikerFirebaseQueries
                    .getMarkedTrails(hiker.id)
                    .get()
                    .await()
                markedTrails.forEach { markedTrail ->
                    HikerFirebaseQueries.deleteMarkedTrail(hiker.id, markedTrail.id)
                }
                val chats = HikerFirebaseQueries
                    .getChats(hiker.id)
                    .get()
                    .await()
                chats.forEach { chat ->
                    val messages = HikerFirebaseQueries
                        .getMessages(hiker.id, chat.id)
                        .get()
                        .await()
                    messages.forEach { message ->
                        HikerFirebaseQueries.deleteMessage(hiker.id, chat.id, message.id)
                    }
                    HikerFirebaseQueries.deleteChat(hiker.id, chat.id)
                }
                HikerFirebaseQueries
                    .deleteHiker(hiker)
                    .await()
            }
            catch (e: Exception) {
                throw e
            }
        }
    }

    /**
     * Adds an hiker's history item
     * @param hikerId : the hiker's id
     * @param historyItem : the history item to add
     * @throws Exception if the request fails
     */

    @Throws(Exception::class)
    suspend fun addHikerHistoryItem(hikerId: String, historyItem: HikerHistoryItem) {
        withContext(Dispatchers.IO) {
            try {
                HikerFirebaseQueries
                    .addHistoryItem(hikerId, historyItem)
                    .await()
            }
            catch (e: Exception) {
                throw e
            }
        }
    }

    /**
     * Deletes all history items matching the given type and item id
     * @param type : the type of history item to delete
     * @param relatedItemId : the id of the related item to delete (the related event or trail)
     * @throws Exception if the request fails
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
            }
            catch (e: Exception) {
                throw e
            }
        }
    }

    /**
     * Creates or updates an hiker's attended event
     * @param hikerId : the hiker's id
     * @param event : the event to create or update
     * @throws Exception if the request fails
     */

    @Throws(Exception::class)
    suspend fun updateHikerAttendedEvent(hikerId:String, event: Event){
        withContext(Dispatchers.IO) {
            try {
                HikerFirebaseQueries
                    .updateAttendedEvent(hikerId, event)
                    .await()
            }
            catch (e: Exception) {
                throw e
            }
        }
    }

    /**
     * Deletes an hiker's attended event
     * @param hikerId : the hiker's id
     * @param eventId : the event's id to delete
     * @throws Exception if the request fails
     */

    @Throws(Exception::class)
    suspend fun deleteHikerAttendedEvent(hikerId: String, eventId: String){
        withContext(Dispatchers.IO) {
            try {
                HikerFirebaseQueries
                    .deleteAttendedEvent(hikerId, eventId)
                    .await()
            }
            catch (e: Exception) {
                throw e
            }
        }
    }

    /**
     * Creates or updates a trail liked by the hiker
     * @param hikerId : the hiker's id
     * @param trail : the trail to create or update
     * @throws Exception if the request fails
     */

    @Throws(Exception::class)
    suspend fun updateHikerLikedTrail(hikerId:String, trail: Trail){
        withContext(Dispatchers.IO) {
            try {
                HikerFirebaseQueries
                    .updateLikedTrail(hikerId, trail)
                    .await()
            }
            catch (e: Exception) {
                throw e
            }
        }
    }

    /**
     * Deletes a trail liked by the hiker
     * @param hikerId : the hiker's id
     * @param trailId : the trail's id to delete
     * @throws Exception if the request fails
     */

    @Throws(Exception::class)
    suspend fun deleteHikerLikedTrail(hikerId: String, trailId: String){
        withContext(Dispatchers.IO) {
            try {
                HikerFirebaseQueries
                    .deleteLikedTrail(hikerId, trailId)
                    .await()
            }
            catch (e: Exception) {
                throw e
            }
        }
    }

    /**
     * Creates or updates a trail marked by the hiker
     * @param hikerId : the hiker's id
     * @param trail : the trail to create or update
     * @throws Exception if the request fails
     */

    @Throws(Exception::class)
    suspend fun updateHikerMarkedTrail(hikerId:String, trail: Trail){
        withContext(Dispatchers.IO) {
            try {
                HikerFirebaseQueries
                    .updateMarkedTrail(hikerId, trail)
                    .await()
            }
            catch (e: Exception) {
                throw e
            }
        }
    }

    /**
     * Deletes a trail marked by the hiker
     * @param hikerId : the hiker's id
     * @param trailId : the trail's id to delete
     * @throws Exception if the request fails
     */

    @Throws(Exception::class)
    suspend fun deleteHikerMarkedTrail(hikerId: String, trailId: String){
        withContext(Dispatchers.IO) {
            try {
                HikerFirebaseQueries
                    .deleteMarkedTrail(hikerId, trailId)
                    .await()
            }
            catch (e: Exception) {
                throw e
            }
        }
    }

    /**
     * Gets the chat between the two given users if exist
     * @param hikerId : the id of the user
     * @param interlocutorId : the id of the interlocutor
     * @return a Duo chat or null if no chat exist between the two users
     * @throws Exception if the request fails
     */
    
    @Throws(Exception::class)
    suspend fun getChatBetweenUsers(hikerId: String, interlocutorId: String): Duo? =
        withContext(Dispatchers.IO) {
            try {
                HikerFirebaseQueries
                    .getChat(hikerId, interlocutorId)
                    .get()
                    .await()
                    .toObject(Duo::class.java)
            }
            catch (e: Exception) {
                throw e
            }
        }

    /**
     * Creates or updates the given chat
     * @param hikerId : the id of the hiker
     * @param duo : the chat group
     * @return a task
     * @throws Exception if the request fails
     */

    @Throws(Exception::class)
    suspend fun createOrUpdateHikerChat(hikerId: String, duo: Duo) {
        withContext(Dispatchers.IO) {
            try {
                HikerFirebaseQueries
                    .createOrUpdateChat(hikerId, duo)
                    .await()
            }
            catch (e: Exception) {
                throw e
            }
        }
    }

    /**
     * Deletes the chat for the given hiker and interlocutor
     * @param hikerId : the id of the hiker
     * @param interlocutorId : the id of the interlocutor
     * @return a task
     * @throws Exception if the request fails
     */

    @Throws(Exception::class)
    suspend fun deleteHikerChat(hikerId: String, interlocutorId: String) {
        withContext(Dispatchers.IO) {
            try {
                val messages = HikerFirebaseQueries
                    .getMessages(hikerId, interlocutorId)
                    .get()
                    .await()
                messages.forEach { message ->
                    HikerFirebaseQueries.deleteMessage(hikerId, interlocutorId, message.id)
                }
                HikerFirebaseQueries
                    .deleteChat(hikerId, interlocutorId)
                    .await()
            }
            catch (e: Exception) {
                throw e
            }
        }
    }

    /**
     * Gets the last message for the given hiker and interlocutor
     * @param hikerId : the id of the hiker
     * @param interlocutorId : the id of the interlocutor
     * @return a message or null if no message exist
     * @throws Exception if the request fails
     */
    
    @Throws(Exception::class)
    suspend fun getLastHikerMessage(hikerId: String, interlocutorId: String): Message? =
        withContext(Dispatchers.IO) {
            try {
                HikerFirebaseQueries
                    .getLastMessage(hikerId, interlocutorId)
                    .get()
                    .await()
                    .documents.firstOrNull()?.toObject(Message::class.java)
            }
            catch(e: Exception) {
                throw e
            }
        }

    /**
     * Creates or updates the given message
     * @param hikerId : the if of the hiker
     * @param interlocutorId : the if of the interlocutor
     * @param message : the message
     * @return a task
     * @throws Exception if the request fails
     */

    @Throws(Exception::class)
    suspend fun createOrUpdateHikerMessage(hikerId: String, interlocutorId: String, message: Message) {
        withContext(Dispatchers.IO) {
            try {
                HikerFirebaseQueries
                    .createOrUpdateMessage(hikerId, interlocutorId, message)
                    .await()
            }
            catch (e: Exception) {
                throw e
            }
        }
    }

    /**
     * Deletes the given message
     * @param hikerId : the id of the hiker
     * @param interlocutorId : the id of the interlocutor
     * @param messageId : the id of the message
     * @return a task
     * @throws Exception if the request fails
     */

    @Throws(Exception::class)
    suspend fun deleteHikerMessage(hikerId: String, interlocutorId: String, messageId: String) {
        withContext(Dispatchers.IO) {
            try {
                HikerFirebaseQueries
                    .deleteMessage(hikerId, interlocutorId, messageId)
                    .await()
            }
            catch (e: Exception) {
                throw e
            }
        }
    }
}