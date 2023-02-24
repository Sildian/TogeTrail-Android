package com.sildian.apps.togetrail.repositories.storage

import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@ViewModelScoped
class StorageRepositoryImpl @Inject constructor(
    private val storageService: StorageService
) : StorageRepository {

    override suspend fun uploadImage(imageLocalPath: String): String =
        storageService.uploadImage(imageLocalPath = imageLocalPath)
            .await()
            .storage
            .downloadUrl
            .await()
            .toString()

    override suspend fun deleteImage(imageUrl: String) {
        storageService.deleteImage(imageUrl = imageUrl).await()
    }
}