package com.sildian.apps.togetrail.trail.model.support

import com.sildian.apps.togetrail.common.baseViewModels.ListDataViewModel
import com.sildian.apps.togetrail.hiker.model.support.HikerFirebaseQueries
import com.sildian.apps.togetrail.trail.model.core.Trail

/*************************************************************************************************
 * This viewModel observes the list of liked trails related to a hiker
 ************************************************************************************************/

class HikerLikedTrailsViewModel: ListDataViewModel<Trail>(Trail::class.java) {

    fun loadLikedTrailsRealTime(hikerId: String) {
        loadDataRealTime(HikerFirebaseQueries.getLikedTrails(hikerId))
    }
}