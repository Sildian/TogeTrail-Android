package com.sildian.apps.togetrail.hiker.model.support

import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestoreException
import com.sildian.apps.togetrail.common.viewModels.BaseObservableViewModel
import com.sildian.apps.togetrail.hiker.model.core.Hiker
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

/*************************************************************************************************
 * This viewModel observes Hiker related data
 ************************************************************************************************/

class HikerViewModel : BaseObservableViewModel() {

    /***************************************Data*************************************************/

    var hiker: Hiker?=null

    /************************************Data monitoring*****************************************/

    /**
     * Loads an hiker from the database in real time
     * @param hikerId : the hiker's id
     * @param failureCallback : the callback to handle a failure in the query
     */

    fun loadHikerFromDatabaseRealTime(hikerId:String, failureCallback:((Exception)->Unit)?=null) {
        this.queryRegistration = HikerRepository.getHikerReference(hikerId)
            .addSnapshotListener { snapshot, e ->
                if (snapshot != null) {
                    hiker = snapshot.toObject(Hiker::class.java)
                    notifyDataChanged()
                }
                else if(e!=null){
                    failureCallback?.invoke(e)
                }
            }
    }

    /**
     * Loads an hiker from the database
     * @param hikerId : the hiker's id
     * @param failureCallback : the callback to handle a failure in the query
     */

    fun loadHikerFromDatabase(hikerId:String, failureCallback:((Exception)->Unit)?=null){
        viewModelScope.launch {
            try {
                val deferredHiker = async { HikerRepository.getHiker(hikerId) }
                hiker = deferredHiker.await()
                notifyDataChanged()
            }
            catch(e:Exception){
                failureCallback?.invoke(e)
            }
        }
    }
}