package com.sildian.apps.togetrail.hiker.model.viewModels

import com.sildian.apps.togetrail.common.baseViewModels.SingleDataViewModel
import com.sildian.apps.togetrail.common.utils.cloudHelpers.AuthRepository
import com.sildian.apps.togetrail.common.utils.cloudHelpers.StorageRepository
import com.sildian.apps.togetrail.hiker.model.core.Hiker
import com.sildian.apps.togetrail.hiker.model.dataRepository.HikerRepository
import com.sildian.apps.togetrail.hiker.model.dataRequests.*
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/*************************************************************************************************
 * This viewModel observes a single Hiker related data
 ************************************************************************************************/

@HiltViewModel
class HikerViewModel @Inject constructor() : SingleDataViewModel<Hiker>(Hiker::class.java) {

    /***********************************Repositories*********************************************/

    @Inject lateinit var authRepository: AuthRepository
    @Inject lateinit var storageRepository: StorageRepository
    @Inject lateinit var hikerRepository: HikerRepository

    /***********************************Extra data********************************************/

    private var imagePathToUpload: String? = null
    private var imagePathToDelete: String? = null

    /********************************Extra data monitoring************************************/

    fun updateImagePathToUpload(imagePath:String) {
        this.mutableData.value?.data?.photoUrl?.let { photoUrl ->
            if (photoUrl.startsWith("https://")) {
                this.imagePathToDelete = photoUrl
            }
        }
        this.imagePathToUpload = imagePath
    }

    fun updateImagePathToDelete(imagePath:String) {
        this.imagePathToUpload = null
        if (imagePath.startsWith("https://")) {
            this.imagePathToDelete = imagePath
        }
    }

    fun clearImagePaths() {
        this.imagePathToUpload = null
        this.imagePathToDelete = null
    }

    /***********************************Data monitoring***************************************/

    fun loadHikerRealTime(hikerId: String) {
        loadDataRealTime(this.hikerRepository.getHikerReference(hikerId))
    }

    fun loadHiker(hikerId: String) {
        loadData(HikerLoadDataRequest(hikerId, this.hikerRepository))
    }

    fun saveHiker() {
        saveData(HikerSaveDataRequest(
            this.mutableData.value?.data,
            this.imagePathToDelete,
            this.imagePathToUpload,
            this.authRepository,
            this.storageRepository,
            this.hikerRepository
        ))
    }

    fun loginUser() {
        loadData(HikerLoginDataRequest(this.authRepository, this.hikerRepository))
    }

    fun logoutUser() {
        clearQueryRegistration()
        this.mutableData.postValue(null)
        runSpecificRequest(HikerLogoutDataRequest(this.authRepository))
    }

    fun resetUserPassword() {
        runSpecificRequest(HikerResetPasswordDataRequest(this.authRepository))
    }

    fun deleteUserAccount() {
        clearQueryRegistration()
        runSpecificRequest(HikerDeleteAccountDataRequest(
            this.authRepository,
            this.storageRepository,
            this.hikerRepository
        ))
    }

    fun sendMessage(text: String) {
        runSpecificRequest(HikerSendMessageDataRequest(
            this.mutableData.value?.data,
            text,
            this.hikerRepository
        ))
    }

    fun markLastMessageAsRead() {
        runSpecificRequest(HikerReadMessageDataRequest(
            this.mutableData.value?.data,
            this.hikerRepository
        ))
    }
}