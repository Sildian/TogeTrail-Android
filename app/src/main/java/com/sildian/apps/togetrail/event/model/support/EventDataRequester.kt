package com.sildian.apps.togetrail.event.model.support

import com.google.firebase.firestore.DocumentReference
import com.sildian.apps.togetrail.event.model.core.Event
import com.sildian.apps.togetrail.hiker.model.core.HikerHistoryItem
import com.sildian.apps.togetrail.hiker.model.core.HikerHistoryType
import com.sildian.apps.togetrail.hiker.model.support.CurrentHikerInfo
import com.sildian.apps.togetrail.hiker.model.support.HikerRepository
import com.sildian.apps.togetrail.chat.model.core.Message
import com.sildian.apps.togetrail.trail.model.core.Trail
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

/*************************************************************************************************
 * Run jobs to request data related to Event
 ************************************************************************************************/

class EventDataRequester {

    /************************************Static items********************************************/

    companion object {

        /**Exceptions messages**/
        private const val EXCEPTION_MESSAGE_NULL_HIKER = "Cannot perform the requested operation with a null hiker"
        private const val EXCEPTION_MESSAGE_NULL_EVENT = "Cannot perform the requested operation with a null event"
        private const val EXCEPTION_MESSAGE_NO_ID_TRAIL = "Cannot perform the requested operation with a trail without id"
        private const val EXCEPTION_MESSAGE_NO_ID_EVENT = "Cannot perform the requested operation with an event without id"
        private const val EXCEPTION_MESSAGE_NO_TEXT_MESSAGE = "Cannot perform the requested operation with a message without text"
    }

    /************************************Repositories********************************************/

    private val hikerRepository = HikerRepository()
    private val eventRepository = EventRepository()

    /***************************************Requests********************************************/

    /**
     * Loads an event from the database in real time
     * @param eventId : the event's id
     */

    fun loadEventFromDatabaseRealTime(eventId:String): DocumentReference =
        eventRepository.getEventReference(eventId)

    /**
     * Loads an event from the database
     * @param eventId : the event's id
     * @return the resulted event
     * @throws Exception if the request fails
     */

    @Throws(Exception::class)
    suspend fun loadEventFromDatabase(eventId:String): Event? =
        withContext(Dispatchers.IO) {
            try {
                async { eventRepository.getEvent(eventId) }.await()
            }
            catch (e: Exception) {
                throw e
            }
        }

    /**
     * Saves the event within the database
     * @param event : the event to be saved
     * @param attachedTrails : the list of trails to be attached to the event
     * @throws NullPointerException if the event or the current hiker is null
     * @throws Exception if the request fails
     */

    @Throws(Exception::class)
    suspend fun saveEventInDatabase(event: Event?, attachedTrails: List<Trail>?) {
        withContext(Dispatchers.IO) {
            try {

                val hiker = CurrentHikerInfo.currentHiker
                if (hiker != null) {

                    if (event != null) {

                        /*If the event is new...*/

                        if (event.id == null) {

                            /*Creates the event and its attached trails*/

                            event.authorId = hiker.id
                            event.id = async { eventRepository.addEvent(event) }.await()
                            launch { eventRepository.updateEvent(event) }.join()

                            attachedTrails?.forEach { trail ->
                                launch {
                                    eventRepository.updateEventAttachedTrail(event.id!!, trail)
                                }.join()
                            }

                            /*Updates the author's profile*/

                                hiker.nbEventsCreated++
                                launch { hikerRepository.updateHiker(hiker) }.join()

                                /*And creates an history item*/

                                val historyItem = HikerHistoryItem(
                                    HikerHistoryType.EVENT_CREATED,
                                    event.creationDate,
                                    event.id!!,
                                    event.name!!,
                                    event.meetingPoint.toString(),
                                    event.mainPhotoUrl
                                )
                                launch {
                                    hikerRepository.addHikerHistoryItem(
                                        hiker.id,
                                        historyItem
                                    )
                                }.join()

                        }
                        else {

                            /*If the event is not new, just updates it*/

                            launch { eventRepository.updateEvent(event) }.join()
                        }
                    }
                    else {
                        throw NullPointerException(EXCEPTION_MESSAGE_NULL_EVENT)
                    }
                }
                else {
                    throw NullPointerException(EXCEPTION_MESSAGE_NULL_HIKER)
                }
            }
            catch (e: Exception) {
                throw e
            }
        }
    }

    /**
     * Attaches a trail to the event
     * @param event : the event
     * @param trail : the trail to attach
     * @throws IllegalArgumentException if the event or the trail has no id
     * @throws NullPointerException if the event is null
     * @throws Exception if the request fails
     */

    @Throws(Exception::class)
    suspend fun attachTrail(event: Event?, trail: Trail) {
        withContext(Dispatchers.IO) {
            try {
                if(event != null) {
                    if (event.id != null) {
                        if (trail.id != null) {
                            launch {
                                eventRepository.updateEventAttachedTrail(
                                    event.id!!,
                                    trail
                                )
                            }.join()
                        }
                        else {
                            throw IllegalArgumentException(EXCEPTION_MESSAGE_NO_ID_TRAIL)
                        }
                    }
                    else {
                        throw IllegalArgumentException(EXCEPTION_MESSAGE_NO_ID_EVENT)
                    }
                }
                else{
                    throw NullPointerException(EXCEPTION_MESSAGE_NULL_EVENT)
                }
            }
            catch (e: Exception) {
                throw e
            }
        }
    }

    /**
     * Detaches a trail from the event
     * @param event : the event
     * @param trail : the trail to detach
     * @throws IllegalArgumentException if the event or the trail has no id
     * @throws NullPointerException if the event is null
     * @throws Exception if the request fails
     */

    @Throws(Exception::class)
    suspend fun detachTrail(event: Event?, trail: Trail) {
        withContext(Dispatchers.IO) {
            try {
                if (event != null) {
                    if (event.id != null) {
                        if (trail.id != null) {
                            launch {
                                eventRepository.deleteEventAttachedTrail(
                                    event.id!!,
                                    trail.id!!
                                )
                            }.join()
                        }
                        else {
                            throw IllegalArgumentException(EXCEPTION_MESSAGE_NO_ID_TRAIL)
                        }
                    }
                    else {
                        throw IllegalArgumentException(EXCEPTION_MESSAGE_NO_ID_EVENT)
                    }
                }
                else {
                    throw NullPointerException(EXCEPTION_MESSAGE_NULL_EVENT)
                }
            }
            catch (e: Exception) {
                throw e
            }
        }
    }

    /**
     * Registers the current user to the event
     * @param event : the event
     * @throws IllegalArgumentException if the event has no id
     * @throws NullPointerException if the event or the current hiker is null
     * @throws Exception if the request fails
     */

    @Throws(Exception::class)
    suspend fun registerUserToEvent(event: Event?) {
        withContext(Dispatchers.IO) {
            try {

                val hiker = CurrentHikerInfo.currentHiker
                if (hiker != null) {
                    if (event != null) {
                        if (event.id != null) {

                            /*Registers the hiker to the event*/

                            hiker.nbEventsAttended++
                            event.nbHikersRegistered++
                            launch { hikerRepository.updateHiker(hiker) }
                            launch { eventRepository.updateEvent(event) }
                            launch { hikerRepository.updateHikerAttendedEvent(hiker.id, event) }
                            launch { eventRepository.updateEventRegisteredHiker(event.id!!, hiker) }

                            /*Then creates an hiker history item*/

                            val historyItem = HikerHistoryItem(
                                HikerHistoryType.EVENT_ATTENDED,
                                Date(),
                                event.id,
                                event.name,
                                event.meetingPoint.toString(),
                                event.mainPhotoUrl
                            )

                            launch { hikerRepository.addHikerHistoryItem(hiker.id, historyItem) }
                        }
                        else {
                            throw IllegalArgumentException(EXCEPTION_MESSAGE_NO_ID_EVENT)
                        }
                    }
                    else {
                        throw NullPointerException(EXCEPTION_MESSAGE_NULL_EVENT)
                    }
                }
                else {
                    throw NullPointerException(EXCEPTION_MESSAGE_NULL_HIKER)
                }
            }
            catch (e: Exception) {
                throw e
            }
        }
    }

    /**
     * Unregisters the current user from the event
     * @param event : the event
     * @throws IllegalArgumentException if the event has no id
     * @throws NullPointerException if the event or the current hiker is null
     * @throws Exception if the request fails
     */

    @Throws(Exception::class)
    suspend fun unregisterUserFromEvent(event: Event?) {
        withContext(Dispatchers.IO) {
            try {

                val hiker = CurrentHikerInfo.currentHiker
                if (hiker != null) {
                    if (event != null) {
                        if (event.id != null) {

                            /*Unregisters the hiker from the event*/

                            hiker.nbEventsAttended--
                            event.nbHikersRegistered--
                            launch { hikerRepository.updateHiker(hiker) }
                            launch { eventRepository.updateEvent(event) }
                            launch {
                                hikerRepository.deleteHikerAttendedEvent(
                                    hiker.id,
                                    event.id!!
                                )
                            }
                            launch {
                                eventRepository.deleteEventRegisteredHiker(
                                    event.id!!,
                                    hiker.id
                                )
                            }

                            /*Also deletes the related history item*/

                            launch {
                                hikerRepository.deleteHikerHistoryItems(
                                    hiker.id,
                                    HikerHistoryType.EVENT_ATTENDED,
                                    event.id!!
                                )
                            }
                        }
                        else {
                            throw IllegalArgumentException(EXCEPTION_MESSAGE_NO_ID_EVENT)
                        }
                    }
                    else {
                        throw NullPointerException(EXCEPTION_MESSAGE_NULL_EVENT)
                    }
                } else {
                    throw NullPointerException(EXCEPTION_MESSAGE_NULL_HIKER)
                }
            }
            catch (e: Exception) {
                throw e
            }
        }
    }

    /**
     * Sends a message to the event's chat
     * @param event : the event
     * @param text : the text
     * @throws IllegalArgumentException if the event has no id or if the text is empty
     * @throws NullPointerException if the event or the current hiker is null
     */

    @Throws(Exception::class)
    suspend fun sendMessage(event: Event?, text: String) {
        withContext(Dispatchers.IO) {
            try {
                val hiker = CurrentHikerInfo.currentHiker
                if (hiker != null) {
                    if (event != null) {
                        if (event.id != null) {
                            if (text.isNotEmpty()) {
                                val message = Message(
                                    text = text,
                                    authorId = hiker.id,
                                    authorName = hiker.name,
                                    authorPhotoUrl = hiker.photoUrl
                                )
                                launch { eventRepository.addEventMessage(event.id!!, message) }
                            }
                            else {
                                throw IllegalArgumentException(EXCEPTION_MESSAGE_NO_TEXT_MESSAGE)
                            }
                        }
                        else {
                            throw IllegalArgumentException(EXCEPTION_MESSAGE_NO_ID_EVENT)
                        }
                    }
                    else {
                        throw NullPointerException(EXCEPTION_MESSAGE_NULL_EVENT)
                    }
                }
                else {
                    throw NullPointerException(EXCEPTION_MESSAGE_NULL_HIKER)
                }
            }
            catch (e: Exception) {
                throw e
            }
        }
    }
}