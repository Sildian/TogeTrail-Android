package com.sildian.apps.togetrail.hiker.model.support

import androidx.lifecycle.viewModelScope
import com.sildian.apps.togetrail.common.baseViewModels.BaseObservableViewModel
import com.sildian.apps.togetrail.hiker.model.core.Hiker
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

/*************************************************************************************************
 * This viewModel observes a single Hiker related data
 ************************************************************************************************/

class HikerViewModel : BaseObservableViewModel() {

    /************************************Static items********************************************/

    companion object{

        /**Messages**/
        private const val NULL_HIKER_UPDATE_MESSAGE="Cannot update a null hiker"
    }

    /***************************************Data*************************************************/

    var hiker: Hiker?=null

    /************************************Data monitoring*****************************************/

    /**
     * Loads an hiker from the database in real time
     * @param hikerId : the hiker's id
     * @param successCallback : the callback to handle a success in the query
     * @param failureCallback : the callback to handle a failure in the query
     */

    fun loadHikerFromDatabaseRealTime(hikerId:String, successCallback:(()->Unit)?=null, failureCallback:((Exception)->Unit)?=null) {
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
                    failureCallback?.invoke(NullPointerException(NULL_HIKER_UPDATE_MESSAGE))
                }
            }
            catch(e:Exception){
                failureCallback?.invoke(e)
            }
        }
    }
}