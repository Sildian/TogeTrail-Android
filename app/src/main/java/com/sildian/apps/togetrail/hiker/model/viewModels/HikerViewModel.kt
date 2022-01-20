package com.sildian.apps.togetrail.hiker.model.viewModels

import com.sildian.apps.togetrail.common.baseViewModels.SingleDataViewModel
import com.sildian.apps.togetrail.common.utils.cloudHelpers.AuthRepository
import com.sildian.apps.togetrail.common.utils.cloudHelpers.StorageRepository
import com.sildian.apps.togetrail.hiker.model.core.Hiker
import com.sildian.apps.togetrail.hiker.model.dataRepository.HikerRepository
import com.sildian.apps.togetrail.hiker.model.dataRequests.*

/*************************************************************************************************
 * This viewModel observes a single Hiker related data
 ************************************************************************************************/

class HikerViewModel : SingleDataViewModel<Hiker>(Hiker::class.java) {

    /***********************************Repositories*********************************************/

    private val authRepository = AuthRepository()
    private val storageRepository = StorageRepository()
    private val hikerRepository = HikerRepository()

    /***********************************Extra data********************************************/

    private var imagePathToUpload: String? = null
    private var imagePathToDelete: String? = null

    /********************************Extra data monitoring************************************/

    fun updateImagePathToUpload(imagePath:String) {
        this.mutableData.value?.photoUrl?.let { photoUrl ->
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
            this.mutableData.value,
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
            this.mutableData.value,
            text,
            this.hikerRepository
        ))
    }

    fun markLastMessageAsRead() {
        runSpecificRequest(HikerReadMessageDataRequest(
            this.mutableData.value,
            this.hikerRepository
        ))
    }
}