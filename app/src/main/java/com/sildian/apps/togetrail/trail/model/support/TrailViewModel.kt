package com.sildian.apps.togetrail.trail.model.support

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.sildian.apps.togetrail.common.baseViewModels.BaseObservableViewModel
import com.sildian.apps.togetrail.trail.model.core.Trail
import com.sildian.apps.togetrail.trail.model.core.TrailPointOfInterest
import io.ticofab.androidgpxparser.parser.domain.Gpx
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

/*************************************************************************************************
 * This viewModel observes a single Trail related data
 ************************************************************************************************/

class TrailViewModel:BaseObservableViewModel() {

    /************************************Static items********************************************/

    companion object{

        /**Logs**/
        private const val TAG = "TrailViewModel"
    }

    /***********************************Exception handler***************************************/

    private val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        Log.e(TAG, throwable.message.toString())
    }

    /***********************************Data requester******************************************/

    private val trailDataRequester = TrailDataRequester()

    /***************************************Data*************************************************/

    var trail: Trail?=null ; private set                                    //The trail
    var trailPointOfInterest: TrailPointOfInterest?=null ; private  set     //The watched point of interest if needed
    private var imagePathToUpload:String?=null                              //Path of image to upload into the cloud
    private var imagePathToDelete:String?=null                              //Path of image to delete from the cloud

    /************************************Data monitoring*****************************************/

    /**Initializes a new Trail with default data**/

    fun initNewTrail() {
        this.trail= TrailBuilder()
            .withDefault()
            .build()
        notifyDataChanged()
    }

    /**
     * Initializes a new Trail with gpx data
     * @param gpx : the gpx data
     */

    fun initNewTrail(gpx:Gpx) {
        this.trail= TrailBuilder()
            .withGpx(gpx)
            .build()
        notifyDataChanged()
    }

    /**
     * Initializes with an existing trail
     * @param trail : the trail
     */

    fun initWithTrail(trail:Trail) {
        this.trail=trail
        notifyDataChanged()
    }

    /**
     * Watches a point of interest
     * It means that the observed data focus on the POI related to the given position
     * @param position : the poi's position
     */

    fun watchPointOfInterest(position:Int) {
        this.trail?.trailTrack?.trailPointsOfInterest?.let { trailPoiList ->
            if(position <= trailPoiList.size-1) {
                this.trailPointOfInterest = trailPoiList[position]
                notifyDataChanged()
            }
        }
    }

    /**
     * Gives an image to be stored on the cloud
     * If the trail or the POI already has an image, it will be deleted from the storage
     * @param isPOIImage : true if the image to upload is related to a POI, otherwise it is related to the trail itself
     * @param imagePath : the temporary image's uri
     */

    fun updateImagePathToUpload(isPOIImage:Boolean, imagePath:String) {
        if(isPOIImage) {
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

    /**
     * Loads a trail from the database in real time
     * @param trailId : the trail's id
     * @param successCallback : the callback to handle a success in the query
     * @param failureCallback : the callback to handle a failure in the query
     */

    fun loadTrailFromDatabaseRealTime(trailId:String, successCallback:(()->Unit)?=null, failureCallback:((Exception)->Unit)?=null) {
        this.queryRegistration?.remove()
        this.queryRegistration = trailDataRequester.loadTrailFromDatabaseRealTime(trailId)
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
                trail = async { trailDataRequester.loadTrailFromDatabase(trailId) }.await()
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
                if (savePOI) {
                    launch {
                        trailDataRequester.saveTrailInDatabase(
                            trail, trailPointOfInterest, imagePathToDelete, imagePathToUpload
                        )
                    }.join()
                }
                else {
                    launch {
                        trailDataRequester.saveTrailInDatabase(trail, imagePathToDelete, imagePathToUpload)
                    }.join()
                }
                Log.d(TAG, "Successfully saved trail in database")
                successCallback?.invoke()
            }
            catch(e:Exception){
                Log.e(TAG, "Failed to save trail in database : ${e.message}")
                failureCallback?.invoke(e)
                throw e
            }
        }
    }
}