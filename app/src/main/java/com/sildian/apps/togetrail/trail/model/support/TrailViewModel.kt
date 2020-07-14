package com.sildian.apps.togetrail.trail.model.support

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.sildian.apps.togetrail.common.baseViewModels.BaseObservableViewModel
import com.sildian.apps.togetrail.common.utils.cloudHelpers.AuthRepository
import com.sildian.apps.togetrail.common.utils.cloudHelpers.StorageRepository
import com.sildian.apps.togetrail.hiker.model.core.HikerHistoryItem
import com.sildian.apps.togetrail.hiker.model.core.HikerHistoryType
import com.sildian.apps.togetrail.hiker.model.support.HikerRepository
import com.sildian.apps.togetrail.trail.model.core.Trail
import com.sildian.apps.togetrail.trail.model.core.TrailPointOfInterest
import io.ticofab.androidgpxparser.parser.domain.Gpx
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.*

/*************************************************************************************************
 * This viewModel observes a single Trail related data
 ************************************************************************************************/

class TrailViewModel:BaseObservableViewModel() {

    /************************************Static items********************************************/

    companion object{

        /**Logs**/
        private const val TAG = "TrailViewModel"

        /**Messages**/
        private const val EXCEPTION_MESSAGE_SAVE_NULL="Cannot save a null trail"
    }

    /***********************************Exception handler***************************************/

    private val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        Log.e(TAG, throwable.message.toString())
    }

    /***************************************Data*************************************************/

    var trail: Trail?=null ; private set                                    //The trail
    var trailPointOfInterest: TrailPointOfInterest?=null ; private  set     //The watched point of interest if needed
    private var imagePathToUpload:String?=null                              //Path of image to upload into the cloud
    private var imagePathToDelete:String?=null                              //Path of image to delete from the cloud

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
     * Initializes with an existing trail
     * @param trail : the trail
     */

    fun initWithTrail(trail:Trail){
        this.trail=trail
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
                    Log.d(TAG, "Successfully loaded trail from database")
                    successCallback?.invoke()
                }
                else if(e!=null){
                    Log.e(TAG, "Failed to load trail from database : ${e.message}")
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
        viewModelScope.launch(this.exceptionHandler) {
            try {
                val deferredTrail = async { TrailRepository.getTrail(trailId) }
                trail = deferredTrail.await()
                notifyDataChanged()
                Log.d(TAG, "Successfully loaded trail from database")
                successCallback?.invoke()
            }
            catch(e:Exception){
                Log.e(TAG, "Failed to load trail from database : ${e.message}")
                failureCallback?.invoke(e)
            }
        }
    }

    /**
     * Saves the trail within the database
     * @param savePOI : true if a POI needs to be saved
     * @param successCallback : the callback to handle a success in the query
     * @param failureCallback : the callback to handle a failure in the query
     */

    fun saveTrailInDatabase(savePOI:Boolean, successCallback:(()->Unit)?=null, failureCallback:((Exception)->Unit)?=null){
        viewModelScope.launch(this.exceptionHandler) {
            try {
                if(trail!=null){

                    trail?.lastUpdate= Date()

                    /*Stores or deletes the image in the cloud if necessary*/

                    imagePathToDelete?.let { url ->
                        launch { StorageRepository.deleteImage(url) }.join()
                    }
                    imagePathToUpload?.let { uri ->
                        val deferredNewImageUrl=async { StorageRepository.uploadImage(uri) }
                        val newImageUrl=deferredNewImageUrl.await()
                        if(savePOI){
                            trailPointOfInterest?.photoUrl=newImageUrl
                        }else {
                            trail?.mainPhotoUrl = newImageUrl
                        }
                    }

                    /*If the trail is new...*/

                    if(trail?.id==null){

                        /*Creates the trail*/

                        trail?.authorId = AuthRepository.getCurrentUser()?.uid
                        val deferredTrailId=async { TrailRepository.addTrail(trail!!) }
                        trail?.id=deferredTrailId.await()
                        launch { TrailRepository.updateTrail(trail!!) }.join()

                        /*Updates the author's profile*/

                        AuthRepository.getCurrentUserProfile()?.let { hiker ->
                            hiker.nbTrailsCreated++
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
                        Log.d(TAG, "Successfully saved trail in database")
                        successCallback?.invoke()
                    }
                    else{

                        /*If the trail is not new, just updates it*/

                        launch { TrailRepository.updateTrail(trail!!) }.join()
                        Log.d(TAG, "Successfully saved trail in database")
                        successCallback?.invoke()
                    }
                }
                else{
                    Log.e(TAG, "Failed to save trail in database : $EXCEPTION_MESSAGE_SAVE_NULL")
                    failureCallback?.invoke(NullPointerException(EXCEPTION_MESSAGE_SAVE_NULL))
                }
            }
            catch(e:Exception){
                Log.e(TAG, "Failed to save trail in database : ${e.message}")
                failureCallback?.invoke(e)
                throw e
            }
        }
    }

    /**
     * Watches a point of interest
     * @param position : the poi's position
     */

    fun watchPointOfInterest(position:Int) {
        if(position<=this.trail?.trailTrack?.trailPointsOfInterest!!.size-1) {
            this.trailPointOfInterest = this.trail?.trailTrack?.trailPointsOfInterest!![position]
            notifyDataChanged()
        }
    }

    /**
     * Gives an image to be stored on the cloud
     * @param isPOIImage : true if the image to upload is related to a POI
     * @param imagePath : the temporary image's uri
     */

    fun updateImagePathToUpload(isPOIImage:Boolean, imagePath:String){
        if(isPOIImage){
            this.trailPointOfInterest?.photoUrl?.let { photoUrl ->
                if (photoUrl.startsWith("https://")) {
                    this.imagePathToDelete = photoUrl
                }
            }
        }else {
            this.trail?.mainPhotoUrl?.let { photoUrl ->
                if (photoUrl.startsWith("https://")) {
                    this.imagePathToDelete = photoUrl
                }
            }
        }
        this.imagePathToUpload = imagePath
    }

    /**
     * Gives an image to be deleted from the cloud
     * @param imagePath : the image's url
     */

    fun updateImagePathToDelete(imagePath:String){
        this.imagePathToUpload=null
        if(imagePath.startsWith("https://")){
            this.imagePathToDelete=imagePath
        }
    }

    /**Clears the image paths**/

    fun clearImagePaths() {
        this.imagePathToUpload = null
        this.imagePathToDelete = null
    }
}