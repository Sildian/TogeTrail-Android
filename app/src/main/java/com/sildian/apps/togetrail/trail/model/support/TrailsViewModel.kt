package com.sildian.apps.togetrail.trail.model.support

import android.util.Log
import com.google.firebase.firestore.Query
import com.sildian.apps.togetrail.common.baseViewModels.BaseViewModel
import com.sildian.apps.togetrail.trail.model.core.Trail

/*************************************************************************************************
 * This viewModel observes a list of trails
 ************************************************************************************************/

class TrailsViewModel:BaseViewModel() {

    /************************************Static items********************************************/

    companion object {

        /**Logs**/
        private const val TAG = "TrailsViewModel"
    }

    /***************************************Data*************************************************/

    val trails= arrayListOf<Trail>()                    //The list of trails

    /************************************Data monitoring*****************************************/

    /**
     * Loads a list of trails from the database in real time
     * @param query : the query to fetch trails (select a query in TrailFirebaseQuery)
     * @param successCallback : the callback to handle a success in the query
     * @param failureCallback : the callback to handle a failure in the query
     */

    fun loadTrailsFromDatabaseRealTime(query:Query, successCallback:(()->Unit)?=null, failureCallback:((Exception)->Unit)?=null){
        this.queryRegistration?.remove()
        this.queryRegistration = query
            .addSnapshotListener { querySnapshot, e ->
                if (querySnapshot != null) {
                    trails.clear()
                    trails.addAll(querySnapshot.toObjects(Trail::class.java))
                    notifyDataChanged()
                    Log.d(TAG, "Successfully loaded ${trails.size} trails from database")
                    successCallback?.invoke()
                }
                else if(e!=null){
                    Log.e(TAG, "Failed to load trails from database : ${e.message}")
                    failureCallback?.invoke(e)
                }
            }
    }
}