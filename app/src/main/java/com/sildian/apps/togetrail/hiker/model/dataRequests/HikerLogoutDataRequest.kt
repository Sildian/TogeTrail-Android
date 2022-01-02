package com.sildian.apps.togetrail.hiker.model.dataRequests

import com.sildian.apps.togetrail.common.baseDataRequests.SpecificDataRequest
import com.sildian.apps.togetrail.common.utils.cloudHelpers.AuthRepository
import com.sildian.apps.togetrail.hiker.model.support.CurrentHikerInfo

/*************************************************************************************************
 * Logs the current hiker out using the current user authentication
 ************************************************************************************************/

class HikerLogoutDataRequest(
    private val authRepository: AuthRepository
)
    : SpecificDataRequest() {

    override suspend fun run() {
        CurrentHikerInfo.currentHiker = null
        authRepository.signUserOut()
    }
}