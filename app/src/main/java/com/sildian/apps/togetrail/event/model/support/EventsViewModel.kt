package com.sildian.apps.togetrail.event.model.support

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.Query
import com.sildian.apps.togetrail.common.baseViewModels.BaseViewModel
import com.sildian.apps.togetrail.event.model.core.Event

/*************************************************************************************************
 * This viewModel observes a list of events
 ************************************************************************************************/

class EventsViewModel: BaseViewModel() {

    /************************************Static items********************************************/

    companion object {

        /**Logs**/
        private const val TAG = "EventsViewModel"
    }
    
    /***************************************Data*************************************************/

    val events = MutableLiveData<List<Event>>()
    val requestFailure = MutableLiveData<Exception?>()

    /************************************Data monitoring*****************************************/

    /**
     * Loads a list of events from the database in real time
     * @param query : the query to fetch events (select a query in EventFirebaseQuery)
     */

    fun loadEventsFromDatabaseRealTime(query: Query) {
        this.queryRegistration?.remove()
        this.queryRegistration = query
            .addSnapshotListener { querySnapshot, e ->
                if (querySnapshot != null) {
                    val results = querySnapshot.toObjects(Event::class.java)
                    Log.d(TAG, "Successfully loaded ${results.size} events from database")
                    events.postValue(results)
                }
                else if (e != null) {
                    Log.e(TAG, "Failed to load events from database : ${e.message}")
                    requestFailure.postValue(e)
                }
            }
    }
}