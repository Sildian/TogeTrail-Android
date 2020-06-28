package com.sildian.apps.togetrail.trail.model.support

import androidx.lifecycle.viewModelScope
import com.sildian.apps.togetrail.common.baseViewModels.BaseObservableViewModel
import com.sildian.apps.togetrail.common.utils.cloudHelpers.AuthFirebaseHelper
import com.sildian.apps.togetrail.hiker.model.core.HikerHistoryItem
import com.sildian.apps.togetrail.hiker.model.core.HikerHistoryType
import com.sildian.apps.togetrail.hiker.model.support.HikerRepository
import com.sildian.apps.togetrail.trail.model.core.Trail
import com.sildian.apps.togetrail.trail.model.core.TrailPointOfInterest
import io.ticofab.androidgpxparser.parser.domain.Gpx
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.*

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

    var trail: Trail?=null ; private set                                    //The trail
    var trailPointOfInterest: TrailPointOfInterest?=null ; private  set     //The watched point of interest if needed

    /************************************Data monitoring*****************************************/

    /**Initializes a new Trail with default data**/

    fun initNewTrail(){
        this.trail= TrailBuilder
            .withDefault()
            .build()
        notifyDataChanged()
    }

    /**
     * Initializes a new Trail with gpx data
     * @param gpx : the gpx data
     */

    fun initNewTrail(gpx:Gpx){
        this.trail= TrailBuilder
            .withGpx(gpx)
            .build()
        notifyDataChanged()
    }

    /**
     * Loads a trail from the database in real time
     * @param trailId : the trail's id
     * @param successCallback : the callback to handle a success in the query
     * @param failureCallback : the callback to handle a failure in the query
     */

    fun loadTrailFromDatabaseRealTime(trailId:String, successCallback:(()->Unit)?=null, failureCallback:((Exception)->Unit)?=null) {
        this.queryRegistration?.remove()
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

    /**
     * Saves the trail within the database
     * @param successCallback : the callback to handle a success in the query
     * @param failureCallback : the callback to handle a failure in the query
     */

    fun saveTrailInDatabase(successCallback:(()->Unit)?=null, failureCallback:((Exception)->Unit)?=null){
        viewModelScope.launch {
            try {
                if(trail!=null){

                    trail?.lastUpdate= Date()

                    /*If the trail is new...*/

                    if(trail?.id==null){

                        /*Creates the trail*/

                        trail?.authorId = AuthFirebaseHelper.getCurrentUser()?.uid
                        val deferredTrailId=async { TrailRepository.addTrail(trail!!) }
                        trail?.id=deferredTrailId.await()
                        launch { TrailRepository.updateTrail(trail!!) }.join()

                        /*Updates the author's profile*/

                        trail?.authorId?.let { authorId ->
                            val deferredHiker = async { HikerRepository.getHiker(authorId) }
                            val hiker = deferredHiker.await()
                            hiker!!.nbTrailsCreated++
                            launch { HikerRepository.updateHiker(hiker) }.join()

                            /*And creates an history item*/

                            val historyItem = HikerHistoryItem(
                                HikerHistoryType.TRAIL_CREATED,
                                trail!!.creationDate,
                                trail!!.id!!,
                                trail!!.name!!,
                                trail!!.location.toString(),
                                trail!!.mainPhotoUrl
                            )
                            launch { HikerRepository.addHikerHistoryItem(hiker.id, historyItem) }.join()
                        }
                        successCallback?.invoke()
                    }
                    else{
                        launch { TrailRepository.updateTrail(trail!!) }.join()
                        successCallback?.invoke()
                    }
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
     * Watches a point of interest
     * @param position : the poi's position
     */

    fun watchPointOfInterest(position:Int) {
        this.trailPointOfInterest=this.trail?.trailTrack?.trailPointsOfInterest!![position]
        notifyDataChanged()
    }
}