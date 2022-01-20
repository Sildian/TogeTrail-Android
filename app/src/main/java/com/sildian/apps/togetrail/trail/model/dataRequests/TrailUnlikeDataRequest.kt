package com.sildian.apps.togetrail.trail.model.dataRequests

import com.sildian.apps.togetrail.common.baseDataRequests.SpecificDataRequest
import com.sildian.apps.togetrail.hiker.model.support.CurrentHikerInfo
import com.sildian.apps.togetrail.hiker.model.dataRepository.HikerRepository
import com.sildian.apps.togetrail.trail.model.core.Trail
import com.sildian.apps.togetrail.trail.model.dataRepository.TrailRepository

/*************************************************************************************************
 * Unlikes a trail if it was previously liked by the user
 ************************************************************************************************/

class TrailUnlikeDataRequest(
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
                    if (trail.nbLikes > 0) {
                        trail.nbLikes--
                        trailRepository.updateTrail(trail)
                    }
                    hikerRepository.deleteHikerLikedTrail(hiker.id, trailId)
                } ?:
                throw IllegalArgumentException("Cannot unlike a trail without id")
            } ?:
            throw NullPointerException("Cannot unlike a null trail")
        } ?:
        throw NullPointerException("Cannot unlike a trail when the current hiker is null")
    }
}