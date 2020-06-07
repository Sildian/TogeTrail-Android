package com.sildian.apps.togetrail.event.model.support

import com.google.firebase.firestore.DocumentReference
import com.sildian.apps.togetrail.event.model.core.Event
import com.sildian.apps.togetrail.hiker.model.core.Hiker
import com.sildian.apps.togetrail.trail.model.core.Trail
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

/*************************************************************************************************
 * Repository for Event
 ************************************************************************************************/

object EventRepository {

    /**
     * Gets an event reference
     * @param eventId : the event's id
     * @return the document reference
     */

    fun getEventReference(eventId:String):DocumentReference =
        EventFirebaseQueries.getEvent(eventId)

    /**
     * Gets an event
     * @param eventId : the event's id
     * @return the obtained event
     */

    @Throws(Exception::class)
    suspend fun getEvent(eventId:String): Event? =
        withContext(Dispatchers.IO) {
            try {
                EventFirebaseQueries
                    .getEvent(eventId)
                    .get()
                    .await()
                    ?.toObject(Event::class.java)
            } catch (e: Exception) {
                throw e
            }
        }

    /**
     * Adds an event
     * @param event : the event to add
     * @return the event's id
     */

    @Throws(Exception::class)
    suspend fun addEvent(event:Event):String? =
        withContext(Dispatchers.IO){
            try{
                EventFirebaseQueries
                    .createEvent(event)
                    .await()
                    .id
            }catch (e: Exception) {
                throw e
            }
        }

    /**
     * Updates an event
     * @param event : the event to update
     */

    @Throws(Exception::class)
    suspend fun updateEvent(event:Event){
        withContext(Dispatchers.IO) {
            try {
                EventFirebaseQueries
                    .updateEvent(event)
                    .await()
            } catch (e: Exception) {
                throw e
            }
        }
    }

    /**
     * Updates an event's attached trail
     * @param eventId : the event's id
     * @param trail : the trail to attach
     */

    @Throws(Exception::class)
    suspend fun updateEventAttachedTrail(eventId:String, trail:Trail){
        withContext(Dispatchers.IO) {
            try {
                EventFirebaseQueries
                    .updateAttachedTrail(eventId, trail)
                    .await()
            } catch (e: Exception) {
                throw e
            }
        }
    }

    /**
     * Deletes an event's attached trail
     * @param eventId : the event's id
     * @param trailId : the trail's id
     */

    @Throws(Exception::class)
    suspend fun deleteEventAttachedTrail(eventId:String, trailId:String){
        withContext(Dispatchers.IO) {
            try {
                EventFirebaseQueries
                    .deleteAttachedTrail(eventId, trailId)
                    .await()
            } catch (e: Exception) {
                throw e
            }
        }
    }

    /**
     * Updates an event's registered hiker
     * @param eventId : the event's id
     * @param hiker : the hiker to register
     */

    @Throws(Exception::class)
    suspend fun updateEventRegisteredHiker(eventId:String, hiker:Hiker){
        withContext(Dispatchers.IO) {
            try {
                EventFirebaseQueries
                    .updateRegisteredHiker(eventId, hiker)
                    .await()
            } catch (e: Exception) {
                throw e
            }
        }
    }

    /**
     * Deletes an event's registered hiker
     * @param eventId : the event's id
     * @param hikerId : the hiker's id
     */

    @Throws(Exception::class)
    suspend fun deleteEventRegisteredHiker(eventId:String, hikerId:String){
        withContext(Dispatchers.IO) {
            try {
                EventFirebaseQueries
                    .deleteRegisteredHiker(eventId, hikerId)
                    .await()
            } catch (e: Exception) {
                throw e
            }
        }
    }
}