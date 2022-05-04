package com.sildian.apps.togetrail.hiker.data.dataRequests

import com.sildian.apps.togetrail.common.baseDataRequests.LoadDataRequest
import com.sildian.apps.togetrail.hiker.data.core.Hiker
import com.sildian.apps.togetrail.hiker.data.source.HikerRepository
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