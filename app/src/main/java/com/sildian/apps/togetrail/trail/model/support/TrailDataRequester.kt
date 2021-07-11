package com.sildian.apps.togetrail.trail.model.support

import android.util.Log
import com.google.firebase.firestore.DocumentReference
import com.sildian.apps.togetrail.common.utils.cloudHelpers.StorageRepository
import com.sildian.apps.togetrail.hiker.model.core.HikerHistoryItem
import com.sildian.apps.togetrail.hiker.model.core.HikerHistoryType
import com.sildian.apps.togetrail.hiker.model.support.CurrentHikerInfo
import com.sildian.apps.togetrail.hiker.model.support.HikerRepository
import com.sildian.apps.togetrail.trail.model.core.Trail
import com.sildian.apps.togetrail.trail.model.core.TrailPointOfInterest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.jvm.Throws

/*************************************************************************************************
 * Run jobs to request data related to Trail
 ************************************************************************************************/

class TrailDataRequester {

    /************************************Static items********************************************/

    companion object {

        /**Logs**/
        private const val TAG = "TrailDataRequester"

        /**Exceptions messages**/
        private const val EXCEPTION_MESSAGE_NULL_HIKER = "Cannot perform the requested operation with a null hiker"
        private const val EXCEPTION_MESSAGE_NULL_TRAIL = "Cannot perform the requested operation with a null trail"
        private const val EXCEPTION_MESSAGE_NO_ID_TRAIL = "Cannot perform the requested operation with a trail without id"
    }

    /************************************Repositories********************************************/

    private val storageRepository = StorageRepository()
    private val hikerRepository = HikerRepository()
    private val trailRepository = TrailRepository()

    /***************************************Requests********************************************/

    /**
     * Loads a trail from the database in real time
     * @param trailId : the trail's id
     */

    fun loadTrailFromDatabaseRealTime(trailId: String): DocumentReference =
        trailRepository.getTrailReference(trailId)

    /**
     * Loads a trail from the database
     * @param trailId : the trail's id
     * @return the resulted trail
     * @throws Exception if the request fails
     */

    @Throws(Exception::class)
    suspend fun loadTrailFromDatabase(trailId: String): Trail? =
        withContext(Dispatchers.IO) {
            try {
                async { trailRepository.getTrail(trailId) }.await()
            }
            catch (e: Exception) {
                throw e
            }
        }

    /**
     * Saves the trail within the database
     * @param trail : the trail to be saved
     * @param imagePathToDelete : path to the hiker's profile image to be deleted if needed
     * @param imagePathToUpload : path to the hiker's profile image to be uploaded if needed
     * @throws NullPointerException if the trail or the current hiker is null
     * @throws Exception if the request fails
     */

    @Throws(Exception::class)
    suspend fun saveTrailInDatabase(trail: Trail?, imagePathToDelete: String?, imagePathToUpload: String?) {
        try {
            saveTrailInDatabase(trail, null, false, imagePathToDelete, imagePathToUpload)
        }
        catch (e: Exception) {
            throw e
        }
    }

    /**
     * Saves the trail within the database with a POI
     * @param trail : the trail to be saved
     * @param trailPointOfInterest : the POI to be saved if needed
     * @param imagePathToDelete : path to the hiker's profile image to be deleted if needed
     * @param imagePathToUpload : path to the hiker's profile image to be uploaded if needed
     * @throws NullPointerException if the trail or the current hiker is null
     * @throws Exception if the request fails
     */

    @Throws(Exception::class)
    suspend fun saveTrailInDatabase(trail: Trail?, trailPointOfInterest: TrailPointOfInterest?, imagePathToDelete: String?, imagePathToUpload: String?) {
        try {
            saveTrailInDatabase(
                trail,
                trailPointOfInterest,
                true,
                imagePathToDelete,
                imagePathToUpload
            )
        }
        catch (e: Exception) {
            throw e
        }
    }

    /**Saves the trail within the database**/

    @Throws(Exception::class)
    private suspend fun saveTrailInDatabase(
        trail: Trail?, trailPointOfInterest: TrailPointOfInterest?, savePOI: Boolean,
        imagePathToDelete: String?, imagePathToUpload: String?
    ) {
        withContext(Dispatchers.IO) {

            try {

                val hiker = CurrentHikerInfo.currentHiker
                if (hiker != null) {

                    if (trail != null) {

                        trail.lastUpdate = Date()
                        trail.authorName = hiker.name
                        trail.authorPhotoUrl = hiker.photoUrl

                        /*Stores or deletes the image in the cloud if necessary*/

                        imagePathToDelete?.let { url ->
                            launch {
                                try {
                                    storageRepository.deleteImage(url)
                                } catch (e: Exception) {
                                    Log.w(TAG, "Failed to delete photo at url $url : ${e.message}")
                                }
                            }.join()
                        }
                        imagePathToUpload?.let { uri ->
                            val newImageUrl = async { storageRepository.uploadImage(uri) }.await()
                            if (savePOI) {
                                trailPointOfInterest?.photoUrl = newImageUrl
                            } else {
                                trail.mainPhotoUrl = newImageUrl
                            }
                        }

                        /*If the trail is new...*/

                        if (trail.id == null) {

                            /*Creates the trail*/

                            trail.authorId = hiker.id
                            trail.id = async { trailRepository.addTrail(trail) }.await()
                            launch { trailRepository.updateTrail(trail) }.join()

                            /*Updates the author's profile*/

                            hiker.nbTrailsCreated++
                            launch { hikerRepository.updateHiker(hiker) }.join()

                            /*And creates an history item*/

                            val historyItem = HikerHistoryItem(
                                HikerHistoryType.TRAIL_CREATED,
                                trail.creationDate,
                                trail.id,
                                trail.name,
                                trail.location.toString(),
                                trail.mainPhotoUrl
                            )
                            launch {
                                hikerRepository.addHikerHistoryItem(
                                    hiker.id,
                                    historyItem
                                )
                            }.join()
                        } else {

                            /*If the trail is not new, just updates it*/

                            launch { trailRepository.updateTrail(trail) }.join()
                        }
                    } else {
                        throw NullPointerException(EXCEPTION_MESSAGE_NULL_TRAIL)
                    }
                } else {
                    throw NullPointerException(EXCEPTION_MESSAGE_NULL_HIKER)
                }
            }
            catch (e: Exception) {
                throw e
            }
        }
    }

    /**
     * Like a trail
     * @param trail : the trail
     * @throws IllegalArgumentException if the trail has no id
     * @throws NullPointerException if the trail or the current hiker is null
     * @throws Exception if the request fails
     */

    @Throws(Exception::class)
    suspend fun likeTrail(trail: Trail?) {
        withContext(Dispatchers.IO) {
            try {
                val hiker = CurrentHikerInfo.currentHiker
                if (hiker != null) {
                    if (trail != null) {
                        if (trail.id != null) {
                            trail.nbLikes++
                            launch { hikerRepository.updateHikerLikedTrail(hiker.id, trail) }
                            launch { trailRepository.updateTrail(trail) }
                        }
                        else {
                            throw IllegalArgumentException(EXCEPTION_MESSAGE_NO_ID_TRAIL)
                        }
                    }
                    else {
                        throw NullPointerException(EXCEPTION_MESSAGE_NULL_TRAIL)
                    }
                }
                else {
                    throw NullPointerException(EXCEPTION_MESSAGE_NULL_HIKER)
                }
            }
            catch (e: Exception) {
                throw e
            }
        }
    }

    /**
     * Unlike a trail
     * @param trail : the trail
     * @throws IllegalArgumentException if the trail has no id
     * @throws NullPointerException if the trail or the current hiker is null
     * @throws Exception if the request fails
     */

    @Throws(Exception::class)
    suspend fun unlikeTrail(trail: Trail?) {
        withContext(Dispatchers.IO) {
            try {
                val hiker = CurrentHikerInfo.currentHiker
                if (hiker != null) {
                    if (trail != null) {
                        if (trail.id != null) {
                            if (trail.nbLikes > 0) {
                                trail.nbLikes--
                            }
                            launch { hikerRepository.deleteHikerLikedTrail(hiker.id, trail.id!!) }
                            launch { trailRepository.updateTrail(trail) }
                        }
                        else {
                            throw IllegalArgumentException(EXCEPTION_MESSAGE_NO_ID_TRAIL)
                        }
                    }
                    else {
                        throw NullPointerException(EXCEPTION_MESSAGE_NULL_TRAIL)
                    }
                }
                else {
                    throw NullPointerException(EXCEPTION_MESSAGE_NULL_HIKER)
                }
            }
            catch (e: Exception) {
                throw e
            }
        }
    }

    /**
     * Mark a trail
     * @param trail : the trail
     * @throws IllegalArgumentException if the trail has no id
     * @throws NullPointerException if the trail or the current hiker is null
     * @throws Exception if the request fails
     */

    @Throws(Exception::class)
    suspend fun markTrail(trail: Trail?) {
        withContext(Dispatchers.IO) {
            try {
                val hiker = CurrentHikerInfo.currentHiker
                if (hiker != null) {
                    if (trail != null) {
                        if (trail.id != null) {
                            launch { hikerRepository.updateHikerMarkedTrail(hiker.id, trail) }
                        }
                        else {
                            throw IllegalArgumentException(EXCEPTION_MESSAGE_NO_ID_TRAIL)
                        }
                    }
                    else {
                        throw NullPointerException(EXCEPTION_MESSAGE_NULL_TRAIL)
                    }
                }
                else {
                    throw NullPointerException(EXCEPTION_MESSAGE_NULL_HIKER)
                }
            }
            catch (e: Exception) {
                throw e
            }
        }
    }

    /**
     * Unmark a trail
     * @param trail : the trail
     * @throws IllegalArgumentException if the trail has no id
     * @throws NullPointerException if the trail or the current hiker is null
     * @throws Exception if the request fails
     */

    @Throws(Exception::class)
    suspend fun unmarkTrail(trail: Trail?) {
        withContext(Dispatchers.IO) {
            try {
                val hiker = CurrentHikerInfo.currentHiker
                if (hiker != null) {
                    if (trail != null) {
                        if (trail.id != null) {
                            launch { hikerRepository.deleteHikerMarkedTrail(hiker.id, trail.id!!) }
                        }
                        else {
                            throw IllegalArgumentException(EXCEPTION_MESSAGE_NO_ID_TRAIL)
                        }
                    }
                    else {
                        throw NullPointerException(EXCEPTION_MESSAGE_NULL_TRAIL)
                    }
                }
                else {
                    throw NullPointerException(EXCEPTION_MESSAGE_NULL_HIKER)
                }
            }
            catch (e: Exception) {
                throw e
            }
        }
    }
}