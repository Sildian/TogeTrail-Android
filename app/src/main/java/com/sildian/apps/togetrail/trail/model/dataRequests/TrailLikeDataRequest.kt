package com.sildian.apps.togetrail.trail.model.dataRequests

import com.sildian.apps.togetrail.common.baseDataRequests.SpecificDataRequest
import com.sildian.apps.togetrail.hiker.model.support.CurrentHikerInfo
import com.sildian.apps.togetrail.hiker.model.support.HikerRepository
import com.sildian.apps.togetrail.trail.model.core.Trail
import com.sildian.apps.togetrail.trail.model.support.TrailRepository

/*************************************************************************************************
 * Likes a trail
 ************************************************************************************************/

class TrailLikeDataRequest(
    private val trail: Trail?,
    private val trailRepository: TrailRepository,
    private val hikerRepository: HikerRepository
)
    : SpecificDataRequest()
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