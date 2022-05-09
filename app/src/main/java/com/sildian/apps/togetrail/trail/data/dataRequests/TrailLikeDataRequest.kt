package com.sildian.apps.togetrail.trail.data.dataRequests

import com.sildian.apps.togetrail.common.baseDataRequests.SpecificDataRequest
import com.sildian.apps.togetrail.hiker.data.helpers.CurrentHikerInfo
import com.sildian.apps.togetrail.hiker.data.source.HikerRepository
import com.sildian.apps.togetrail.trail.data.models.Trail
import com.sildian.apps.togetrail.trail.data.source.TrailRepository
import kotlinx.coroutines.CoroutineDispatcher

/*************************************************************************************************
 * Likes a trail
 ************************************************************************************************/

class TrailLikeDataRequest(
    dispatcher: CoroutineDispatcher,
    private val trail: Trail?,
    private val trailRepository: TrailRepository,
    private val hikerRepository: HikerRepository
)
    : SpecificDataRequest(dispatcher)
{

    override suspend fun run() {
        CurrentHikerInfo.currentHiker?.let { hiker ->
            trail?.let { trail ->
                trail.id?.let { trailId ->
                    trail.nbLikes++
                    trailRepository.updateTrail(trail)
                    hikerRepository.updateHikerLikedTrail(hiker.id, trail)
                } ?:
                throw IllegalArgumentException("Cannot like a trail without id")
            } ?:
            throw NullPointerException("Cannot like a null trail")
        } ?:
        throw NullPointerException("Cannot like a trail when the current hiker is null")
    }
}