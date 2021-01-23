package com.sildian.apps.togetrail.hiker.model.support

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sildian.apps.togetrail.chat.model.core.Message
import com.sildian.apps.togetrail.common.baseViewModels.BaseViewModel
import com.sildian.apps.togetrail.hiker.model.core.Hiker
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

/*************************************************************************************************
 * This viewModel observes a single Hiker related data
 ************************************************************************************************/

class HikerViewModel : BaseViewModel() {

    /************************************Static items********************************************/

    companion object{

        /**Logs**/
        private const val TAG = "HikerViewModel"
    }

    /***********************************Exception handler***************************************/

    private val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        Log.e(TAG, throwable.message.toString())
    }

    /**********************************Data requester*******************************************/

    private val hikerDataRequester = HikerDataRequester()

    /*********************************Monitoring info*******************************************/

    private var imagePathToUpload: String? = null
    private var imagePathToDelete: String? = null

    /********************************LiveData to be observed*************************************/

    val hiker = MutableLiveData<Hiker?>()
    val saveRequestSuccess = MutableLiveData<Boolean>()
    val requestFailure = MutableLiveData<Exception?>()

    /************************************Data monitoring*****************************************/

    /**
     * Gives an image to be stored on the cloud before running saveHikerInDatabase
     * If the hiker already has a photo, it will be deleted
     * @param imagePath : the temporary image's uri
     */

    fun updateImagePathToUpload(imagePath:String) {
        this.hiker.value?.photoUrl?.let { photoUrl ->
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

    fun updateImagePathToDelete(imagePath:String) {
        this.imagePathToUpload=null
        if (imagePath.startsWith("https://")) {
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
     */

    fun loadHikerFromDatabaseRealTime(hikerId:String) {
        this.queryRegistration?.remove()
        this.queryRegistration = hikerDataRequester.loadHikerFromDatabaseRealTime(hikerId)
            .addSnapshotListener { snapshot, e ->
                if (snapshot != null) {
                    val result = snapshot.toObject(Hiker::class.java)
                    Log.d(TAG, "Successfully loaded hiker from database")
                    hiker.postValue(result)
                }
                else if(e != null) {
                    Log.e(TAG, "Failed to load hiker from database : ${e.message}")
                    requestFailure.postValue(e)
                }
            }
    }

    /**
     * Loads an hiker from the database
     * @param hikerId : the hiker's id
     */

    fun loadHikerFromDatabase(hikerId:String) {
        viewModelScope.launch(this.exceptionHandler) {
            try {
                val result = async { hikerDataRequester.loadHikerFromDatabase(hikerId) }.await()
                Log.d(TAG, "Successfully loaded hiker from database")
                hiker.postValue(result)
            }
            catch(e:Exception) {
                Log.e(TAG, "Failed to load hiker from database : ${e.message}")
                requestFailure.postValue(e)
            }
        }
    }

    /**
     * Saves the hiker within the database, after uploading or deleting the profile's image
     */

    fun saveHikerInDatabase() {
        viewModelScope.launch(this.exceptionHandler) {
            try {
                launch { hikerDataRequester.saveHikerInDatabase(hiker.value, imagePathToDelete, imagePathToUpload) }.join()
                Log.d(TAG, "Successfully saved hiker in database")
                saveRequestSuccess.postValue(true)
            }
            catch(e:Exception) {
                Log.e(TAG, "Failed to save hiker in database : ${e.message}")
                requestFailure.postValue(e)
            }
        }
    }

    /**
     * Logs the user in and if this is a new user, creates a new Hiker
     */

    fun loginUser() {
        viewModelScope.launch(this.exceptionHandler) {
            try {
                val result = async { hikerDataRequester.loginUser() }.await()
                Log.d(TAG, "Successfully logged the user in")
                hiker.postValue(result)
            }
            catch(e:Exception) {
                Log.e(TAG, "Failed to log the user in : ${e.message}")
                requestFailure.postValue(e)
            }
        }
    }

    /**Logs the user out**/

    fun logoutUser() {
        clearQueryRegistration()
        hikerDataRequester.logoutUser()
        this.hiker.postValue(null)
    }

    /**
     * Resets the user's password
     */

    fun resetUserPassword() {
        viewModelScope.launch(this.exceptionHandler) {
            try {
                launch { hikerDataRequester.resetUserPassword() }.join()
                Log.d(TAG, "Successfully reset the user password")
                saveRequestSuccess.postValue(true)
            }
            catch(e:Exception){
                Log.e(TAG, "Failed to reset the user password : ${e.message}")
                requestFailure.postValue(e)
            }
        }
    }

    /**
     * Deletes the user's account as well as all related hiker data and the profile's image
     */

    fun deleteUserAccount() {
        viewModelScope.launch(this.exceptionHandler) {
            try {
                clearQueryRegistration()
                launch { hikerDataRequester.deleteUserAccount() }.join()
                Log.d(TAG, "Successfully deleted the user account")
                saveRequestSuccess.postValue(true)
            }
            catch(e:Exception) {
                Log.e(TAG, "Failed to delete the user account : ${e.message}")
                requestFailure.postValue(e)
            }
        }
    }

    /**
     * Sends a message to a given interlocutor
     */

    fun sendMessage(interlocutorId: String, text: String) {
        viewModelScope.launch(this.exceptionHandler) {
            try {
                launch { hikerDataRequester.sendMessage(interlocutorId, text) }.join()
                Log.d(TAG, "Successfully sent the message")
            }
            catch (e:Exception) {
                Log.e(TAG, "Failed to send the message : ${e.message}")
                requestFailure.postValue(e)
            }
        }
    }

    /**
     * Deletes a message
     */

    fun deleteMessage(interlocutorId: String, message: Message) {
        viewModelScope.launch(this.exceptionHandler) {
            try {
                launch { hikerDataRequester.deleteMessage(interlocutorId, message) }.join()
                Log.d(TAG, "Successfully deleted the message")
            }
            catch (e:Exception) {
                Log.e(TAG, "Failed to delete the message : ${e.message}")
                requestFailure.postValue(e)
            }
        }
    }

    /**
     * Deletes the existing chat between the user and the given interlocutor
     */

    fun deleteChat(interlocutorId: String) {
        viewModelScope.launch(this.exceptionHandler) {
            try {
                launch { hikerDataRequester.deleteChat(interlocutorId) }.join()
                Log.d(TAG, "Successfully deleted the chat")
            }
            catch (e:Exception) {
                Log.e(TAG, "Failed to delete the chat : ${e.message}")
                requestFailure.postValue(e)
            }
        }
    }
}