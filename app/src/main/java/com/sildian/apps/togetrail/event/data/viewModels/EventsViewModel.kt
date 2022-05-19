package com.sildian.apps.togetrail.event.data.viewModels

import com.google.firebase.firestore.Query
import com.sildian.apps.togetrail.common.baseDataRequests.FirebaseQueryDataFlowRequest
import com.sildian.apps.togetrail.common.baseViewModels.ListDataViewModel
import com.sildian.apps.togetrail.common.utils.coroutinesHelpers.CoroutineIODispatcher
import com.sildian.apps.togetrail.event.data.models.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

/*************************************************************************************************
 * This viewModel observes a list of events
 ************************************************************************************************/

@HiltViewModel
class EventsViewModel @Inject constructor(@CoroutineIODispatcher dispatcher: CoroutineDispatcher): ListDataViewModel<Event>(Event::class.java, dispatcher) {

    fun loadEventsFlow(query: Query) {
        loadDataFlow(FirebaseQueryDataFlowRequest(this.dispatcher, this.dataModelClass, query))
    }
}