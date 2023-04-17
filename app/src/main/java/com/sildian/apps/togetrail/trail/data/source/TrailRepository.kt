package com.sildian.apps.togetrail.trail.data.source

import com.google.firebase.firestore.DocumentReference
import com.sildian.apps.togetrail.trail.data.models.Trail
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/*************************************************************************************************
 * Repository for Trail
 ************************************************************************************************/

/***************************************Definition***********************************************/

@Deprecated("Replaced by new repositories")
interface TrailRepository {

    /**
     * Gets a trail reference
     * @param trailId : the trail's id
     * @return the document reference
     */

    @Throws(Exception::class)
    fun getTrailReference(trailId:String): DocumentReference

    /**
     * Gets a trail
     * @param trailId : the trail's id
     * @return the obtained trail
     * @throws Exception if the request fails
     */

    @Throws(Exception::class)
    suspend fun getTrail(trailId:String): Trail?

    /**
     * Adds a trail
     * @param trail : the trail to add
     * @return the created trail's id
     * @throws Exception if the request fails
     */

    @Throws(Exception::class)
    suspend fun addTrail(trail:Trail): String?

    /**
     * Updates a trail
     * @param trail : the trail to update
     * @throws Exception if the request fails
     */

    @Throws(Exception::class)
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

    override fun getTrailReference(trailId: String): DocumentReference =
        TrailFirebaseQueries.getTrail(trailId)

    override suspend fun getTrail(trailId: String): Trail? =
        TrailFirebaseQueries
            .getTrail(trailId)
            .get()
            .await()
            ?.toObject(Trail::class.java)

    override suspend fun addTrail(trail: Trail): String? =
        TrailFirebaseQueries
            .createTrail(trail)
            .await()
            .id

    override suspend fun updateTrail(trail:Trail) {
        TrailFirebaseQueries
            .updateTrail(trail)
            .await()
    }
}