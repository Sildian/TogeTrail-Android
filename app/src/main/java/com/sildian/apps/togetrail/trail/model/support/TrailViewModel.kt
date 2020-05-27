package com.sildian.apps.togetrail.trail.model.support

import androidx.lifecycle.viewModelScope
import com.sildian.apps.togetrail.common.baseViewModels.BaseObservableViewModel
import com.sildian.apps.togetrail.trail.model.core.Trail
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

/*************************************************************************************************
 * This viewModel observes a single Trail related data
 ************************************************************************************************/

class TrailViewModel:BaseObservableViewModel() {

    /************************************Static items********************************************/

    companion object{

        /**Messages**/
        private const val EXCEPTION_MESSAGE_SAVE_NULL="Cannot save a null trail"
    }

    /***************************************Data*************************************************/

    var trail: Trail?=null                              //The trail

    /************************************Data monitoring*****************************************/

    /**
     * Loads a trail from the database in real time
     * @param trailId : the trail's id
     * @param successCallback : the callback to handle a success in the query
     * @param failureCallback : the callback to handle a failure in the query
     */

    fun loadTrailFromDatabaseRealTime(trailId:String, successCallback:(()->Unit)?=null, failureCallback:((Exception)->Unit)?=null) {
        this.queryRegistration = TrailRepository.getTrailReference(trailId)
            .addSnapshotListener { snapshot, e ->
                if (snapshot != null) {
                    trail = snapshot.toObject(Trail::class.java)
                    notifyDataChanged()
                    successCallback?.invoke()
                }
                else if(e!=null){
                    failureCallback?.invoke(e)
                }
            }
    }

    /**
     * Loads a trail from the database
     * @param trailId : the trail's id
     * @param successCallback : the callback to handle a success in the query
     * @param failureCallback : the callback to handle a failure in the query
     */

    fun loadTrailFromDatabase(trailId:String, successCallback:(()->Unit)?=null, failureCallback:((Exception)->Unit)?=null){
        viewModelScope.launch {
            try {
                val deferredTrail = async { TrailRepository.getTrail(trailId) }
                trail = deferredTrail.await()
                notifyDataChanged()
                successCallback?.invoke()
            }
            catch(e:Exception){
                failureCallback?.invoke(e)
            }
        }
    }
}