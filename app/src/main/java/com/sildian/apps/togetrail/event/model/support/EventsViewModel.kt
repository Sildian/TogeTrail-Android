package com.sildian.apps.togetrail.event.model.support

import com.google.firebase.firestore.Query
import com.sildian.apps.togetrail.common.baseViewModels.BaseObservableViewModel
import com.sildian.apps.togetrail.event.model.core.Event

/*************************************************************************************************
 * This viewModel observes a list of events
 ************************************************************************************************/

class EventsViewModel:BaseObservableViewModel() {

    /***************************************Data*************************************************/

    val events= arrayListOf<Event>()                    //The list of events

    /************************************Data monitoring*****************************************/

    /**
     * Loads a list of events from the database in real time
     * @param query : the query to fetch events (select a query in EventFirebaseQuery)
     * @param successCallback : the callback to handle a success in the query
     * @param failureCallback : the callback to handle a failure in the query
     */

    fun loadEventsFromDatabaseRealTime(query: Query, successCallback:(()->Unit)?=null, failureCallback:((Exception)->Unit)?=null){
        this.queryRegistration = query
            .addSnapshotListener { querySnapshot, e ->
                if (querySnapshot != null) {
                    events.clear()
                    events.addAll(querySnapshot.toObjects(Event::class.java))
                    notifyDataChanged()
                    successCallback?.invoke()
                }
                else if(e!=null){
                    failureCallback?.invoke(e)
                }
            }
    }
}