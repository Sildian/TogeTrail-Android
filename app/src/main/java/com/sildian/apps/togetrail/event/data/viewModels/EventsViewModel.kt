package com.sildian.apps.togetrail.event.data.viewModels

import com.google.firebase.firestore.Query
import com.sildian.apps.togetrail.common.baseViewModels.ListDataViewModel
import com.sildian.apps.togetrail.event.data.core.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/*************************************************************************************************
 * This viewModel observes a list of events
 ************************************************************************************************/

@HiltViewModel
class EventsViewModel @Inject constructor(): ListDataViewModel<Event>(Event::class.java) {

    fun loadEventsRealTime(query: Query) {
        loadDataRealTime(query)
    }
}