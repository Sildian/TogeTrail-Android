package com.sildian.apps.togetrail.event.model.support

import com.google.firebase.firestore.Query
import com.sildian.apps.togetrail.common.baseViewModels.ListDataViewModel
import com.sildian.apps.togetrail.event.model.core.Event

/*************************************************************************************************
 * This viewModel observes a list of events
 ************************************************************************************************/

class EventsViewModel: ListDataViewModel<Event>(Event::class.java) {

    fun loadEventsRealTime(query: Query) {
        loadDataRealTime(query)
    }
}