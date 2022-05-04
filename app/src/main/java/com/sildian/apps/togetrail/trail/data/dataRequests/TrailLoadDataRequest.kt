package com.sildian.apps.togetrail.trail.data.dataRequests

import com.sildian.apps.togetrail.common.baseDataRequests.LoadDataRequest
import com.sildian.apps.togetrail.trail.data.core.Trail
import com.sildian.apps.togetrail.trail.data.source.TrailRepository
import kotlinx.coroutines.CoroutineDispatcher

/*************************************************************************************************
 * Loads a trail from the database
 ************************************************************************************************/

class TrailLoadDataRequest(
    dispatcher: CoroutineDispatcher,
    private val trailId: String,
    private val trailRepository: TrailRepository
    )
    : LoadDataRequest<Trail>(dispatcher) {

    override suspend fun load(): Trail? =
        trailRepository.getTrail(trailId)
}