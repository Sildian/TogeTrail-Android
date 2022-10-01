package com.sildian.apps.togetrail.hiker.data.dataRequests

import com.sildian.apps.togetrail.common.baseDataRequests.SpecificDataRequest
import com.sildian.apps.togetrail.common.utils.cloudHelpers.AuthRepository
import com.sildian.apps.togetrail.common.utils.isValidEmail
import com.sildian.apps.togetrail.hiker.data.models.Hiker
import com.sildian.apps.togetrail.hiker.data.source.HikerRepository
import kotlinx.coroutines.CoroutineDispatcher

/*************************************************************************************************
 * Change the current user's email address
 ************************************************************************************************/

class HikerChangeEmailAddressDataRequest (
    dispatcher: CoroutineDispatcher,
    private val hiker: Hiker?,
    private val newEmailAddress: String?,
    private val authRepository: AuthRepository,
    private val hikerRepository: HikerRepository,
)
    : SpecificDataRequest(dispatcher) {

    override suspend fun run() {
        hiker?.let { hiker ->
            authRepository.getCurrentUser()?.let { user ->
                newEmailAddress?.let {
                    if (newEmailAddress.isValidEmail()) {
                        authRepository.changeUserEmailAddress(newEmailAddress)
                        hiker.email = newEmailAddress
                        hikerRepository.updateHiker(hiker)
                    } else {
                        throw IllegalArgumentException("Cannot perform the requested operation with an invalid email address")
                    }
                } ?:
                throw NullPointerException("Cannot perform the requested operation with a null email address")
            } ?:
            throw NullPointerException("Cannot perform the requested operation when the current user is null")
        } ?:
        throw NullPointerException("Cannot perform the requested operation with a null hiker")
    }
}