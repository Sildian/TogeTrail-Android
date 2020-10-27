package com.sildian.apps.togetrail.trail.model.support

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sildian.apps.togetrail.common.baseViewModels.BaseViewModel
import com.sildian.apps.togetrail.trail.model.core.Trail
import com.sildian.apps.togetrail.trail.model.core.TrailPointOfInterest
import io.ticofab.androidgpxparser.parser.domain.Gpx
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

/*************************************************************************************************
 * This viewModel observes a single Trail related data
 ************************************************************************************************/

class TrailViewModel : BaseViewModel() {

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

    /*********************************Monitoring info*******************************************/

    private var imagePathToUpload:String? = null
    private var imagePathToDelete:String? = null

    /********************************LiveData to be observed*************************************/

    val trail = MutableLiveData<Trail?>()
    val trailPointOfInterest = MutableLiveData<TrailPointOfInterest?>()
    val saveRequestSuccess = MutableLiveData<Boolean>()
    val requestFailure = MutableLiveData<Exception?>()

    /************************************Data monitoring*****************************************/

    /**Initializes a new Trail with default data**/

    fun initNewTrail() {
        this.trail.postValue(
            TrailBuilder()
                .withDefault()
                .build()
        )
    }

    /**
     * Initializes a new Trail with gpx data
     * @param gpx : the gpx data
     */

    fun initNewTrail(gpx: Gpx) {
        this.trail.postValue(
            TrailBuilder()
                .withGpx(gpx)
                .build()
        )
    }

    /**
     * Initializes with an existing trail
     * @param trail : the trail
     */

    fun initWithTrail(trail: Trail) {
        this.trail.postValue(trail)
    }

    /**
     * Watches a point of interest
     * It means that the observed data focus on the POI related to the given position
     * @param position : the poi's position
     */

    fun watchPointOfInterest(position:Int) {
        this.trail.value?.trailTrack?.trailPointsOfInterest?.let { trailPoiList ->
            if(position <= trailPoiList.size-1) {
                this.trailPointOfInterest.postValue(trailPoiList[position])
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
            this.trailPointOfInterest.value?.photoUrl?.let { photoUrl ->
                if (photoUrl.startsWith("https://")) {
                    this.imagePathToDelete = photoUrl
                }
            }
        } else {
            this.trail.value?.mainPhotoUrl?.let { photoUrl ->
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
            this.imagePathToDelete = imagePath
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
     */

    fun loadTrailFromDatabaseRealTime(trailId:String) {
        this.queryRegistration?.remove()
        this.queryRegistration = trailDataRequester.loadTrailFromDatabaseRealTime(trailId)
            .addSnapshotListener { snapshot, e ->
                if (snapshot != null) {
                    val result = snapshot.toObject(Trail::class.java)
                    Log.d(TAG, "Successfully loaded trail from database")
                    trail.postValue(result)
                }
                else if(e != null){
                    Log.e(TAG, "Failed to load trail from database : ${e.message}")
                    requestFailure.postValue(e)
                }
            }
    }

    /**
     * Loads a trail from the database
     * @param trailId : the trail's id
     */

    fun loadTrailFromDatabase(trailId:String){
        viewModelScope.launch(this.exceptionHandler) {
            try {
                val result = async { trailDataRequester.loadTrailFromDatabase(trailId) }.await()
                Log.d(TAG, "Successfully loaded trail from database")
                trail.postValue(result)
            }
            catch(e:Exception){
                Log.e(TAG, "Failed to load trail from database : ${e.message}")
                requestFailure.postValue(e)
            }
        }
    }

    /**
     * Saves the trail within the database
     * @param savePOI : true if a POI needs to be saved
     */

    fun saveTrailInDatabase(savePOI:Boolean){
        viewModelScope.launch(this.exceptionHandler) {
            try {
                if (savePOI) {
                    launch {
                        trailDataRequester.saveTrailInDatabase(
                            trail.value, trailPointOfInterest.value, imagePathToDelete, imagePathToUpload
                        )
                    }.join()
                }
                else {
                    launch {
                        trailDataRequester.saveTrailInDatabase(trail.value, imagePathToDelete, imagePathToUpload)
                    }.join()
                }
                Log.d(TAG, "Successfully saved trail in database")
                saveRequestSuccess.postValue(true)
            }
            catch(e:Exception) {
                Log.e(TAG, "Failed to save trail in database : ${e.message}")
                requestFailure.postValue(e)
            }
        }
    }
}