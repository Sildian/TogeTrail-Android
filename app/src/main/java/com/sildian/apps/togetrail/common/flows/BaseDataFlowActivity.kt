package com.sildian.apps.togetrail.common.flows

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.sildian.apps.togetrail.event.model.core.Event
import com.sildian.apps.togetrail.event.model.support.EventFirebaseQueries
import com.sildian.apps.togetrail.hiker.model.core.Hiker
import com.sildian.apps.togetrail.hiker.model.core.HikerHistoryItem
import com.sildian.apps.togetrail.hiker.model.support.HikerFirebaseQueries
import com.sildian.apps.togetrail.trail.model.core.Trail
import com.sildian.apps.togetrail.trail.model.support.TrailFirebaseQueries

/*************************************************************************************************
 * Base activity for all activity aiming to load and save data
 ************************************************************************************************/

abstract class BaseDataFlowActivity : AppCompatActivity(), DataFlow {

    /**********************************Static items**********************************************/

    companion object {

        /**Logs**/
        private const val TAG = "BaseDataFlowActivity"
    }

    /**********************************Queries items*********************************************/

    protected var hikerQueryRegistration: ListenerRegistration? = null      //The listener registration for the hiker query
    protected var trailQueryRegistration: ListenerRegistration? = null      //The listener registration for the trail query
    protected var eventQueryRegistration: ListenerRegistration? = null      //The listener registration for the event query
    protected var trailsQueryRegistration: ListenerRegistration? = null     //The listener registration for the trails list query
    protected var eventsQueryRegistration: ListenerRegistration? = null     //The listener registration for the events list query

    /************************************Life cycle**********************************************/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutId())
        loadData()
        initializeUI()
    }

    override fun onDestroy() {
        this.hikerQueryRegistration?.remove()
        this.trailQueryRegistration?.remove()
        this.eventQueryRegistration?.remove()
        this.trailsQueryRegistration?.remove()
        this.eventsQueryRegistration?.remove()
        super.onDestroy()
    }

    /*********************************Data monitoring*******************************************/

    override fun loadData() {
        //Nothing here, override in children
    }

    override fun updateData(data: Any?) {
        //Nothing here, override in children
    }

    override fun saveData() {
        //Nothing here, override in children
    }

    /************************************UI monitoring*******************************************/

    abstract fun getLayoutId(): Int

    /**************************************Queries***********************************************/

    /**
     * Handles query errors
     * @param e : the exception
     */

    private fun handleQueryError(e: Exception) {
        Log.w(TAG, e.message.toString())
        //TODO handle
    }

    /*****************************Hiker queries**/

    protected fun getHikerRealTime(hikerId:String, callback: (Hiker?) -> Unit) {
        this.hikerQueryRegistration = HikerFirebaseQueries.getHiker(hikerId)
            .addSnapshotListener { snapshot, e ->
                if (snapshot != null) {
                    val hiker = snapshot.toObject(Hiker::class.java)
                    Log.d(TAG, "Hiker '${hiker?.id}' loaded from the database")
                    callback.invoke(hiker)
                }
                else if(e!=null){
                    handleQueryError(e)
                }
        }
    }

    protected fun getHiker(hikerId:String, callback:(Hiker?)->Unit){
        HikerFirebaseQueries.getHiker(hikerId).get()
            .addOnSuccessListener{ snapshot ->
                if(snapshot!=null){
                    val hiker=snapshot.toObject(Hiker::class.java)
                    Log.d(TAG, "Trail '${hiker?.id}' loaded from the database")
                    callback.invoke(hiker)
                }
            }
            .addOnFailureListener { e ->
                handleQueryError(e)
            }
    }

    protected fun updateHiker(hiker:Hiker, callback:(()->Unit)?=null){
        HikerFirebaseQueries.createOrUpdateHiker(hiker)
            .addOnSuccessListener {
                Log.d(TAG, "Hiker '${hiker.id}' updated successfully")
                callback?.invoke()
            }
            .addOnFailureListener { e ->
                handleQueryError(e)
            }
    }

    protected fun addHikerHistoryItem(hikerId:String, historyItem:HikerHistoryItem, callback:(()->Unit)?=null){
        HikerFirebaseQueries.addHistoryItem(hikerId, historyItem)
            .addOnSuccessListener {
                callback?.invoke()
            }
            .addOnFailureListener { e ->
                handleQueryError(e)
            }
    }

    protected fun updateHikerAttendedEvent(hikerId:String, event:Event, callback:(()->Unit)?=null){
        HikerFirebaseQueries.updateAttendedEvent(hikerId, event)
            .addOnSuccessListener {
                callback?.invoke()
            }
            .addOnFailureListener { e ->
                handleQueryError(e)
            }
    }

    protected fun deleteHikerAttendedEvent(hikerId: String, eventId: String, callback:(()->Unit)?=null){
        HikerFirebaseQueries.deleteAttendedEvent(hikerId, eventId)
            .addOnSuccessListener {
                callback?.invoke()
            }
            .addOnFailureListener { e ->
                handleQueryError(e)
            }
    }

    /*****************************Trail queries**/

    protected fun getTrailsRealTime(query: Query, callback:(List<Trail>)->Unit){
        this.trailsQueryRegistration= query
            .addSnapshotListener { querySnapshot, e ->
                if(querySnapshot!=null){
                    Log.d(TAG, "Query finished with ${querySnapshot.size()} trails")
                    val trails=querySnapshot.toObjects(Trail::class.java)
                    callback.invoke(trails)
                }
                else if(e!=null){
                    handleQueryError(e)
                }
            }
    }

    protected fun getTrailRealTime(trailId:String, callback:(Trail?)->Unit){
        this.trailQueryRegistration= TrailFirebaseQueries.getTrail(trailId)
            .addSnapshotListener { snapshot, e ->
                if(snapshot!=null){
                    val trail=snapshot.toObject(Trail::class.java)
                    Log.d(TAG, "Trail '${trail?.id}' loaded from the database")
                    callback.invoke(trail)
                }
                else if(e!=null){
                    handleQueryError(e)
                }
            }
    }

    protected fun getTrail(trailId:String, callback:(Trail?)->Unit){
        TrailFirebaseQueries.getTrail(trailId).get()
            .addOnSuccessListener{ snapshot ->
                if(snapshot!=null){
                    val trail=snapshot.toObject(Trail::class.java)
                    Log.d(TAG, "Trail '${trail?.id}' loaded from the database")
                    callback.invoke(trail)
                }
            }
            .addOnFailureListener { e ->
                handleQueryError(e)
            }
    }

    protected fun addTrail(trail:Trail, callback:((String)->Unit)?=null){
        TrailFirebaseQueries.createTrail(trail)
            .addOnSuccessListener { documentReference ->
                val trailId = documentReference.id
                Log.d(TAG, "Trail '$trailId' created in the database")
                callback?.invoke(trailId)
            }
            .addOnFailureListener { e ->
                handleQueryError(e)
            }
    }

    protected fun updateTrail(trail:Trail, callback:(()->Unit)?=null){
        TrailFirebaseQueries.updateTrail(trail)
            .addOnSuccessListener {
                Log.d(TAG, "Trail '${trail.id}' updated successfully")
                callback?.invoke()
            }
            .addOnFailureListener { e ->
                handleQueryError(e)
            }
    }

    /*****************************Event queries**/

    protected fun getEventsRealTime(query:Query, callback:(List<Event>)->Unit){
        this.eventsQueryRegistration=query
            .addSnapshotListener { querySnapshot, e ->
                if(querySnapshot!=null){
                    Log.d(TAG, "Query finished with ${querySnapshot.size()} events")
                    val events=querySnapshot.toObjects(Event::class.java)
                    callback.invoke(events)
                }
                else if(e!=null){
                    handleQueryError(e)
                }
            }
    }

    protected fun getEventRealTime(eventId:String, callback:(Event?)->Unit){
        this.eventQueryRegistration= EventFirebaseQueries.getEvent(eventId)
            .addSnapshotListener { snapshot, e ->
                if(snapshot!=null){
                    val event=snapshot.toObject(Event::class.java)
                    Log.d(TAG, "Event '${event?.id}' loaded from the database")
                    callback.invoke(event)
                }
                else if(e!=null){
                    handleQueryError(e)
                }
            }
    }

    protected fun getEvent(eventId:String, callback:(Event?)->Unit){
        EventFirebaseQueries.getEvent(eventId).get()
            .addOnSuccessListener{ snapshot ->
                if(snapshot!=null){
                    val event=snapshot.toObject(Event::class.java)
                    Log.d(TAG, "Event '${event?.id}' loaded from the database")
                    callback.invoke(event)
                }
            }
            .addOnFailureListener { e ->
                handleQueryError(e)
            }
    }

    protected fun addEvent(event:Event, callback:((String)->Unit)?=null){
        EventFirebaseQueries.createEvent(event)
            .addOnSuccessListener { documentReference ->
                val eventId = documentReference.id
                Log.d(TAG, "Event '$eventId' created in the database")
                callback?.invoke(eventId)
            }
            .addOnFailureListener { e ->
                handleQueryError(e)
            }
    }

    protected fun updateEvent(event:Event, callback:(()->Unit)?=null){
        EventFirebaseQueries.updateEvent(event)
            .addOnSuccessListener {
                Log.d(TAG, "Event '${event.id}' updated successfully")
                callback?.invoke()
            }
            .addOnFailureListener { e ->
                handleQueryError(e)
            }
    }

    protected fun updateEventAttachedTrail(eventId:String, trail: Trail, callback:(()->Unit)?=null){
        EventFirebaseQueries.updateAttachedTrail(eventId, trail)
            .addOnSuccessListener {
                callback?.invoke()
            }
            .addOnFailureListener { e ->
                handleQueryError(e)
            }
    }

    protected fun deleteEventAttachedTrail(eventId:String, trailId:String, callback:(()->Unit)?=null){
        EventFirebaseQueries.deleteAttachedTrail(eventId, trailId)
            .addOnSuccessListener {
                callback?.invoke()
            }
            .addOnFailureListener { e ->
                handleQueryError(e)
            }
    }

    protected fun updateEventRegisteredHiker(eventId:String, hiker:Hiker, callback:(()->Unit)?=null){
        EventFirebaseQueries.updateRegisteredHiker(eventId.toString(), hiker)
            .addOnSuccessListener {
                callback?.invoke()
            }
            .addOnFailureListener { e ->
                handleQueryError(e)
            }
    }

    protected fun deleteEventRegisteredHiker(eventId: String, hikerId: String, callback:(()->Unit)?=null){
        EventFirebaseQueries.deleteRegisteredHiker(eventId, hikerId)
            .addOnSuccessListener {
                callback?.invoke()
            }
            .addOnFailureListener { e ->
                handleQueryError(e)
            }
    }
}