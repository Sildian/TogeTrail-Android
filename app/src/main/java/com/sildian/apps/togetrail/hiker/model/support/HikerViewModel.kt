package com.sildian.apps.togetrail.hiker.model.support

import androidx.lifecycle.viewModelScope
import com.sildian.apps.togetrail.common.baseViewModels.BaseObservableViewModel
import com.sildian.apps.togetrail.common.utils.cloudHelpers.AuthFirebaseHelper
import com.sildian.apps.togetrail.hiker.model.core.Hiker
import com.sildian.apps.togetrail.hiker.model.core.HikerHistoryItem
import com.sildian.apps.togetrail.hiker.model.core.HikerHistoryType
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

/*************************************************************************************************
 * This viewModel observes a single Hiker related data
 ************************************************************************************************/

class HikerViewModel : BaseObservableViewModel() {

    /************************************Static items********************************************/

    companion object{

        /**Messages**/
        private const val EXCEPTION_MESSAGE_SAVE_NULL="Cannot save a null hiker"
    }

    /***************************************Data*************************************************/

    var hiker: Hiker?=null ; private set                        //The hiker

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
                    successCallback?.invoke()
                }
                else if(e!=null){
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
        viewModelScope.launch {
            try {
                val deferredHiker = async { HikerRepository.getHiker(hikerId) }
                hiker = deferredHiker.await()
                notifyDataChanged()
                successCallback?.invoke()
            }
            catch(e:Exception){
                failureCallback?.invoke(e)
            }
        }
    }

    /**
     * Saves the hiker within the database
     * @param successCallback : the callback to handle a success in the query
     * @param failureCallback : the callback to handle a failure in the query
     */

    fun saveHikerInDatabase(successCallback:(()->Unit)?=null, failureCallback:((Exception)->Unit)?=null){
        viewModelScope.launch {
            try{
                if(hiker!=null){
                    launch { HikerRepository.updateHiker(hiker!!) }.join()
                    successCallback?.invoke()
                }
                else{
                    failureCallback?.invoke(NullPointerException(EXCEPTION_MESSAGE_SAVE_NULL))
                }
            }
            catch(e:Exception){
                failureCallback?.invoke(e)
            }
        }
    }

    /**
     * Saves an item within the hiker's history
     * @param historyItem : the history item
     * @param successCallback : the callback to handle a success in the query
     * @param failureCallback : the callback to handle a failure in the query
     */

    fun saveHikerHistoryItem(historyItem:HikerHistoryItem, successCallback:(()->Unit)?=null, failureCallback:((Exception)->Unit)?=null){
        viewModelScope.launch {
            try{
                if(hiker!=null){
                    launch { HikerRepository.addHikerHistoryItem(hiker!!.id, historyItem) }.join()
                    successCallback?.invoke()
                }
                else{
                    failureCallback?.invoke(NullPointerException(EXCEPTION_MESSAGE_SAVE_NULL))
                }
            }
            catch(e:Exception){
                failureCallback?.invoke(e)
            }
        }
    }

    /**
     * Logs the user in and if this is a new user, creates a new Hiker
     * @param successCallback : the callback to handle a success in the query
     * @param failureCallback : the callback to handle a failure in the query
     */

    fun loginUser(successCallback:(()->Unit)?=null, failureCallback:((Exception)->Unit)?=null){
        viewModelScope.launch {
            try {

                /*Gets the current user and the related Hiker info*/

                val user = AuthFirebaseHelper.getCurrentUser()
                user?.let { usr ->
                    val deferredHiker = async { HikerRepository.getHiker(usr.uid) }
                    hiker = deferredHiker.await()

                    /*If the hiker is null, then creates a new Hiker in the database*/

                    if (hiker == null) {
                        hiker = HikerBuilder
                            .withFirebaseUser(usr)
                            .build()
                        launch { HikerRepository.updateHiker(hiker!!) }.join()
                        val historyItem = HikerHistoryItem(
                            HikerHistoryType.HIKER_REGISTERED,
                            hiker?.registrationDate!!
                        )
                        launch {
                            HikerRepository.addHikerHistoryItem(
                                hiker!!.id,
                                historyItem
                            )
                        }.join()
                    }
                }

                /*Then notifies the callbacks*/

                notifyDataChanged()
                successCallback?.invoke()
            }
            catch(e:Exception){
                failureCallback?.invoke(e)
            }
        }
    }

    /**Logs the user out**/

    fun logoutUser(){
        this.hiker=null
        AuthFirebaseHelper.signUserOut()
        notifyDataChanged()
    }
}