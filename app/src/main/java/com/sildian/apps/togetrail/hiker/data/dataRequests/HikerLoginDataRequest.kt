package com.sildian.apps.togetrail.hiker.data.dataRequests

import com.sildian.apps.togetrail.common.baseDataRequests.LoadDataRequest
import com.sildian.apps.togetrail.common.utils.cloudHelpers.AuthRepository
import com.sildian.apps.togetrail.hiker.data.core.Hiker
import com.sildian.apps.togetrail.hiker.data.core.HikerHistoryItem
import com.sildian.apps.togetrail.hiker.data.core.HikerHistoryType
import com.sildian.apps.togetrail.hiker.data.helpers.CurrentHikerInfo
import com.sildian.apps.togetrail.hiker.data.helpers.HikerBuilder
import com.sildian.apps.togetrail.hiker.data.source.HikerRepository
import kotlinx.coroutines.CoroutineDispatcher

/*************************************************************************************************
 * Logs a hiker in using the current user authentication
 * If the current user has no hiker profile, a new one is created
 ************************************************************************************************/

class HikerLoginDataRequest(
    dispatcher: CoroutineDispatcher,
    private val authRepository: AuthRepository,
    private val hikerRepository: HikerRepository
)
    : LoadDataRequest<Hiker>(dispatcher) {

    override suspend fun load(): Hiker? {
        return authRepository.getCurrentUser()?.let { user ->
            val hiker = hikerRepository.getHiker(user.uid)?: createHiker()
            CurrentHikerInfo.currentHiker = hiker
            hiker
        } ?:
        throw NullPointerException("Cannot perform the requested operation when the current user is null")
    }

    private suspend fun createHiker(): Hiker? =
        authRepository.getCurrentUser()?.let { user ->
            val hiker = HikerBuilder()
                .withFirebaseUser(user)
                .build()
            hikerRepository.updateHiker(hiker)
            val historyItem = HikerHistoryItem(
                HikerHistoryType.HIKER_REGISTERED,
                hiker.registrationDate
            )
            hikerRepository.addHikerHistoryItem(hiker.id, historyItem)
            hiker
        }
}