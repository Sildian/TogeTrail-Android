package com.sildian.apps.togetrail.hiker.model.dataRequests

import com.sildian.apps.togetrail.common.baseDataRequests.LoadDataRequest
import com.sildian.apps.togetrail.hiker.model.core.Hiker
import com.sildian.apps.togetrail.hiker.model.dataRepository.HikerRepository
import kotlinx.coroutines.CoroutineDispatcher

/*************************************************************************************************
 * Loads a hiker from the database
 ************************************************************************************************/

class HikerLoadDataRequest(
    dispatcher: CoroutineDispatcher,
    private val hikerId: String,
    private val hikerRepository: HikerRepository
    )
    : LoadDataRequest<Hiker>(dispatcher) {

    override suspend fun load(): Hiker? =
        hikerRepository.getHiker(hikerId)
}