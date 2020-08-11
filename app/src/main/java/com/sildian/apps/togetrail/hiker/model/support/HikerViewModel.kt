package com.sildian.apps.togetrail.hiker.model.support

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.sildian.apps.togetrail.common.baseViewModels.BaseObservableViewModel
import com.sildian.apps.togetrail.common.utils.cloudHelpers.AuthRepository
import com.sildian.apps.togetrail.common.utils.cloudHelpers.StorageRepository
import com.sildian.apps.togetrail.hiker.model.core.Hiker
import com.sildian.apps.togetrail.hiker.model.core.HikerHistoryItem
import com.sildian.apps.togetrail.hiker.model.core.HikerHistoryType
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

/*************************************************************************************************
 * This viewModel observes a single Hiker related data
 ************************************************************************************************/

class HikerViewModel : BaseObservableViewModel() {

    /************************************Static items********************************************/

    companion object{

        /**Logs**/
        private const val TAG="HikerViewModel"

        /**Messages**/
        private const val EXCEPTION_MESSAGE_SAVE_NULL="Cannot save a null hiker"
        private const val EXCEPTION_MESSAGE_LOGIN_NULL="Cannot login with a null user"
        private const val EXCEPTION_MESSAGE_DELETE_NULL="Cannot delete a null hiker"
    }

    /***********************************Exception handler***************************************/

    private val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        Log.e(TAG, throwable.message.toString())
    }

    /***************************************Data*************************************************/

    var hiker: Hiker?=null ; private set                //The hiker
    private var imagePathToUpload:String?=null          //Path of image to upload into the cloud
    private var imagePathToDelete:String?=null          //Path of image to delete from the cloud

    /************************************Data monitoring*****************************************/

    /**
     * Loads an hiker from the database in real time
     * @param hikerId : the hiker's id
     * @param successCallback : the callback to handle a success in the query
     * @param failureCallback : the callback to handle a failure in the query
     */

    fun loadHikerFromDatabaseRealTime(hikerId:String, successCallback:(()->Unit)?=null, failureCallback:((Exception)->Unit)?=null) {
        this.queryRegistration?.remove()
        this.queryRegistration = HikerRepository.getHikerReference(hikerId)
            .addSnapshotListener { snapshot, e ->
                if (snapshot != null) {
                    hiker = snapshot.toObject(Hiker::class.java)
                    notifyDataChanged()
                    Log.d(TAG, "Successfully loaded hiker from database")
                    successCallback?.invoke()
                }
                else if(e!=null){
                    Log.e(TAG, "Failed to load hiker from database : ${e.message}")
                    failureCallback?.invoke(e)
                }
            }
    }

    /**
     * Loads an hiker from the database
     * @param hikerId : the hiker's id
     * @param successCallback : the callback to handle a success in the query
     * @param failureCallback : the callback to handle a failure in the query
     */

    fun loadHikerFromDatabase(hikerId:String, successCallback:(()->Unit)?=null, failureCallback:((Exception)->Unit)?=null){
        viewModelScope.launch(this.exceptionHandler) {
            try {
                val deferredHiker = async { HikerRepository.getHiker(hikerId) }
                hiker = deferredHiker.await()
                notifyDataChanged()
                Log.d(TAG, "Successfully loaded hiker from database")
                successCallback?.invoke()
            }
            catch(e:Exception){
                Log.e(TAG, "Failed to load hiker from database : ${e.message}")
                failureCallback?.invoke(e)
                throw e
            }
        }
    }

    /**
     * Saves the hiker within the database, after uploading or deleting the profile's image
     * @param successCallback : the callback to handle a success in the query
     * @param failureCallback : the callback to handle a failure in the query
     */

    fun saveHikerInDatabase(successCallback:(()->Unit)?=null, failureCallback:((Exception)->Unit)?=null){
        viewModelScope.launch(this.exceptionHandler) {
            try{
                if(hiker!=null){
                    imagePathToDelete?.let { url ->
                        launch { StorageRepository.deleteImage(url) }.join()
                    }
                    imagePathToUpload?.let { uri ->
                        val deferredNewImageUrl=async { StorageRepository.uploadImage(uri) }
                        val newImageUrl=deferredNewImageUrl.await()
                        hiker?.photoUrl=newImageUrl
                    }
                    launch { HikerRepository.updateHiker(hiker!!) }.join()
                    launch { AuthRepository.updateUserProfile(hiker!!.name!!, hiker!!.photoUrl) }.join()
                    Log.d(TAG, "Successfully saved hiker in database")
                    successCallback?.invoke()
                }
                else{
                    Log.e(TAG, "Failed to save hiker in database : $EXCEPTION_MESSAGE_SAVE_NULL")
                    failureCallback?.invoke(NullPointerException(EXCEPTION_MESSAGE_SAVE_NULL))
                }
            }
            catch(e:Exception){
                Log.e(TAG, "Failed to save hiker in database : ${e.message}")
                failureCallback?.invoke(e)
                throw e
            }
        }
    }

    /**
     * Gives an image to be stored on the cloud
     * @param imagePath : the temporary image's uri
     */

    fun updateImagePathToUpload(imagePath:String){
        this.hiker?.photoUrl?.let { photoUrl ->
            if (photoUrl.startsWith("https://")) {
                this.imagePathToDelete = photoUrl
            }
        }
        this.imagePathToUpload = imagePath
    }

    /**
     * Gives an image to be deleted from the cloud
     * @param imagePath : the image's url
     */

    fun updateImagePathToDelete(imagePath:String){
        this.imagePathToUpload=null
        if(imagePath.startsWith("https://")){
            this.imagePathToDelete=imagePath
        }
    }

    /**Clears images paths**/

    fun clearImagePaths() {
        this.imagePathToUpload = null
        this.imagePathToDelete = null
    }

    /**
     * Logs the user in and if this is a new user, creates a new Hiker
     * @param successCallback : the callback to handle a success in the query
     * @param failureCallback : the callback to handle a failure in the query
     */

    fun loginUser(successCallback:(()->Unit)?=null, failureCallback:((Exception)->Unit)?=null){
        viewModelScope.launch(this.exceptionHandler) {
            try {

                /*Gets the current user and the related Hiker info*/

                val user = AuthRepository.getCurrentUser()
                if (user != null) {
                    val deferredHiker = async { HikerRepository.getHiker(user.uid) }
                    hiker = deferredHiker.await()

                    /*If the hiker is null, then creates a new Hiker in the database*/

                    if (hiker == null) {
                        hiker = HikerBuilder()
                            .withFirebaseUser(user)
                            .build()
                        launch { HikerRepository.updateHiker(hiker!!) }.join()

                        /*Also creates an history item*/

                        val historyItem = HikerHistoryItem(
                            HikerHistoryType.HIKER_REGISTERED,
                            hiker?.registrationDate!!
                        )
                        launch { HikerRepository.addHikerHistoryItem(hiker!!.id, historyItem) }.join()
                    }

                    CurrentHikerInfo.currentHiker = hiker

                    /*Then notifies the callbacks*/

                    notifyDataChanged()
                    Log.d(TAG, "Successfully logged the user in")
                    successCallback?.invoke()
                }
                else {
                    Log.e(TAG, "Failed to log the user in : $EXCEPTION_MESSAGE_LOGIN_NULL")
                    failureCallback?.invoke(NullPointerException(EXCEPTION_MESSAGE_LOGIN_NULL))
                }
            }
            catch(e:Exception){
                Log.e(TAG, "Failed to log the user in : ${e.message}")
                failureCallback?.invoke(e)
                throw e
            }
        }
    }

    /**Logs the user out**/

    fun logoutUser(){
        this.hiker=null
        CurrentHikerInfo.currentHiker = null
        AuthRepository.signUserOut()
        notifyDataChanged()
    }

    /**
     * Resets the user's password
     * @param successCallback : the callback to handle a success in the query
     * @param failureCallback : the callback to handle a failure in the query
     */

    fun resetUserPassword(successCallback:(()->Unit)?=null, failureCallback:((Exception)->Unit)?=null){
        viewModelScope.launch(this.exceptionHandler) {
            try{
                launch { AuthRepository.resetUserPassword() }.join()
                Log.d(TAG, "Successfully reset the user password")
                successCallback?.invoke()
            }
            catch(e:Exception){
                Log.e(TAG, "Failed to reset the user password : ${e.message}")
                failureCallback?.invoke(e)
                throw e
            }
        }
    }

    /**
     * Deletes the user's account as well as all related hiker data and the profile's image
     * @param successCallback : the callback to handle a success in the query
     * @param failureCallback : the callback to handle a failure in the query
     */

    fun deleteUserAccount(successCallback:(()->Unit)?=null, failureCallback:((Exception)->Unit)?=null){
        viewModelScope.launch(this.exceptionHandler) {
            try{
                if(hiker!=null){
                    hiker?.photoUrl?.let { photoUrl ->
                        launch { StorageRepository.deleteImage(photoUrl) }.join()
                    }
                    launch { HikerRepository.deleteHiker(hiker!!) }.join()
                    launch { AuthRepository.deleteUserAccount() }.join()
                    Log.d(TAG, "Successfully deleted the user account")
                    successCallback?.invoke()
                }
                else{
                    Log.e(TAG, "Failed to delete the user account : $EXCEPTION_MESSAGE_DELETE_NULL")
                    failureCallback?.invoke(NullPointerException(EXCEPTION_MESSAGE_DELETE_NULL))
                }
            }
            catch(e:Exception){
                Log.e(TAG, "Failed to delete the user account : ${e.message}")
                failureCallback?.invoke(e)
                throw e
            }
        }
    }
}