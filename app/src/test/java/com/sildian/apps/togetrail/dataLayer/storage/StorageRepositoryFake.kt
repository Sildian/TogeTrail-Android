package com.sildian.apps.togetrail.dataLayer.storage

import com.sildian.apps.togetrail.common.utils.nextUrlString
import com.sildian.apps.togetrail.common.network.StorageException
import kotlin.random.Random

class StorageRepositoryFake(
    private val error: StorageException? = null,
    private val imageUrl: String = Random.nextUrlString(),
) : StorageRepository {

    var uploadImageSuccessCount: Int = 0 ; private set
    var deleteImageSuccessCount: Int = 0 ; private set

    override suspend fun uploadImage(imageLocalPath: String): String {
        error?.let { throw it } ?: uploadImageSuccessCount++
        return imageUrl
    }

    override suspend fun deleteImage(imageUrl: String) {
        error?.let { throw it } ?: deleteImageSuccessCount++
    }
}