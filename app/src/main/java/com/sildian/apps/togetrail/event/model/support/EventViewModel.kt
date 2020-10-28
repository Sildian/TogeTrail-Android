package com.sildian.apps.togetrail.event.model.support

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sildian.apps.togetrail.common.baseViewModels.BaseViewModel
import com.sildian.apps.togetrail.common.utils.cloudHelpers.AuthRepository
import com.sildian.apps.togetrail.event.model.core.Event
import com.sildian.apps.togetrail.trail.model.core.Trail
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

/*************************************************************************************************
 * This viewModel observes a single Event related data
 ************************************************************************************************/

class EventViewModel : BaseViewModel() {

    /************************************Static items********************************************/

    companion object{

        /**Logs**/
        private const val TAG = "EventViewModel"
    }

    /***********************************Exception handler***************************************/

    private val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        Log.e(TAG, throwable.message.toString())
    }

    /***********************************Data requester******************************************/

    private val eventDataRequester = EventDataRequester()

    /*********************************Monitoring info*******************************************/

    val attachedTrails= arrayListOf<Trail>()        //The list of attached trails (useful only when the event has no id yet)

    /********************************LiveData to be observed*************************************/

    val event = MutableLiveData<Event?>()
    val saveRequestSuccess = MutableLiveData<Boolean>()
    val requestFailure = MutableLiveData<Exception?>()

    /************************************Data monitoring*****************************************/

    /**Initializes a new event**/

    fun initNewEvent(){
        this.event.postValue(Event())
    }

    /**
     * Checks that the current user is the event's author
     * @return a boolean
     */

    fun currentUserIsAuthor(): Boolean =
        AuthRepository().getCurrentUser()?.uid == this.event.value?.authorId

    /**
     * Loads an event from the database in real time
     * @param eventId : the event's id
     */

    fun loadEventFromDatabaseRealTime(eventId:String) {
        this.queryRegistration?.remove()
        this.queryRegistration = eventDataRequester.loadEventFromDatabaseRealTime(eventId)
            .addSnapshotListener { snapshot, e ->
                if (snapshot != null) {
                    val result = snapshot.toObject(Event::class.java)
                    Log.d(TAG, "Successfully loaded event from database")
                    event.postValue(result)
                }
                else if (e != null) {
                    Log.e(TAG, "Failed to load event from database : ${e.message}")
                    requestFailure.postValue(e)
                }
            }
    }

    /**
     * Loads an event from the database
     * @param eventId : the event's id
     */

    fun loadEventFromDatabase(eventId:String) {
        viewModelScope.launch(this.exceptionHandler) {
            try {
                val result = async { eventDataRequester.loadEventFromDatabase(eventId) }.await()
                Log.d(TAG, "Successfully loaded event from database")
                event.postValue(result)
            }
            catch (e: Exception) {
                Log.e(TAG, "Failed to load event from database : ${e.message}")
                requestFailure.postValue(e)
            }
        }
    }

    /**
     * Saves the event within the database
     */

    fun saveEventInDatabase() {
        viewModelScope.launch(this.exceptionHandler) {
            try {
                launch { eventDataRequester.saveEventInDatabase(event.value, attachedTrails) }.join()
                Log.d(TAG, "Successfully saved event in database")
                saveRequestSuccess.postValue(true)
            }
            catch (e: Exception) {
                Log.e(TAG, "Failed to save event in database : ${e.message}")
                requestFailure.postValue(e)
            }
        }
    }

    /**
     * Attaches a trail to the event
     * @param trail : the trail to attach
     */

    fun attachTrail(trail: Trail) {
        viewModelScope.launch(this.exceptionHandler) {
            try {
                launch { eventDataRequester.attachTrail(event.value, trail) }.join()
                Log.d(TAG, "Successfully attached new trail to the event")
            }
            catch (e:Exception) {
                Log.e(TAG, "Failed to attach new trail to the event : ${e.message}")
                requestFailure.postValue(e)
            }
        }
    }

    /**
     * Detaches a trail from the event
     * @param trail : the trail to detach
     */

    fun detachTrail(trail: Trail) {
        viewModelScope.launch(this.exceptionHandler) {
            try {
                launch { eventDataRequester.detachTrail(event.value, trail) }.join()
                Log.d(TAG, "Successfully detached trail from the event")
            }
            catch (e:Exception) {
                Log.e(TAG, "Failed to detach trail from the event : ${e.message}")
                requestFailure.postValue(e)
            }
        }
    }

    /**
     * Registers the current user to the event
     */

    fun registerUserToEvent() {
        viewModelScope.launch(this.exceptionHandler) {
            try {
                launch { eventDataRequester.registerUserToEvent(event.value) }.join()
                Log.d(TAG, "Successfully registered new user to the event")
            }
            catch (e:Exception) {
                Log.e(TAG, "Failed to register new user to the event : ${e.message}")
                requestFailure.postValue(e)
            }
        }
    }

    /**
     * Unregisters the current user from the event
     */

    fun unregisterUserFromEvent() {
        viewModelScope.launch(this.exceptionHandler) {
            try {
                launch { eventDataRequester.unregisterUserFromEvent(event.value) }.join()
                Log.d(TAG, "Successfully unregistered user from the event")
            }
            catch (e:Exception) {
                Log.e(TAG, "Failed to unregister user from the event : ${e.message}")
                requestFailure.postValue(e)
            }
        }
    }
}