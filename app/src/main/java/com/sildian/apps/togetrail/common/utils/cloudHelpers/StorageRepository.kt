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

    @Throws(Exception::class)
    suspend fun uploadImage(filePath: String): String?

    /**
     * Deletes an image from the cloud
     * @param url : the image's url in the cloud
     * @throws Exception if the request fails
     */

    @Throws(Exception::class)
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

    override suspend fun uploadImage(filePath: String): String? =
        StorageFirebaseQueries.uploadImage(filePath)
            .await()
            .storage
            .downloadUrl
            .await()
            .toString()

    override suspend fun deleteImage(url: String) {
        StorageFirebaseQueries.deleteImage(url)
            .await()
    }
}