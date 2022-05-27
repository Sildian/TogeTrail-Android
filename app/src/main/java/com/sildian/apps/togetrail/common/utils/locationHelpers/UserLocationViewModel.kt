package com.sildian.apps.togetrail.common.utils.locationHelpers

import android.location.Location
import com.sildian.apps.togetrail.common.baseViewModels.SingleDataViewModel
import com.sildian.apps.togetrail.common.utils.coroutinesHelpers.CoroutineIODispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

/*************************************************************************************************
 * This viewModel observes User location related data
 ************************************************************************************************/

@HiltViewModel
class UserLocationViewModel @Inject constructor(
    @CoroutineIODispatcher dispatcher: CoroutineDispatcher,
    private val userLocationFinder: UserLocationFinder
)
    : SingleDataViewModel<Location>(Location::class.java, dispatcher)
{

    suspend fun findLastLocationResult(): Location? =
        loadDataResult(FindUserLastLocationDataRequest(this.dispatcher, this.userLocationFinder))
}