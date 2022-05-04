package com.sildian.apps.togetrail.hiker.data.viewModels

import com.sildian.apps.togetrail.common.baseViewModels.SingleDataViewModel
import com.sildian.apps.togetrail.common.utils.cloudHelpers.AuthRepository
import com.sildian.apps.togetrail.common.utils.cloudHelpers.StorageRepository
import com.sildian.apps.togetrail.common.utils.coroutinesHelpers.CoroutineIODispatcher
import com.sildian.apps.togetrail.hiker.data.core.Hiker
import com.sildian.apps.togetrail.hiker.data.source.HikerRepository
import com.sildian.apps.togetrail.hiker.data.dataRequests.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

/*************************************************************************************************
 * This viewModel observes a single Hiker related data
 ************************************************************************************************/

@HiltViewModel
class HikerViewModel @Inject constructor(
    @CoroutineIODispatcher dispatcher: CoroutineDispatcher,
    val authRepository: AuthRepository,
    val storageRepository: StorageRepository,
    val hikerRepository: HikerRepository
)
    : SingleDataViewModel<Hiker>(Hiker::class.java, dispatcher) {

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
        loadData(HikerLoadDataRequest(this.dispatcher, hikerId, this.hikerRepository))
    }

    fun saveHiker() {
        saveData(HikerSaveDataRequest(
            this.dispatcher,
            this.mutableData.value?.data,
            this.imagePathToDelete,
            this.imagePathToUpload,
            this.authRepository,
            this.storageRepository,
            this.hikerRepository
        ))
    }

    fun loginUser() {
        loadData(HikerLoginDataRequest(this.dispatcher, this.authRepository, this.hikerRepository))
    }

    fun logoutUser() {
        clearQueryRegistration()
        this.mutableData.postValue(null)
        runSpecificRequest(HikerLogoutDataRequest(this.dispatcher, this.authRepository))
    }

    fun resetUserPassword() {
        runSpecificRequest(HikerResetPasswordDataRequest(this.dispatcher, this.authRepository))
    }

    fun deleteUserAccount() {
        clearQueryRegistration()
        runSpecificRequest(HikerDeleteAccountDataRequest(
            this.dispatcher,
            this.authRepository,
            this.storageRepository,
            this.hikerRepository
        ))
    }

    fun sendMessage(text: String) {
        runSpecificRequest(HikerSendMessageDataRequest(
            this.dispatcher,
            this.mutableData.value?.data,
            text,
            this.hikerRepository
        ))
    }

    fun markLastMessageAsRead() {
        runSpecificRequest(HikerReadMessageDataRequest(
            this.dispatcher,
            this.mutableData.value?.data,
            this.hikerRepository
        ))
    }
}