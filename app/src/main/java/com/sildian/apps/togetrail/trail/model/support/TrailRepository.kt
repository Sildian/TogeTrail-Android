package com.sildian.apps.togetrail.trail.model.support

import com.google.firebase.firestore.DocumentReference
import com.sildian.apps.togetrail.trail.model.core.Trail
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

/*************************************************************************************************
 * Repository for Trail
 ************************************************************************************************/

object TrailRepository {

    /**
     * Gets a trail reference
     * @param trailId : the trail's id
     * @return the document reference
     */

    fun getTrailReference(trailId:String): DocumentReference =
        TrailFirebaseQueries.getTrail(trailId)

    /**
     * Gets a trail
     * @param trailId : the trail's id
     * @return the obtained trail
     */

    @Throws(Exception::class)
    suspend fun getTrail(trailId:String): Trail? =
        withContext(Dispatchers.IO) {
            try {
                TrailFirebaseQueries
                    .getTrail(trailId)
                    .get()
                    .await()
                    ?.toObject(Trail::class.java)
            } catch (e: Exception) {
                throw e
            }
        }

    /**
     * Adds a trail
     * @param trail : the trail to add
     * @return the trail's id
     */

    @Throws(Exception::class)
    suspend fun addTrail(trail:Trail):String? =
        withContext(Dispatchers.IO){
            try{
                TrailFirebaseQueries
                    .createTrail(trail)
                    .await()
                    .id
            }catch (e: Exception) {
                throw e
            }
        }

    /**
     * Updates a trail
     * @param trail : the trail to update
     */

    @Throws(Exception::class)
    suspend fun updateTrail(trail:Trail){
        withContext(Dispatchers.IO) {
            try {
                TrailFirebaseQueries
                    .updateTrail(trail)
                    .await()
            } catch (e: Exception) {
                throw e
            }
        }
    }
}