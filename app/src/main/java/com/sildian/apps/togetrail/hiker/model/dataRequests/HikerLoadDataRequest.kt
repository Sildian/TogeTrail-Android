package com.sildian.apps.togetrail.hiker.model.dataRequests

import com.sildian.apps.togetrail.common.baseDataRequests.LoadDataRequest
import com.sildian.apps.togetrail.hiker.model.core.Hiker
import com.sildian.apps.togetrail.hiker.model.dataRepository.HikerRepository

/*************************************************************************************************
 * Loads a hiker from the database
 ************************************************************************************************/

class HikerLoadDataRequest(
    private val hikerId: String,
    private val hikerRepository: HikerRepository
    )
    : LoadDataRequest<Hiker>() {

    override suspend fun load(): Hiker? =
        hikerRepository.getHiker(hikerId)
}