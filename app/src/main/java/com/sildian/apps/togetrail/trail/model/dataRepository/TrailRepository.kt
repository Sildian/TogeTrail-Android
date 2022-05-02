package com.sildian.apps.togetrail.trail.model.dataRepository

import com.google.firebase.firestore.DocumentReference
import com.sildian.apps.togetrail.trail.model.core.Trail
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

/*************************************************************************************************
 * Repository for Trail
 ************************************************************************************************/

/***************************************Definition***********************************************/

interface TrailRepository {

    /**
     * Gets a trail reference
     * @param trailId : the trail's id
     * @return the document reference
     */

    fun getTrailReference(trailId:String): DocumentReference

    /**
     * Gets a trail
     * @param trailId : the trail's id
     * @return the obtained trail
     * @throws Exception if the request fails
     */

    suspend fun getTrail(trailId:String): Trail?

    /**
     * Adds a trail
     * @param trail : the trail to add
     * @return the created trail's id
     * @throws Exception if the request fails
     */

    suspend fun addTrail(trail:Trail): String?

    /**
     * Updates a trail
     * @param trail : the trail to update
     * @throws Exception if the request fails
     */

    suspend fun updateTrail(trail:Trail)
}

/************************************Injection module********************************************/

@Module
@InstallIn(ViewModelComponent::class)
object TrailRepositoryModule {

    @Provides
    fun provideRealTrailRepository(): TrailRepository = RealTrailRepository()
}

/*********************************Real implementation*******************************************/

@ViewModelScoped
class RealTrailRepository @Inject constructor(): TrailRepository {

    override fun getTrailReference(trailId:String): DocumentReference =
        TrailFirebaseQueries.getTrail(trailId)

    @Throws(Exception::class)
    override suspend fun getTrail(trailId:String): Trail? =
        withContext(Dispatchers.IO) {
            try {
                TrailFirebaseQueries
                    .getTrail(trailId)
                    .get()
                    .await()
                    ?.toObject(Trail::class.java)
            }
            catch (e: Exception) {
                throw e
            }
        }

    @Throws(Exception::class)
    override suspend fun addTrail(trail:Trail): String? =
        withContext(Dispatchers.IO) {
            try {
                TrailFirebaseQueries
                    .createTrail(trail)
                    .await()
                    .id
            }
            catch (e: Exception) {
                throw e
            }
        }

    @Throws(Exception::class)
    override suspend fun updateTrail(trail:Trail) {
        withContext(Dispatchers.IO) {
            try {
                TrailFirebaseQueries
                    .updateTrail(trail)
                    .await()
            }
            catch (e: Exception) {
                throw e
            }
        }
    }
}