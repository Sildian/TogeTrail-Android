package com.sildian.apps.togetrail.repositories.storage

import android.net.Uri

interface StorageRepository {
    suspend fun uploadImage(imageLocalPath: String): Uri
    suspend fun deleteImage(imageUrl: String)
}