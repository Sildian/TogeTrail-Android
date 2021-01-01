package com.sildian.apps.togetrail.event.model.support

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import com.sildian.apps.togetrail.location.model.core.Location
import com.sildian.apps.togetrail.common.utils.GeoUtilities
import com.sildian.apps.togetrail.event.model.core.Event
import com.sildian.apps.togetrail.hiker.model.core.Hiker
import com.sildian.apps.togetrail.message.model.core.Message
import com.sildian.apps.togetrail.trail.model.core.Trail
import java.util.*

/*************************************************************************************************
 * Provides with Firebase queries on Event
 ************************************************************************************************/

object EventFirebaseQueries {

    /*********************************Collection references**************************************/

    private const val COLLECTION_NAME="event"
    private const val SUB_COLLECTION_ATTACHED_TRAIL_NAME="attachedTrail"
    private const val SUB_COLLECTION_REGISTERED_HIKER_NAME="registeredHiker"
    private const val SUB_COLLECTION_MESSAGE="message"
    private fun getCollection() =
        FirebaseFirestore.getInstance().collection(COLLECTION_NAME)
    private fun getAttachedTrailSubCollection(eventId:String) =
        getCollection().document(eventId).collection(SUB_COLLECTION_ATTACHED_TRAIL_NAME)
    private fun getRegisteredHikerSubCollection(eventId:String) =
        getCollection().document(eventId).collection(SUB_COLLECTION_REGISTERED_HIKER_NAME)
    private fun getMessageSubCollection(eventId: String) =
        getCollection().document(eventId).collection(SUB_COLLECTION_MESSAGE)

    /*************************************Queries************************************************/

    /**
     * Gets the future events from the database
     * @return a query
     */

    fun getNextEvents(): Query =
        getCollection()
            .whereGreaterThan("beginDate", Date())
            .orderBy("beginDate", Query.Direction.ASCENDING)

    /**
     * Gets the future events created by a specific user
     * @param authorId : the id of the author
     * @return a query
     */

    fun getMyEvents(authorId:String): Query =
        getCollection()
            .whereEqualTo("authorId", authorId)
            .whereGreaterThan("beginDate", Date())
            .orderBy("beginDate", Query.Direction.ASCENDING)

    /**
     * Gets the future events nearby a location
     * If the region is not null, gets the events within the region
     * Else if the country is not null, gets the events within the country
     * @param location : the location
     * @return a query or null if the location is empty
     */

    fun getEventsNearbyLocation(location: Location):Query? =
        when {
            location.region!=null -> {
                getCollection()
                    .whereEqualTo(FieldPath.of("meetingPoint", "country", "code"), location.country?.code)
                    .whereEqualTo(FieldPath.of("meetingPoint", "region", "code"), location.region.code)
                    .whereGreaterThan("beginDate", Date())
                    .orderBy("beginDate", Query.Direction.ASCENDING)
            }
            location.country!=null -> {
                getCollection()
                    .whereEqualTo(FieldPath.of("meetingPoint", "country", "code"), location.country.code)
                    .whereGreaterThan("beginDate", Date())
                    .orderBy("beginDate", Query.Direction.ASCENDING)
            }
            else -> null
        }

    /**
     * Gets the future events around a point, using bounds to calculate the area limitations
     * @param point : the origin point
     * @return a query
     */

    fun getEventsAroundPoint(point: LatLng):Query{
        val bounds= GeoUtilities.getBoundsAroundOriginPoint(point)
        val minGeoPoint=GeoPoint(bounds.southwest.latitude, bounds.southwest.longitude)
        val maxGeoPoint=GeoPoint(bounds.northeast.latitude, bounds.northeast.longitude)
        return getCollection()
            .whereGreaterThanOrEqualTo("position", minGeoPoint)
            .whereLessThanOrEqualTo("position", maxGeoPoint)
            .orderBy("position")
            .orderBy("beginDate", Query.Direction.ASCENDING)
    }

    /**
     * Gets a given event
     * @param id : the id of the event
     * @return a document reference
     */

    fun getEvent(id:String): DocumentReference =
        getCollection().document(id)

    /**
     * Creates a new event in the database
     * @param event : the event
     * @return a task result
     */

    fun createEvent(event:Event):Task<DocumentReference> =
        getCollection().add(event)

    /**
     * Updates an existing event in the database
     * @param event : the event
     * @return a task result
     */

    fun updateEvent(event:Event):Task<Void> =
        getCollection().document(event.id!!).set(event)

    /**
     * Gets all trails attached to an event
     * @param eventId : the id of the event
     * @return a query
     */

    fun getAttachedTrails(eventId: String):Query =
        getAttachedTrailSubCollection(eventId)

    /**
     * Updates a trail attached to an event
     * @param eventId : the id of the event
     * @param trail : the trail to update
     * @return a task result
     */

    fun updateAttachedTrail(eventId:String, trail: Trail):Task<Void>{

        /*Simplifies the trail stored into an event : clears all the points and poi*/

        trail.trailTrack.trailPoints.clear()
        trail.trailTrack.trailPointsOfInterest.clear()

        /*Then updates the trail*/

        return getAttachedTrailSubCollection(eventId).document(trail.id.toString()).set(trail)
    }

    /**
     * Deletes a trail attached to an event
     * @param eventId : the id of the event
     * @param trailId : the id of the trail
     * @return a task result
     */

    fun deleteAttachedTrail(eventId:String, trailId:String) : Task<Void> =
        getAttachedTrailSubCollection(eventId).document(trailId).delete()

    /**
     * Gets all hikers registered to an event
     * @param eventId : the id of the event
     * @return a query
     */

    fun getRegisteredHikers(eventId:String):Query =
        getRegisteredHikerSubCollection(eventId)

    /**
     * Updates a hiker registered to an event
     * @param eventId : the id of the event
     * @param hiker : the hiker to update
     * @return a task result
     */

    fun updateRegisteredHiker(eventId: String, hiker: Hiker):Task<Void> =
        getRegisteredHikerSubCollection(eventId).document(hiker.id).set(hiker)

    /**
     * Deletes a hiker registered to an event
     * @param eventId : the id of the event
     * @param hikerId : the id of the hiker
     * @return a task result
     */

    fun deleteRegisteredHiker(eventId:String, hikerId:String):Task<Void> =
        getRegisteredHikerSubCollection(eventId).document(hikerId).delete()

    /**
     * Get all messages related to an event
     * @param eventId : the id of the event
     * @return a query
     */

    fun getMessages(eventId: String): Query =
        getMessageSubCollection(eventId)

    /**
     * Add a message to the event's chat
     * @param eventId : the id of the event
     * @param message : the message
     * @return a task result
     */

    fun addMessage(eventId: String, message: Message): Task<DocumentReference> =
        getMessageSubCollection(eventId).add(message)
}