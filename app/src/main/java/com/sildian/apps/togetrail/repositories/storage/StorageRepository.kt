package com.sildian.apps.togetrail.repositories.storage

interface StorageRepository {
    suspend fun uploadImage(imageLocalPath: String): String
    suspend fun deleteImage(imageUrl: String)
}