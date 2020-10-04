package com.sildian.apps.togetrail.hiker.model.support

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.sildian.apps.togetrail.common.baseViewModels.BaseObservableViewModel
import com.sildian.apps.togetrail.hiker.model.core.Hiker
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
    }

    /***********************************Exception handler***************************************/

    private val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        Log.e(TAG, throwable.message.toString())
    }

    /**********************************Data requester*******************************************/

    private val hikerDataRequester = HikerDataRequester()

    /***************************************Data*************************************************/

    var hiker: Hiker?=null ; private set                //The hiker
    private var imagePathToUpload:String?=null          //Path of image to upload into the cloud
    private var imagePathToDelete:String?=null          //Path of image to delete from the cloud

    /************************************Data monitoring*****************************************/

    /**
     * Gives an image to be stored on the cloud before running saveHikerInDatabase
     * If the hiker already has a photo, it will be deleted
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
     * Gives an image to be deleted from the cloud before running saveHikerInDatabase
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
     * Loads an hiker from the database in real time
     * @param hikerId : the hiker's id
     * @param successCallback : the callback to handle a success in the query
     * @param failureCallback : the callback to handle a failure in the query
     */

    fun loadHikerFromDatabaseRealTime(hikerId:String, successCallback:(()->Unit)?=null, failureCallback:((Exception)->Unit)?=null) {
        this.queryRegistration?.remove()
        this.queryRegistration = hikerDataRequester.loadHikerFromDatabaseRealTime(hikerId)
            .addSnapshotListener { snapshot, e ->
                if (snapshot != null) {
                    hiker = snapshot.toObject(Hiker::class.java)
                    notifyDataChanged()
                    Log.d(TAG, "Successfully loaded hiker from database")
                    successCallback?.invoke()
                }
                else if(e != null){
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
                hiker = async { hikerDataRequester.loadHikerFromDatabase(hikerId) }.await()
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
            try {
                launch { hikerDataRequester.saveHikerInDatabase(hiker, imagePathToDelete, imagePathToUpload) }.join()
                Log.d(TAG, "Successfully saved hiker in database")
                successCallback?.invoke()
            }
            catch(e:Exception) {
                Log.e(TAG, "Failed to save hiker in database : ${e.message}")
                failureCallback?.invoke(e)
                throw e
            }
        }
    }

    /**
     * Logs the user in and if this is a new user, creates a new Hiker
     * @param successCallback : the callback to handle a success in the query
     * @param failureCallback : the callback to handle a failure in the query
     */

    fun loginUser(successCallback:(()->Unit)?=null, failureCallback:((Exception)->Unit)?=null){
        viewModelScope.launch(this.exceptionHandler) {
            try {
                hiker = async { hikerDataRequester.loginUser() }.await()
                notifyDataChanged()
                Log.d(TAG, "Successfully logged the user in")
                successCallback?.invoke()
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
        hikerDataRequester.logoutUser()
        this.hiker = null
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
                launch { hikerDataRequester.resetUserPassword() }.join()
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
                hikerDataRequester.deleteUserAccount()
                Log.d(TAG, "Successfully deleted the user account")
                successCallback?.invoke()
            }
            catch(e:Exception){
                Log.e(TAG, "Failed to delete the user account : ${e.message}")
                failureCallback?.invoke(e)
                throw e
            }
        }
    }
}