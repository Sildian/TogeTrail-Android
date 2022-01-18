package com.sildian.apps.togetrail.trail.model.support

import com.sildian.apps.togetrail.common.baseViewModels.ListDataViewModel
import com.sildian.apps.togetrail.hiker.model.support.HikerFirebaseQueries
import com.sildian.apps.togetrail.trail.model.core.Trail

/*************************************************************************************************
 * This viewModel observes the list of marked trails related to a hiker
 ************************************************************************************************/

class HikerMarkedTrailsViewModel: ListDataViewModel<Trail>(Trail::class.java) {

    fun loadMarkedTrailsRealTime(hikerId: String) {
        loadDataRealTime(HikerFirebaseQueries.getMarkedTrails(hikerId))
    }
}