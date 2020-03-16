package com.sildian.apps.togetrail.event.model.support

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.sildian.apps.togetrail.event.model.core.Event
import com.sildian.apps.togetrail.hiker.model.core.Hiker
import com.sildian.apps.togetrail.trail.model.core.Trail

/*************************************************************************************************
 * Provides with Firebase queries on Event
 ************************************************************************************************/

object EventFirebaseQueries {

    /****************************************Callbacks*******************************************/

    interface OnEventsQueryResultListener{

        /**
         * This event is triggered when a list of events is successfully returned from the query
         * @param events : the resulted list of events
         */

        fun onEventsQueryResult(events:List<Event>)
    }

    /*********************************Collection references**************************************/

    private const val COLLECTION_NAME="event"
    private const val SUB_COLLECTION_ATTACHED_TRAIL_NAME="attachedTrail"
    private const val SUB_COLLECTION_REGISTERED_HIKER_NAME="registeredHiker"
    private fun getCollection() =
        FirebaseFirestore.getInstance().collection(COLLECTION_NAME)
    private fun getAttachedTrailSubCollection(eventId:String) =
        getCollection().document(eventId).collection(SUB_COLLECTION_ATTACHED_TRAIL_NAME)
    private fun getRegisteredHikerSubCollection(eventId:String) =
        getCollection().document(eventId).collection(SUB_COLLECTION_REGISTERED_HIKER_NAME)

    /*************************************Queries************************************************/

    /**
     * Gets the events from the database
     * @return a task result
     */

    fun getEvents(): Query =
        getCollection().orderBy("beginDate", Query.Direction.ASCENDING)

    /**
     * Gets a given event
     * @param id : the id of the event
     * @return a task result
     */

    fun getEvent(id:String): Task<DocumentSnapshot> =
        getCollection().document(id).get()

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
     * @return a task result
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
     * @return a task result
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
}