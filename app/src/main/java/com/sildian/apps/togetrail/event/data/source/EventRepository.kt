package com.sildian.apps.togetrail.event.data.source

import com.google.firebase.firestore.DocumentReference
import com.sildian.apps.togetrail.event.data.models.Event
import com.sildian.apps.togetrail.hiker.data.models.Hiker
import com.sildian.apps.togetrail.chat.data.models.Message
import com.sildian.apps.togetrail.trail.data.models.Trail
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/*************************************************************************************************
 * Repository for Event
 ************************************************************************************************/

/***************************************Definition***********************************************/

@Deprecated("Replaced by new repositories")
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

    @Throws(Exception::class)
    suspend fun getEvent(eventId:String): Event?

    /**
     * Adds an event
     * @param event : the event to add
     * @return the event's id
     * @throws Exception if the request fails
     */

    @Throws(Exception::class)
    suspend fun addEvent(event:Event): String?

    /**
     * Updates an event
     * @param event : the event to update
     * @throws Exception if the request fails
     */

    @Throws(Exception::class)
    suspend fun updateEvent(event:Event)

    /**
     * Updates an event's attached trail
     * @param eventId : the event's id
     * @param trail : the trail to attach
     * @throws Exception if the request fails
     */

    @Throws(Exception::class)
    suspend fun updateEventAttachedTrail(eventId:String, trail:Trail)

    /**
     * Deletes an event's attached trail
     * @param eventId : the event's id
     * @param trailId : the trail's id
     * @throws Exception if the request fails
     */

    @Throws(Exception::class)
    suspend fun deleteEventAttachedTrail(eventId:String, trailId:String)

    /**
     * Updates an event's registered hiker
     * @param eventId : the event's id
     * @param hiker : the hiker to register
     * @throws Exception if the request fails
     */

    @Throws(Exception::class)
    suspend fun updateEventRegisteredHiker(eventId:String, hiker:Hiker)

    /**
     * Deletes an event's registered hiker
     * @param eventId : the event's id
     * @param hikerId : the hiker's id
     * @throws Exception if the request fails
     */

    @Throws(Exception::class)
    suspend fun deleteEventRegisteredHiker(eventId:String, hikerId:String)

    /**
     * Creates or updates a message in the event's chat
     * @param eventId : the event's id
     * @param message : the message
     * @throws Exception if the request fails
     */

    @Throws(Exception::class)
    suspend fun createOrUpdateEventMessage(eventId:String, message: Message)

    /**
     * Deletes a message from the event's chat
     * @param eventId : the event's id
     * @param messageId : the message's id
     * @throws Exception if the request fails
     */

    @Throws(Exception::class)
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

    override suspend fun getEvent(eventId:String): Event? =
        EventFirebaseQueries
            .getEvent(eventId)
            .get()
            .await()
            ?.toObject(Event::class.java)

    override suspend fun addEvent(event:Event): String? =
        EventFirebaseQueries
            .createEvent(event)
            .await()
            .id

    override suspend fun updateEvent(event:Event) {
        EventFirebaseQueries
            .updateEvent(event)
            .await()
    }

    override suspend fun updateEventAttachedTrail(eventId:String, trail:Trail) {
        EventFirebaseQueries
            .updateAttachedTrail(eventId, trail)
            .await()
    }

    override suspend fun deleteEventAttachedTrail(eventId:String, trailId:String) {
        EventFirebaseQueries
            .deleteAttachedTrail(eventId, trailId)
            .await()
    }

    override suspend fun updateEventRegisteredHiker(eventId:String, hiker:Hiker) {
        EventFirebaseQueries
            .updateRegisteredHiker(eventId, hiker)
            .await()
    }

    override suspend fun deleteEventRegisteredHiker(eventId:String, hikerId:String) {
        EventFirebaseQueries
            .deleteRegisteredHiker(eventId, hikerId)
            .await()
    }

    override suspend fun createOrUpdateEventMessage(eventId:String, message: Message) {
        EventFirebaseQueries
            .createOrUpdateMessage(eventId, message)
            .await()
    }

    override suspend fun deleteEventMessage(eventId:String, messageId: String) {
        EventFirebaseQueries
            .deleteMessage(eventId, messageId)
            .await()
    }
}