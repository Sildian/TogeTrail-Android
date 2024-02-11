package com.sildian.apps.togetrail.dataLayer.storage

import com.sildian.apps.togetrail.common.network.storageOperation
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class StorageRepositoryImpl @Inject constructor(
    private val storageService: StorageService
) : StorageRepository {

    override suspend fun uploadImage(imageUri: String): String =
        storageOperation {
            storageService.uploadImage(imageUri = imageUri)
                .await()
                .storage
                .downloadUrl
                .await()
                .toString()
        }

    override suspend fun deleteImage(imageUrl: String) {
        storageOperation {
            storageService.deleteImage(imageUrl = imageUrl).await()
        }
    }
}