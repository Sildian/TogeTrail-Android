package com.sildian.apps.togetrail.hiker.data.source

import com.google.firebase.firestore.DocumentReference
import com.sildian.apps.togetrail.chat.data.models.Duo
import com.sildian.apps.togetrail.chat.data.models.Message
import com.sildian.apps.togetrail.event.data.models.Event
import com.sildian.apps.togetrail.hiker.data.models.Hiker
import com.sildian.apps.togetrail.hiker.data.models.HikerHistoryItem
import com.sildian.apps.togetrail.hiker.data.models.HikerHistoryType
import com.sildian.apps.togetrail.trail.data.models.Trail
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/*************************************************************************************************
 * Repository for Hiker
 ************************************************************************************************/

/***************************************Definition***********************************************/

interface HikerRepository {

    /**
     * Gets an hiker reference
     * @param hikerId : the hiker's id
     * @return the document reference
     */

    fun getHikerReference(hikerId: String): DocumentReference

    /**
     * Gets an hiker
     * @param hikerId : the hiker's id
     * @return the obtained hiker
     * @throws Exception if the request fails
     */

    @Throws(Exception::class)
    suspend fun getHiker(hikerId: String): Hiker?

    /**
     * Updates an hiker
     * @param hiker : the hiker to update
     * @throws Exception if the request fails
     */

    @Throws(Exception::class)
    suspend fun updateHiker(hiker: Hiker)

    /**
     * Deletes an hiker as well as the related sub items
     * @param hiker : the hiker to delete
     * @throws Exception if the request fails
     */

    @Throws(Exception::class)
    suspend fun deleteHiker(hiker: Hiker)

    /**
     * Adds an hiker's history item
     * @param hikerId : the hiker's id
     * @param historyItem : the history item to add
     * @throws Exception if the request fails
     */

    @Throws(Exception::class)
    suspend fun addHikerHistoryItem(hikerId: String, historyItem: HikerHistoryItem)

    /**
     * Deletes all history items matching the given type and item id
     * @param type : the type of history item to delete
     * @param relatedItemId : the id of the related item to delete (the related event or trail)
     * @throws Exception if the request fails
     */

    @Throws(Exception::class)
    suspend fun deleteHikerHistoryItems(hikerId: String, type: HikerHistoryType, relatedItemId: String)

    /**
     * Creates or updates an hiker's attended event
     * @param hikerId : the hiker's id
     * @param event : the event to create or update
     * @throws Exception if the request fails
     */

    @Throws(Exception::class)
    suspend fun updateHikerAttendedEvent(hikerId:String, event: Event)

    /**
     * Deletes an hiker's attended event
     * @param hikerId : the hiker's id
     * @param eventId : the event's id to delete
     * @throws Exception if the request fails
     */

    @Throws(Exception::class)
    suspend fun deleteHikerAttendedEvent(hikerId: String, eventId: String)

    /**
     * Creates or updates a trail liked by the hiker
     * @param hikerId : the hiker's id
     * @param trail : the trail to create or update
     * @throws Exception if the request fails
     */

    @Throws(Exception::class)
    suspend fun updateHikerLikedTrail(hikerId:String, trail: Trail)

    /**
     * Deletes a trail liked by the hiker
     * @param hikerId : the hiker's id
     * @param trailId : the trail's id to delete
     * @throws Exception if the request fails
     */

    @Throws(Exception::class)
    suspend fun deleteHikerLikedTrail(hikerId: String, trailId: String)

    /**
     * Creates or updates a trail marked by the hiker
     * @param hikerId : the hiker's id
     * @param trail : the trail to create or update
     * @throws Exception if the request fails
     */

    @Throws(Exception::class)
    suspend fun updateHikerMarkedTrail(hikerId:String, trail: Trail)

    /**
     * Deletes a trail marked by the hiker
     * @param hikerId : the hiker's id
     * @param trailId : the trail's id to delete
     * @throws Exception if the request fails
     */

    @Throws(Exception::class)
    suspend fun deleteHikerMarkedTrail(hikerId: String, trailId: String)

    /**
     * Gets the chat between the two given users if exist
     * @param hikerId : the id of the user
     * @param interlocutorId : the id of the interlocutor
     * @return a Duo chat or null if no chat exist between the two users
     * @throws Exception if the request fails
     */

    @Throws(Exception::class)
    suspend fun getChatBetweenUsers(hikerId: String, interlocutorId: String): Duo?

    /**
     * Creates or updates the given chat
     * @param hikerId : the id of the hiker
     * @param duo : the chat group
     * @return a task
     * @throws Exception if the request fails
     */

    @Throws(Exception::class)
    suspend fun createOrUpdateHikerChat(hikerId: String, duo: Duo)

    /**
     * Deletes the chat for the given hiker and interlocutor
     * @param hikerId : the id of the hiker
     * @param interlocutorId : the id of the interlocutor
     * @return a task
     * @throws Exception if the request fails
     */

    @Throws(Exception::class)
    suspend fun deleteHikerChat(hikerId: String, interlocutorId: String)

    /**
     * Gets the last message for the given hiker and interlocutor
     * @param hikerId : the id of the hiker
     * @param interlocutorId : the id of the interlocutor
     * @return a message or null if no message exist
     * @throws Exception if the request fails
     */

    @Throws(Exception::class)
    suspend fun getLastHikerMessage(hikerId: String, interlocutorId: String): Message?

    /**
     * Creates or updates the given message
     * @param hikerId : the if of the hiker
     * @param interlocutorId : the if of the interlocutor
     * @param message : the message
     * @return a task
     * @throws Exception if the request fails
     */

    @Throws(Exception::class)
    suspend fun createOrUpdateHikerMessage(hikerId: String, interlocutorId: String, message: Message)

    /**
     * Deletes the given message
     * @param hikerId : the id of the hiker
     * @param interlocutorId : the id of the interlocutor
     * @param messageId : the id of the message
     * @return a task
     * @throws Exception if the request fails
     */

    @Throws(Exception::class)
    suspend fun deleteHikerMessage(hikerId: String, interlocutorId: String, messageId: String)
}

/************************************Injection module********************************************/

@Module
@InstallIn(ViewModelComponent::class)
object HikerRepositoryModule {

    @Provides
    fun provideRealHikerRepository(): HikerRepository = RealHikerRepository()
}

/*********************************Real implementation*******************************************/

@ViewModelScoped
class RealHikerRepository @Inject constructor(): HikerRepository {

    override fun getHikerReference(hikerId: String): DocumentReference =
        HikerFirebaseQueries.getHiker(hikerId)

    override suspend fun getHiker(hikerId: String): Hiker? =
        HikerFirebaseQueries
            .getHiker(hikerId)
            .get()
            .await()
            ?.toObject(Hiker::class.java)

    override suspend fun updateHiker(hiker: Hiker) {
        HikerFirebaseQueries
            .createOrUpdateHiker(hiker)
            .await()
    }

    override suspend fun deleteHiker(hiker: Hiker) {
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

    override suspend fun addHikerHistoryItem(hikerId: String, historyItem: HikerHistoryItem) {
        HikerFirebaseQueries
            .addHistoryItem(hikerId, historyItem)
            .await()
    }

    override suspend fun deleteHikerHistoryItems(hikerId: String, type: HikerHistoryType, relatedItemId: String) {
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

    override suspend fun updateHikerAttendedEvent(hikerId: String, event: Event) {
        HikerFirebaseQueries
            .updateAttendedEvent(hikerId, event)
            .await()
    }

    override suspend fun deleteHikerAttendedEvent(hikerId: String, eventId: String) {
        HikerFirebaseQueries
            .deleteAttendedEvent(hikerId, eventId)
            .await()
    }

    override suspend fun updateHikerLikedTrail(hikerId: String, trail: Trail) {
        HikerFirebaseQueries
            .updateLikedTrail(hikerId, trail)
            .await()
    }

    override suspend fun deleteHikerLikedTrail(hikerId: String, trailId: String) {
        HikerFirebaseQueries
            .deleteLikedTrail(hikerId, trailId)
            .await()
    }

    override suspend fun updateHikerMarkedTrail(hikerId: String, trail: Trail) {
        HikerFirebaseQueries
            .updateMarkedTrail(hikerId, trail)
            .await()
    }

    override suspend fun deleteHikerMarkedTrail(hikerId: String, trailId: String) {
        HikerFirebaseQueries
            .deleteMarkedTrail(hikerId, trailId)
            .await()
    }

    override suspend fun getChatBetweenUsers(hikerId: String, interlocutorId: String): Duo? =
        HikerFirebaseQueries
            .getChat(hikerId, interlocutorId)
            .get()
            .await()
            .toObject(Duo::class.java)

    override suspend fun createOrUpdateHikerChat(hikerId: String, duo: Duo) {
        HikerFirebaseQueries
            .createOrUpdateChat(hikerId, duo)
            .await()
    }

    override suspend fun deleteHikerChat(hikerId: String, interlocutorId: String) {
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

    override suspend fun getLastHikerMessage(hikerId: String, interlocutorId: String): Message? =
        HikerFirebaseQueries
            .getLastMessage(hikerId, interlocutorId)
            .get()
            .await()
            .documents.firstOrNull()?.toObject(Message::class.java)

    override suspend fun createOrUpdateHikerMessage(hikerId: String, interlocutorId: String, message: Message) {
        HikerFirebaseQueries
            .createOrUpdateMessage(hikerId, interlocutorId, message)
            .await()
    }

    override suspend fun deleteHikerMessage(hikerId: String, interlocutorId: String, messageId: String) {
        HikerFirebaseQueries
            .deleteMessage(hikerId, interlocutorId, messageId)
            .await()
    }
}