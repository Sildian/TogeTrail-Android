package com.sildian.apps.togetrail.trail.model.dataRequests

import com.sildian.apps.togetrail.common.baseDataRequests.SpecificDataRequest
import com.sildian.apps.togetrail.hiker.model.support.CurrentHikerInfo
import com.sildian.apps.togetrail.hiker.model.support.HikerRepository
import com.sildian.apps.togetrail.trail.model.core.Trail

/*************************************************************************************************
 * Unmarks a trail if it was previously marked by the user
 ************************************************************************************************/

class TrailUnmarkDataRequest(
    private val trail: Trail?,
    private val hikerRepository: HikerRepository
)
    : SpecificDataRequest()
{

    override suspend fun run() {
        CurrentHikerInfo.currentHiker?.let { hiker ->
            trail?.let { trail ->
                trail.id?.let { trailId ->
                    hikerRepository.deleteHikerMarkedTrail(hiker.id, trailId)
                } ?:
                throw IllegalArgumentException("Cannot unmark a trail without id")
            } ?:
            throw NullPointerException("Cannot unmark a null trail")
        } ?:
        throw NullPointerException("Cannot unmark a trail when the current hiker is null")
    }
}