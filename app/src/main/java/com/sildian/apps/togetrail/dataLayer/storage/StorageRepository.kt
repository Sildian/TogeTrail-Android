package com.sildian.apps.togetrail.dataLayer.storage

import com.sildian.apps.togetrail.common.network.StorageException

interface StorageRepository {
    @Throws(StorageException::class)
    suspend fun uploadImage(imageLocalPath: String): String
    @Throws(StorageException::class)
    suspend fun deleteImage(imageUrl: String)
}