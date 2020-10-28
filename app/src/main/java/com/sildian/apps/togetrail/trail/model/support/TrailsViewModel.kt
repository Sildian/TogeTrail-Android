package com.sildian.apps.togetrail.trail.model.support

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.Query
import com.sildian.apps.togetrail.common.baseViewModels.BaseViewModel
import com.sildian.apps.togetrail.trail.model.core.Trail

/*************************************************************************************************
 * This viewModel observes a list of trails
 ************************************************************************************************/

class TrailsViewModel: BaseViewModel() {

    /************************************Static items********************************************/

    companion object {

        /**Logs**/
        private const val TAG = "TrailsViewModel"
    }

    /***************************************Data*************************************************/

    val trails = MutableLiveData<List<Trail>>()
    val requestFailure = MutableLiveData<Exception?>()

    /************************************Data monitoring*****************************************/

    /**
     * Loads a list of trails from the database in real time
     * @param query : the query to fetch trails (select a query in TrailFirebaseQuery)
     */

    fun loadTrailsFromDatabaseRealTime(query: Query) {
        this.queryRegistration?.remove()
        this.queryRegistration = query
            .addSnapshotListener { querySnapshot, e ->
                if (querySnapshot != null) {
                    val results = querySnapshot.toObjects(Trail::class.java)
                    Log.d(TAG, "Successfully loaded ${results.size} trails from database")
                    trails.postValue(results)
                }
                else if (e != null) {
                    Log.e(TAG, "Failed to load trails from database : ${e.message}")
                    requestFailure.postValue(e)
                }
            }
    }
}