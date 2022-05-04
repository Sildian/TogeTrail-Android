package com.sildian.apps.togetrail.trail.data.viewModels

import com.sildian.apps.togetrail.common.baseViewModels.ListDataViewModel
import com.sildian.apps.togetrail.hiker.data.source.HikerFirebaseQueries
import com.sildian.apps.togetrail.trail.data.core.Trail
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/*************************************************************************************************
 * This viewModel observes the list of liked trails related to a hiker
 ************************************************************************************************/

@HiltViewModel
class HikerLikedTrailsViewModel @Inject constructor(): ListDataViewModel<Trail>(Trail::class.java) {

    fun loadLikedTrailsRealTime(hikerId: String) {
        loadDataRealTime(HikerFirebaseQueries.getLikedTrails(hikerId))
    }
}