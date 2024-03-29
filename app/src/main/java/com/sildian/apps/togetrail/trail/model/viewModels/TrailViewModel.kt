package com.sildian.apps.togetrail.trail.model.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sildian.apps.togetrail.common.baseViewModels.SingleDataViewModel
import com.sildian.apps.togetrail.common.utils.cloudHelpers.StorageRepository
import com.sildian.apps.togetrail.hiker.model.dataRepository.HikerRepository
import com.sildian.apps.togetrail.trail.model.core.Trail
import com.sildian.apps.togetrail.trail.model.core.TrailPointOfInterest
import com.sildian.apps.togetrail.trail.model.dataRepository.TrailRepository
import com.sildian.apps.togetrail.trail.model.dataRequests.*
import com.sildian.apps.togetrail.trail.model.support.TrailBuilder
import dagger.hilt.android.lifecycle.HiltViewModel
import io.ticofab.androidgpxparser.parser.domain.Gpx
import javax.inject.Inject

/*************************************************************************************************
 * This viewModel observes a single Trail related data
 ************************************************************************************************/

@HiltViewModel
class TrailViewModel @Inject constructor() : SingleDataViewModel<Trail>(Trail::class.java) {

    /***********************************Repositories*********************************************/

    @Inject lateinit var storageRepository: StorageRepository
    @Inject lateinit var hikerRepository: HikerRepository
    @Inject lateinit var trailRepository: TrailRepository

    /************************************Extra data*********************************************/

    private var imagePathToUpload: String? = null
    private var imagePathToDelete: String? = null
    private val mutableTrailPointOfInterest = MutableLiveData<TrailPointOfInterest?>()
    val trailPointOfInterest: LiveData<TrailPointOfInterest?> = mutableTrailPointOfInterest

    /**********************************Extra data monitoring*************************************/

    fun updateImagePathToUpload(isPOIImage: Boolean, imagePath: String) {
        if (isPOIImage) {
            this.trailPointOfInterest.value?.photoUrl?.let { photoUrl ->
                if (photoUrl.startsWith("https://")) {
                    this.imagePathToDelete = photoUrl
                }
            }
        } else {
            this.mutableData.value?.mainPhotoUrl?.let { photoUrl ->
                if (photoUrl.startsWith("https://")) {
                    this.imagePathToDelete = photoUrl
                }
            }
        }
        this.imagePathToUpload = imagePath
    }

    fun updateImagePathToDelete(imagePath: String) {
        this.imagePathToUpload = null
        if (imagePath.startsWith("https://")) {
            this.imagePathToDelete = imagePath
        }
    }

    fun clearImagePaths() {
        this.imagePathToUpload = null
        this.imagePathToDelete = null
    }

    fun watchPointOfInterest(position:Int) {
        this.mutableData.value?.trailTrack?.trailPointsOfInterest?.let { trailPoiList ->
            if (position <= trailPoiList.size - 1) {
                this.mutableTrailPointOfInterest.postValue(trailPoiList[position])
            }
        }
    }

    override fun notifyDataObserver() {
        super.notifyDataObserver()
        this.mutableTrailPointOfInterest.value = this.mutableTrailPointOfInterest.value
    }

    /************************************Data monitoring*****************************************/

    fun initNewTrail() {
        this.mutableData.postValue(
            TrailBuilder()
                .withDefault()
                .build()
        )
    }

    fun initNewTrail(gpx: Gpx) {
        this.mutableData.postValue(
            TrailBuilder()
                .withGpx(gpx)
                .build()
        )
    }

    fun initWithTrail(trail: Trail) {
        this.mutableData.postValue(trail)
    }

    fun loadTrailRealTime(trailId: String) {
        loadDataRealTime(this.trailRepository.getTrailReference(trailId))
    }

    fun loadTrail(trailId: String) {
        loadData(TrailLoadDataRequest(trailId, this.trailRepository))
    }

    fun saveTrail(savePOI: Boolean) {
        val dataRequest = TrailSaveDataRequest(
                this.mutableData.value,
                this.imagePathToDelete,
                this.imagePathToUpload,
                this.storageRepository,
                this.hikerRepository,
                this.trailRepository
            )
        if (savePOI) {
            dataRequest.editPOI(this.mutableTrailPointOfInterest.value)
        }
        saveData(dataRequest)
    }

    fun likeTrail() {
        runSpecificRequest(TrailLikeDataRequest(
            this.mutableData.value,
            this.trailRepository,
            this.hikerRepository
        ))
    }

    fun unlikeTrail() {
        runSpecificRequest(TrailUnlikeDataRequest(
            this.mutableData.value,
            this.trailRepository,
            this.hikerRepository
        ))
    }

    fun markTrail() {
        runSpecificRequest(TrailMarkDataRequest(this.mutableData.value, this.hikerRepository))
    }

    fun unmarkTrail() {
        runSpecificRequest(TrailUnmarkDataRequest(this.mutableData.value, this.hikerRepository))
    }
}