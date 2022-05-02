package com.sildian.apps.togetrail.common.utils.cloudHelpers

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/*************************************************************************************************
 * Repository for Storage
 ************************************************************************************************/

/***************************************Definition***********************************************/

interface StorageRepository {

    /**
     * Uploads an image into the cloud
     * @param filePath : the original image's path
     * @return the uploaded image's url on the server
     * @throws Exception if the request fails
     */

    suspend fun uploadImage(filePath: String): String?

    /**
     * Deletes an image from the cloud
     * @param url : the image's url in the cloud
     * @throws Exception if the request fails
     */

    suspend fun deleteImage(url: String)
}

/************************************Injection module********************************************/

@Module
@InstallIn(ViewModelComponent::class)
object StorageRepositoryModule {

    @Provides
    fun provideRealStorageRepository(): StorageRepository = RealStorageRepository()
}

/*********************************Real implementation*******************************************/

@ViewModelScoped
class RealStorageRepository @Inject constructor(): StorageRepository {

    @Throws(Exception::class)
    override suspend fun uploadImage(filePath: String): String? =
        try {
            StorageFirebaseQueries.uploadImage(filePath)
                .await()
                .storage
                .downloadUrl
                .await()
                .toString()
        } catch (e: Exception) {
            throw e
        }

    @Throws(Exception::class)
    override suspend fun deleteImage(url: String) {
        try {
            StorageFirebaseQueries.deleteImage(url)
                .await()
        } catch (e: Exception) {
            throw e
        }
    }
}