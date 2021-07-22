package com.sildian.apps.togetrail.trail.model.support

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.ListenerRegistration
import com.sildian.apps.togetrail.common.baseViewModels.BaseViewModel
import com.sildian.apps.togetrail.hiker.model.support.HikerFirebaseQueries
import com.sildian.apps.togetrail.trail.model.core.Trail

/*************************************************************************************************
 * This viewModel observes lists of trails related to the given hiker
 ************************************************************************************************/

class HikerTrailsViewModel: BaseViewModel() {

    /************************************Static items********************************************/

    companion object {

        /**Logs**/
        private const val TAG = "HikerTrailsViewModel"
    }

    /********************************Query registration******************************************/

    private var likedTrailsQueryRegistration: ListenerRegistration? = null
    private var markedTrailsQueryRegistration: ListenerRegistration? = null

    /***************************************Data*************************************************/

    val likedTrails = MutableLiveData<List<Trail>>()
    val markedTrails = MutableLiveData<List<Trail>>()
    val requestFailure = MutableLiveData<Exception?>()

    /**************************Query registration monitoring*************************************/

    override fun clearQueryRegistration() {
        super.clearQueryRegistration()
        this.likedTrailsQueryRegistration?.remove()
        this.likedTrailsQueryRegistration = null
        this.markedTrailsQueryRegistration?.remove()
        this.markedTrailsQueryRegistration = null
    }

    override fun isQueryRegistrationBusy(): Boolean =
        super.isQueryRegistrationBusy()
                || this.likedTrailsQueryRegistration != null
                || this.markedTrailsQueryRegistration != null

    /************************************Data monitoring*****************************************/

    /**
     * Loads the list of hiker's liked trails from the database in real time
     * @param hikerId : the hiker's id
     */

    fun loadLikedTrailsFromDatabaseRealTime(hikerId: String) {
        this.likedTrailsQueryRegistration?.remove()
        this.likedTrailsQueryRegistration = HikerFirebaseQueries.getLikedTrails(hikerId)
            .addSnapshotListener { querySnapshot, e ->
                if (querySnapshot != null) {
                    val results = querySnapshot.toObjects(Trail::class.java)
                    Log.d(TAG, "Successfully loaded ${results.size} liked trails from database")
                    likedTrails.postValue(results)
                }
                else if (e != null) {
                    Log.e(TAG, "Failed to load liked trails from database : ${e.message}")
                    requestFailure.postValue(e)
                }
            }
    }

    /**
     * Loads the list of hiker's marked trails from the database in real time
     * @param hikerId : the hiker's id
     */

    fun loadMarkedTrailsFromDatabaseRealTime(hikerId: String) {
        this.markedTrailsQueryRegistration?.remove()
        this.markedTrailsQueryRegistration = HikerFirebaseQueries.getMarkedTrails(hikerId)
            .addSnapshotListener { querySnapshot, e ->
                if (querySnapshot != null) {
                    val results = querySnapshot.toObjects(Trail::class.java)
                    Log.d(TAG, "Successfully loaded ${results.size} marked trails from database")
                    markedTrails.postValue(results)
                }
                else if (e != null) {
                    Log.e(TAG, "Failed to load marked trails from database : ${e.message}")
                    requestFailure.postValue(e)
                }
            }
    }
}