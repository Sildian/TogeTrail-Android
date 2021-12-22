package com.sildian.apps.togetrail.hiker.model.dataRequests

import android.util.Log
import com.sildian.apps.togetrail.common.baseDataRequests.SaveDataRequest
import com.sildian.apps.togetrail.common.utils.cloudHelpers.AuthRepository
import com.sildian.apps.togetrail.common.utils.cloudHelpers.StorageRepository
import com.sildian.apps.togetrail.hiker.model.core.Hiker
import com.sildian.apps.togetrail.hiker.model.support.HikerRepository

/*************************************************************************************************
 * Saves a hiker within the database
 ************************************************************************************************/

class HikerSaveDataRequest(
    hiker: Hiker?,
    private val imagePathToDelete: String?,
    private val imagePathToUpload: String?,
    private val authRepository: AuthRepository,
    private val storageRepository: StorageRepository,
    private val hikerRepository: HikerRepository
)
    : SaveDataRequest<Hiker>(hiker) {

    companion object {
        private const val TAG = "HikerSaveDataRequest"
    }

    override suspend fun save() {
        this.data?.let {
            deleteImageFromStorage()
            uploadImageToStorage()
            saveHiker()
        }?:
        throw NullPointerException("Cannot perform the requested operation with a null hiker")
    }

    private suspend fun deleteImageFromStorage() {
        this.imagePathToDelete?.let { url ->
            try {
                this.storageRepository.deleteImage(url)
            } catch (e: Exception) {
                Log.w(TAG, "Failed to delete photo at url $url : ${e.message}")
            }
        }
    }

    private suspend fun uploadImageToStorage() {
        this.imagePathToUpload?.let { uri ->
            try {
                val newImageUrl = this.storageRepository.uploadImage(uri)
                this.data?.photoUrl = newImageUrl
            } catch (e: Exception) {
                Log.w(TAG, "Failed to upload photo from uri $uri : ${e.message}")
            }
        }
    }
    
    private suspend fun saveHiker() {
        this.data?.let { hiker ->
            this.hikerRepository.updateHiker(hiker)
            hiker.name?.let { hikerName ->
                this.authRepository.updateUserProfile(hikerName, hiker.photoUrl)
            }
        }
    }
}