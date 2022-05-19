package com.sildian.apps.togetrail.trail.data.viewModels

import com.google.firebase.firestore.Query
import com.sildian.apps.togetrail.common.baseDataRequests.FirebaseQueryDataFlowRequest
import com.sildian.apps.togetrail.common.baseViewModels.ListDataViewModel
import com.sildian.apps.togetrail.common.utils.coroutinesHelpers.CoroutineIODispatcher
import com.sildian.apps.togetrail.trail.data.models.Trail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

/*************************************************************************************************
 * This viewModel observes a list of trails
 ************************************************************************************************/

@HiltViewModel
class TrailsViewModel @Inject constructor(@CoroutineIODispatcher dispatcher: CoroutineDispatcher): ListDataViewModel<Trail>(Trail::class.java, dispatcher) {

    fun loadTrailsFlow(query: Query) {
        loadDataFlow(FirebaseQueryDataFlowRequest(this.dispatcher, this.dataModelClass, query))
    }
}