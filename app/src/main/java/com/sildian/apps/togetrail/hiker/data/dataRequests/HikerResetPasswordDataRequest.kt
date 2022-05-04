package com.sildian.apps.togetrail.hiker.data.dataRequests

import com.sildian.apps.togetrail.common.baseDataRequests.SpecificDataRequest
import com.sildian.apps.togetrail.common.utils.cloudHelpers.AuthRepository
import kotlinx.coroutines.CoroutineDispatcher

/*************************************************************************************************
 * Resets the current user's password
 ************************************************************************************************/

class HikerResetPasswordDataRequest(
    dispatcher: CoroutineDispatcher,
    private val authRepository: AuthRepository
)
    : SpecificDataRequest(dispatcher) {

    override suspend fun run() {
        authRepository.getCurrentUser()?.let { user ->
            authRepository.resetUserPassword()
        } ?:
        throw NullPointerException("Cannot perform the requested operation when the current user is null")
    }
}