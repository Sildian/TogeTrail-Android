package com.sildian.apps.togetrail.trail.data.dataRequests

import android.util.Log
import com.sildian.apps.togetrail.common.baseDataRequests.SaveDataRequest
import com.sildian.apps.togetrail.common.utils.cloudHelpers.StorageRepository
import com.sildian.apps.togetrail.hiker.data.models.HikerHistoryItem
import com.sildian.apps.togetrail.hiker.data.models.HikerHistoryType
import com.sildian.apps.togetrail.hiker.data.helpers.CurrentHikerInfo
import com.sildian.apps.togetrail.hiker.data.source.HikerRepository
import com.sildian.apps.togetrail.trail.data.models.Trail
import com.sildian.apps.togetrail.trail.data.models.TrailPointOfInterest
import com.sildian.apps.togetrail.trail.data.source.TrailRepository
import kotlinx.coroutines.CoroutineDispatcher
import java.util.*

/*************************************************************************************************
 * Saves a trail within the database
 ************************************************************************************************/

class TrailSaveDataRequest(
    dispatcher: CoroutineDispatcher,
    trail: Trail?,
    private val imagePathToDelete: String?,
    private val imagePathToUpload: String?,
    private val storageRepository: StorageRepository,
    private val hikerRepository: HikerRepository,
    private val trailRepository: TrailRepository
)
    : SaveDataRequest<Trail>(dispatcher, trail) {

    companion object {
        private const val TAG = "TrailSaveDataRequest"
    }

    private var trailPOI: TrailPointOfInterest? = null

    fun editPOI(poi: TrailPointOfInterest?): TrailSaveDataRequest {
        poi?.let {
            if (this.data?.trailTrack?.trailPointsOfInterest?.contains(poi) == true) {
                this.trailPOI = poi
            }
        }
        return this
    }

    override suspend fun save() {
        CurrentHikerInfo.currentHiker?.let {
            this.data?.let { trail ->
                deleteImageFromStorage()
                uploadImageToStorage()
                val isNewTrail = trail.id == null
                saveTrail()
                if (isNewTrail) {
                    updateCurrentHikerHistoryWithCreatedTrail()
                }
            }?:
            throw NullPointerException("Cannot perform the requested operation with a null trail")
        }?:
        throw NullPointerException("Cannot perform the requested operation while the current hiker is null")
    }
    
    private suspend fun deleteImageFromStorage() {
        this.imagePathToDelete?.let { url ->
            try {
                this.storageRepository.deleteImage(url)
            } catch (e: Exception) {
                Log.w(TAG, "Failed to delete photo at url $url : ${e.message}")
            }
        }
    }

    private suspend fun uploadImageToStorage() {
        this.imagePathToUpload?.let { uri ->
            try {
                val newImageUrl = storageRepository.uploadImage(uri)
                if (trailPOI != null) {
                    trailPOI?.photoUrl = newImageUrl
                } else {
                    data?.mainPhotoUrl = newImageUrl
                }
            } catch (e: Exception) {
                Log.w(TAG, "Failed to upload photo from uri $uri : ${e.message}")
            }
        }
    }

    private suspend fun saveTrail() {
        CurrentHikerInfo.currentHiker?.let { hiker ->
            this.data?.let { trail ->
                trail.lastUpdate = Date()
                trail.authorName = hiker.name
                trail.authorPhotoUrl = hiker.photoUrl
                if (trail.id == null) {
                    trail.authorId = hiker.id
                    trail.id = trailRepository.addTrail(trail)
                }
                this.trailRepository.updateTrail(trail)
            }
        }
    }

    private suspend fun updateCurrentHikerHistoryWithCreatedTrail() {
        CurrentHikerInfo.currentHiker?.let { hiker ->
            this.data?.let { trail ->
                hiker.nbTrailsCreated++
                this.hikerRepository.updateHiker(hiker)
                val historyItem = HikerHistoryItem(
                    HikerHistoryType.TRAIL_CREATED,
                    trail.creationDate,
                    trail.id,
                    trail.name,
                    trail.location.toString(),
                    trail.mainPhotoUrl
                )
                this.hikerRepository.addHikerHistoryItem(hiker.id, historyItem)
            }
        }
    }
}