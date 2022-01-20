package com.sildian.apps.togetrail.hiker.model.dataRepository

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.sildian.apps.togetrail.chat.model.core.Duo
import com.sildian.apps.togetrail.chat.model.core.Message
import com.sildian.apps.togetrail.event.model.core.Event
import com.sildian.apps.togetrail.hiker.model.core.Hiker
import com.sildian.apps.togetrail.hiker.model.core.HikerHistoryItem
import com.sildian.apps.togetrail.hiker.model.core.HikerHistoryType
import com.sildian.apps.togetrail.trail.model.core.Trail
/*************************************************************************************************
 * Provides with Firebase queries on Hiker
 ************************************************************************************************/

object HikerFirebaseQueries {

    /*********************************Collection references**************************************/

    private const val COLLECTION_NAME="hiker"
    private const val SUB_COLLECTION_HISTORY_ITEM="hikerHistoryItem"
    private const val SUB_COLLECTION_ATTENDED_EVENT_NAME="attendedEvent"
    private const val SUB_COLLECTION_LIKED_TRAIL_NAME="likedTrail"
    private const val SUB_COLLECTION_MARKED_TRAIL_NAME="markedTrail"
    private const val SUB_COLLECTION_CHAT_NAME = "chat"
    private const val SUB_COLLECTION_MESSAGE_NAME = "message"
    private fun getCollection() = FirebaseFirestore.getInstance().collection(COLLECTION_NAME)
    private fun getHistoryItemSubCollection(hikerId:String) =
        getCollection().document(hikerId).collection(SUB_COLLECTION_HISTORY_ITEM)
    private fun getAttendedEventSubCollection(hikerId:String) =
        getCollection().document(hikerId).collection(SUB_COLLECTION_ATTENDED_EVENT_NAME)
    private fun getLikedTrailSubCollection(hikerId: String) =
        getCollection().document(hikerId).collection(SUB_COLLECTION_LIKED_TRAIL_NAME)
    private fun getMarkedTrailSubCollection(hikerId: String) =
        getCollection().document(hikerId).collection(SUB_COLLECTION_MARKED_TRAIL_NAME)
    private fun getChatSubCollection(hikerId: String) =
        getCollection().document(hikerId).collection(SUB_COLLECTION_CHAT_NAME)
    private fun getMessageSubCollection(hikerId: String, interlocutorId: String) =
        getChatSubCollection(hikerId).document(interlocutorId).collection(
            SUB_COLLECTION_MESSAGE_NAME
        )

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

    /**
     * Gets all trails liked by the hiker
     * @param hikerId : the id of the hiker
     * @return a query
     */

    fun getLikedTrails(hikerId: String): Query =
        getLikedTrailSubCollection(hikerId)

    /**
     * Updates a trail liked by the hiker
     * @param hikerId : the id of the hiker
     * @param trail : the trail to update
     * @return a task result
     */

    fun updateLikedTrail(hikerId: String, trail: Trail): Task<Void> {

        /*Simplifies the trail stored : clears all the points and poi*/

        trail.trailTrack.trailPoints.clear()
        trail.trailTrack.trailPointsOfInterest.clear()

        /*Then updates the trail*/

        return getLikedTrailSubCollection(hikerId).document(trail.id.toString()).set(trail)
    }

    /**
     * Deletes a trail liked by the hiker
     * @param hikerId : the id of the hiker
     * @param trailId : the id of the trail
     * @return a task result
     */

    fun deleteLikedTrail(hikerId: String, trailId: String): Task<Void> =
        getLikedTrailSubCollection(hikerId).document(trailId).delete()

    /**
     * Gets all trails marked by the hiker
     * @param hikerId : the id of the hiker
     * @return a query
     */

    fun getMarkedTrails(hikerId: String): Query =
        getMarkedTrailSubCollection(hikerId)

    /**
     * Updates a trail marked by the hiker
     * @param hikerId : the id of the hiker
     * @param trail : the trail to update
     * @return a task result
     */

    fun updateMarkedTrail(hikerId: String, trail: Trail): Task<Void> {

        /*Simplifies the trail stored : clears all the points and poi*/

        trail.trailTrack.trailPoints.clear()
        trail.trailTrack.trailPointsOfInterest.clear()

        /*Then updates the trail*/

        return getMarkedTrailSubCollection(hikerId).document(trail.id.toString()).set(trail)
    }

    /**
     * Deletes a trail marked by the hiker
     * @param hikerId : the id of the hiker
     * @param trailId : the id of the trail
     * @return a task result
     */

    fun deleteMarkedTrail(hikerId: String, trailId: String): Task<Void> =
        getMarkedTrailSubCollection(hikerId).document(trailId).delete()

    /**
     * Gets the list of chats for the given user
     * @param hikerId : the id of the hiker
     * @return a query
     */

    fun getChats(hikerId: String): Query =
        getChatSubCollection(hikerId)
            .orderBy(FieldPath.of("lastMessage", "date"), Query.Direction.DESCENDING)

    /**
     * Gets the chat between the two given users if exist
     * @param hikerId : the id of the user
     * @param interlocutorId : the id of the interlocutor
     * @return a document reference
     */

    fun getChat(hikerId: String, interlocutorId: String): DocumentReference =
        getChatSubCollection(hikerId).document(interlocutorId)

    /**
     * Creates or updates the given chat
     * @param hikerId : the id of the hiker
     * @param duo : the chat group
     * @return a task
     */

    fun createOrUpdateChat(hikerId: String, duo: Duo): Task<Void> =
        getChatSubCollection(hikerId).document(duo.interlocutorId).set(duo)

    /**
     * Deletes the chat for the given hiker and interlocutor
     * @param hikerId : the id of the hiker
     * @param interlocutorId : the id of the interlocutor
     * @return a task
     */

    fun deleteChat(hikerId: String, interlocutorId: String): Task<Void> =
        getChatSubCollection(hikerId).document(interlocutorId).delete()

    /**
     * Gets the list of messages for the the given hiker and interlocutor
     * @param hikerId : the id of the hiker
     * @param interlocutorId : the id of the interlocutor
     * @return a query
     */

    fun getMessages(hikerId: String, interlocutorId: String): Query =
        getMessageSubCollection(hikerId, interlocutorId)
            .orderBy("date", Query.Direction.ASCENDING)

    /**
     * Gets the last message for the given hiker and interlocutor
     * @param hikerId : the id of the hiker
     * @param interlocutorId : the id of the interlocutor
     * @return a query
     */

    fun getLastMessage(hikerId: String, interlocutorId: String): Query =
        getMessageSubCollection(hikerId, interlocutorId)
            .orderBy("date", Query.Direction.DESCENDING)
            .limit(1)

    /**
     * Creates or updates the given message
     * @param hikerId : the id of the hiker
     * @param interlocutorId : the if of the interlocutor
     * @param message : the message
     * @return a task
     */

    fun createOrUpdateMessage(hikerId: String, interlocutorId: String, message: Message): Task<Void> =
        getMessageSubCollection(hikerId, interlocutorId).document(message.id).set(message)

    /**
     * Deletes the given message
     * @param hikerId : the id of the hiker
     * @param interlocutorId : the id of the interlocutor
     * @param messageId : the id of the message
     * @return a task
     */

    fun deleteMessage(hikerId: String, interlocutorId: String, messageId: String): Task<Void> =
        getMessageSubCollection(hikerId, interlocutorId).document(messageId).delete()
}

