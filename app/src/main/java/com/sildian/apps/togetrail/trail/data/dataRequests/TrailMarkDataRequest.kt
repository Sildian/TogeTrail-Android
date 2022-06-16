package com.sildian.apps.togetrail.trail.data.dataRequests

import com.sildian.apps.togetrail.common.baseDataRequests.SpecificDataRequest
import com.sildian.apps.togetrail.hiker.data.helpers.CurrentHikerInfo
import com.sildian.apps.togetrail.hiker.data.source.HikerRepository
import com.sildian.apps.togetrail.trail.data.models.Trail
import kotlinx.coroutines.CoroutineDispatcher

/*************************************************************************************************
 * Marks a trail
 ************************************************************************************************/

class TrailMarkDataRequest(
    dispatcher: CoroutineDispatcher,
    private val trail: Trail?,
    private val hikerRepository: HikerRepository
)
    : SpecificDataRequest(dispatcher)
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