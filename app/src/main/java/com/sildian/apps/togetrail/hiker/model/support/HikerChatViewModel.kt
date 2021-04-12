package com.sildian.apps.togetrail.hiker.model.support

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.sildian.apps.togetrail.chat.model.core.Duo
import com.sildian.apps.togetrail.common.baseViewModels.BaseViewModel

/*************************************************************************************************
 * This viewModel observes chat data related to a hiker
 ************************************************************************************************/

class HikerChatViewModel: BaseViewModel() {

    /************************************Static items********************************************/

    companion object {

        /**Logs**/
        private const val TAG = "HikerChatViewModel"
    }

    /***************************************Data*************************************************/

    val chats = MutableLiveData<List<Duo>>()
    val requestFailure = MutableLiveData<Exception?>()

    /************************************Data monitoring*****************************************/

    /**
     * Loads a list of chats from the database in real time
     * @param hikerId : the if of the owner hiker
     */

    fun loadChatsFromDatabaseRealTime(hikerId: String) {
        this.queryRegistration?.remove()
        this.queryRegistration = HikerFirebaseQueries.getChats(hikerId)
            .addSnapshotListener { querySnapshot, e ->
                if (querySnapshot != null) {
                    val results = querySnapshot.toObjects(Duo::class.java)
                    Log.d(TAG, "Successfully loaded ${results.size} chats from database")
                    chats.postValue(results)
                }
                else if (e != null) {
                    Log.e(TAG, "Failed to load chats from database : ${e.message}")
                    requestFailure.postValue(e)
                }
            }
    }
}