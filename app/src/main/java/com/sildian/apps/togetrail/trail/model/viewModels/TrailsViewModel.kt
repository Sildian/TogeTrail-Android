package com.sildian.apps.togetrail.trail.model.viewModels

import com.google.firebase.firestore.Query
import com.sildian.apps.togetrail.common.baseViewModels.ListDataViewModel
import com.sildian.apps.togetrail.trail.model.core.Trail
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/*************************************************************************************************
 * This viewModel observes a list of trails
 ************************************************************************************************/

@HiltViewModel
class TrailsViewModel @Inject constructor(): ListDataViewModel<Trail>(Trail::class.java) {

    fun loadTrailsRealTime(query: Query) {
        loadDataRealTime(query)
    }
}