package com.sildian.apps.togetrail.hiker.model.dataRequests

import com.sildian.apps.togetrail.common.baseDataRequests.SpecificDataRequest
import com.sildian.apps.togetrail.common.utils.cloudHelpers.AuthRepository
import com.sildian.apps.togetrail.hiker.model.support.CurrentHikerInfo
import kotlinx.coroutines.CoroutineDispatcher

/*************************************************************************************************
 * Logs the current hiker out using the current user authentication
 ************************************************************************************************/

class HikerLogoutDataRequest(
    dispatcher: CoroutineDispatcher,
    private val authRepository: AuthRepository
)
    : SpecificDataRequest(dispatcher) {

    override suspend fun run() {
        CurrentHikerInfo.currentHiker = null
        authRepository.signUserOut()
    }
}