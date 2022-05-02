package com.sildian.apps.togetrail.event.model.dataRepository

import com.google.firebase.firestore.DocumentReference
import com.sildian.apps.togetrail.event.model.core.Event
import com.sildian.apps.togetrail.hiker.model.core.Hiker
import com.sildian.apps.togetrail.chat.model.core.Message
import com.sildian.apps.togetrail.trail.model.core.Trail
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

/*************************************************************************************************
 * Repository for Event
 ************************************************************************************************/

/***************************************Definition***********************************************/

interface EventRepository {

    /**
     * Gets an event reference
     * @param eventId : the event's id
     * @return the document reference
     */

    fun getEventReference(eventId:String): DocumentReference

    /**
     * Gets an event
     * @param eventId : the event's id
     * @return the obtained event
     * @throws Exception if the request fails
     */

    suspend fun getEvent(eventId:String): Event?

    /**
     * Adds an event
     * @param event : the event to add
     * @return the event's id
     * @throws Exception if the request fails
     */

    suspend fun addEvent(event:Event): String?

    /**
     * Updates an event
     * @param event : the event to update
     * @throws Exception if the request fails
     */

    suspend fun updateEvent(event:Event)

    /**
     * Updates an event's attached trail
     * @param eventId : the event's id
     * @param trail : the trail to attach
     * @throws Exception if the request fails
     */

    suspend fun updateEventAttachedTrail(eventId:String, trail:Trail)

    /**
     * Deletes an event's attached trail
     * @param eventId : the event's id
     * @param trailId : the trail's id
     * @throws Exception if the request fails
     */

    suspend fun deleteEventAttachedTrail(eventId:String, trailId:String)

    /**
     * Updates an event's registered hiker
     * @param eventId : the event's id
     * @param hiker : the hiker to register
     * @throws Exception if the request fails
     */

    suspend fun updateEventRegisteredHiker(eventId:String, hiker:Hiker)

    /**
     * Deletes an event's registered hiker
     * @param eventId : the event's id
     * @param hikerId : the hiker's id
     * @throws Exception if the request fails
     */

    suspend fun deleteEventRegisteredHiker(eventId:String, hikerId:String)

    /**
     * Creates or updates a message in the event's chat
     * @param eventId : the event's id
     * @param message : the message
     * @throws Exception if the request fails
     */

    suspend fun createOrUpdateEventMessage(eventId:String, message: Message)

    /**
     * Deletes a message from the event's chat
     * @param eventId : the event's id
     * @param messageId : the message's id
     * @throws Exception if the request fails
     */

    suspend fun deleteEventMessage(eventId:String, messageId: String)
}

/************************************Injection module********************************************/

@Module
@InstallIn(ViewModelComponent::class)
object EventRepositoryModule {

    @Provides
    fun provideRealEventRepository(): EventRepository = RealEventRepository()
}

/*********************************Real implementation*******************************************/

@ViewModelScoped
class RealEventRepository @Inject constructor(): EventRepository {

    override fun getEventReference(eventId:String): DocumentReference =
        EventFirebaseQueries.getEvent(eventId)

    @Throws(Exception::class)
    override suspend fun getEvent(eventId:String): Event? =
        withContext(Dispatchers.IO) {
            try {
                EventFirebaseQueries
                    .getEvent(eventId)
                    .get()
                    .await()
                    ?.toObject(Event::class.java)
            }
            catch (e: Exception) {
                throw e
            }
        }

    @Throws(Exception::class)
    override suspend fun addEvent(event:Event): String? =
        withContext(Dispatchers.IO){
            try{
                EventFirebaseQueries
                    .createEvent(event)
                    .await()
                    .id
            }
            catch (e: Exception) {
                throw e
            }
        }

    @Throws(Exception::class)
    override suspend fun updateEvent(event:Event) {
        withContext(Dispatchers.IO) {
            try {
                EventFirebaseQueries
                    .updateEvent(event)
                    .await()
            }
            catch (e: Exception) {
                throw e
            }
        }
    }

    @Throws(Exception::class)
    override suspend fun updateEventAttachedTrail(eventId:String, trail:Trail) {
        withContext(Dispatchers.IO) {
            try {
                EventFirebaseQueries
                    .updateAttachedTrail(eventId, trail)
                    .await()
            }
            catch (e: Exception) {
                throw e
            }
        }
    }

    @Throws(Exception::class)
    override suspend fun deleteEventAttachedTrail(eventId:String, trailId:String) {
        withContext(Dispatchers.IO) {
            try {
                EventFirebaseQueries
                    .deleteAttachedTrail(eventId, trailId)
                    .await()
            }
            catch (e: Exception) {
                throw e
            }
        }
    }

    @Throws(Exception::class)
    override suspend fun updateEventRegisteredHiker(eventId:String, hiker:Hiker) {
        withContext(Dispatchers.IO) {
            try {
                EventFirebaseQueries
                    .updateRegisteredHiker(eventId, hiker)
                    .await()
            }
            catch (e: Exception) {
                throw e
            }
        }
    }

    @Throws(Exception::class)
    override suspend fun deleteEventRegisteredHiker(eventId:String, hikerId:String) {
        withContext(Dispatchers.IO) {
            try {
                EventFirebaseQueries
                    .deleteRegisteredHiker(eventId, hikerId)
                    .await()
            }
            catch (e: Exception) {
                throw e
            }
        }
    }

    @Throws(Exception::class)
    override suspend fun createOrUpdateEventMessage(eventId:String, message: Message) {
        withContext(Dispatchers.IO) {
            try {
                EventFirebaseQueries
                    .createOrUpdateMessage(eventId, message)
                    .await()
            }
            catch (e: Exception) {
                throw e
            }
        }
    }

    @Throws(Exception::class)
    override suspend fun deleteEventMessage(eventId:String, messageId: String) {
        withContext(Dispatchers.IO) {
            try {
                EventFirebaseQueries
                    .deleteMessage(eventId, messageId)
                    .await()
            }
            catch (e: Exception) {
                throw e
            }
        }
    }
}