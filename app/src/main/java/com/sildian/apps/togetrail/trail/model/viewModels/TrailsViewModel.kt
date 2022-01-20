package com.sildian.apps.togetrail.trail.model.viewModels

import com.google.firebase.firestore.Query
import com.sildian.apps.togetrail.common.baseViewModels.ListDataViewModel
import com.sildian.apps.togetrail.trail.model.core.Trail

/*************************************************************************************************
 * This viewModel observes a list of trails
 ************************************************************************************************/

class TrailsViewModel: ListDataViewModel<Trail>(Trail::class.java) {

    fun loadTrailsRealTime(query: Query) {
        loadDataRealTime(query)
    }
}