package com.sildian.apps.togetrail.hiker.model.dataRequests

import android.util.Log
import com.sildian.apps.togetrail.common.baseDataRequests.SpecificDataRequest
import com.sildian.apps.togetrail.common.utils.cloudHelpers.AuthRepository
import com.sildian.apps.togetrail.common.utils.cloudHelpers.StorageRepository
import com.sildian.apps.togetrail.hiker.model.support.CurrentHikerInfo
import com.sildian.apps.togetrail.hiker.model.dataRepository.HikerRepository

/*************************************************************************************************
 * Deletes a user account as well as the related hiker profile
 ************************************************************************************************/

class HikerDeleteAccountDataRequest(
    private val authRepository: AuthRepository,
    private val storageRepository: StorageRepository,
    private val hikerRepository: HikerRepository
)
    : SpecificDataRequest() {

    companion object {
        private const val TAG = "HikerDeleteDataRequest"
    }
    
    override suspend fun run() {
        authRepository.getCurrentUser()?.let { user ->
            CurrentHikerInfo.currentHiker?.let { hiker ->
                deleteHikerPhoto()
                deleteHiker()
                deleteUserAccount()
                CurrentHikerInfo.currentHiker = null
            } ?:
            throw NullPointerException("Cannot perform the requested operation when the current hiker is null")
        } ?:
        throw NullPointerException("Cannot perform the requested operation when the current user is null")
    }

    private suspend fun deleteHikerPhoto() {
        CurrentHikerInfo.currentHiker?.photoUrl?.let { photoUrl ->
            try {
                storageRepository.deleteImage(photoUrl)
            } catch (e: Exception) {
                Log.w(TAG, "Failed to delete photo at url $photoUrl : ${e.message}")
            }
        }
    }

    private suspend fun deleteHiker() {
        CurrentHikerInfo.currentHiker?.let { hiker ->
            hikerRepository.deleteHiker(hiker)
        }
    }

    private suspend fun deleteUserAccount() {
        authRepository.deleteUserAccount()
    }
}