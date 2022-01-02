package com.sildian.apps.togetrail.hiker.model.dataRequests

import com.sildian.apps.togetrail.common.baseDataRequests.SpecificDataRequest
import com.sildian.apps.togetrail.common.utils.cloudHelpers.AuthRepository

/*************************************************************************************************
 * Resets the current user's password
 ************************************************************************************************/

class HikerResetPasswordDataRequest(
    private val authRepository: AuthRepository
)
    : SpecificDataRequest() {

    override suspend fun run() {
        authRepository.getCurrentUser()?.let { user ->
            authRepository.resetUserPassword()
        } ?:
        throw NullPointerException("Cannot perform the requested operation when the current user is null")
    }
}