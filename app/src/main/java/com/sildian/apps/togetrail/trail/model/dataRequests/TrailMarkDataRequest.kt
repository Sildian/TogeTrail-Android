package com.sildian.apps.togetrail.trail.model.dataRequests

import com.sildian.apps.togetrail.common.baseDataRequests.SpecificDataRequest
import com.sildian.apps.togetrail.hiker.model.support.CurrentHikerInfo
import com.sildian.apps.togetrail.hiker.model.dataRepository.HikerRepository
import com.sildian.apps.togetrail.trail.model.core.Trail

/*************************************************************************************************
 * Marks a trail
 ************************************************************************************************/

class TrailMarkDataRequest(
    private val trail: Trail?,
    private val hikerRepository: HikerRepository
)
    : SpecificDataRequest()
{

    override suspend fun run() {
        CurrentHikerInfo.currentHiker?.let { hiker ->
            trail?.let { trail ->
                trail.id?.let { trailId ->
                    hikerRepository.updateHikerMarkedTrail(hiker.id, trail)
                } ?:
                throw IllegalArgumentException("Cannot mark a trail without id")
            } ?:
            throw NullPointerException("Cannot mark a null trail")
        } ?:
        throw NullPointerException("Cannot mark a trail when the current hiker is null")
    }
}