package com.sildian.apps.togetrail.repositories.storage

import android.net.Uri
import com.sildian.apps.togetrail.common.network.storageOperation
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@ViewModelScoped
class StorageRepositoryImpl @Inject constructor(
    private val storageService: StorageService
) : StorageRepository {

    override suspend fun uploadImage(imageLocalPath: String): Uri =
        storageOperation {
            storageService.uploadImage(imageLocalPath = imageLocalPath)
                .await()
                .storage
                .downloadUrl
                .await()
        }

    override suspend fun deleteImage(imageUrl: String) {
        storageOperation {
            storageService.deleteImage(imageUrl = imageUrl).await()
        }
    }
}