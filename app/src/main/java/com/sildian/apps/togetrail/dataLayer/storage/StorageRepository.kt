package com.sildian.apps.togetrail.dataLayer.storage

import android.net.Uri
import com.sildian.apps.togetrail.common.network.StorageException

interface StorageRepository {
    @Throws(StorageException::class)
    suspend fun uploadImage(imageLocalPath: String): Uri
    @Throws(StorageException::class)
    suspend fun deleteImage(imageUrl: String)
}