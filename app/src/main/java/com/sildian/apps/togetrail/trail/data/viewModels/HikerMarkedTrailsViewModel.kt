package com.sildian.apps.togetrail.trail.data.viewModels

import com.sildian.apps.togetrail.common.baseViewModels.ListDataViewModel
import com.sildian.apps.togetrail.common.utils.cloudHelpers.FirebaseQueryDataFlowRequest
import com.sildian.apps.togetrail.common.coroutines.CoroutineIODispatcher
import com.sildian.apps.togetrail.hiker.data.source.HikerFirebaseQueries
import com.sildian.apps.togetrail.trail.data.models.Trail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

/*************************************************************************************************
 * This viewModel observes the list of marked trails related to a hiker
 ************************************************************************************************/

@HiltViewModel
class HikerMarkedTrailsViewModel @Inject constructor(@CoroutineIODispatcher dispatcher: CoroutineDispatcher): ListDataViewModel<Trail>(Trail::class.java, dispatcher) {

    fun loadMarkedTrailsFlow(hikerId: String) {
        loadDataFlow(FirebaseQueryDataFlowRequest(this.dispatcher, this.dataModelClass, HikerFirebaseQueries.getMarkedTrails(hikerId)))
    }
}