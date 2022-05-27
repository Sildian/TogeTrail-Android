package com.sildian.apps.togetrail.common.utils.locationHelpers

import android.location.Location
import com.sildian.apps.togetrail.common.baseDataRequests.LoadDataRequest
import kotlinx.coroutines.CoroutineDispatcher

/*************************************************************************************************
 * Finds the user's last location
 ************************************************************************************************/

class FindUserLastLocationDataRequest(
    dispatcher: CoroutineDispatcher,
    private val userLocationFinder: UserLocationFinder
):
    LoadDataRequest<Location>(dispatcher)
{

    override suspend fun load(): Location? =
        this.userLocationFinder.findLastLocation()
}