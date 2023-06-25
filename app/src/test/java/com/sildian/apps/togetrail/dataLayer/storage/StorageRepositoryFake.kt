package com.sildian.apps.togetrail.dataLayer.storage

import android.net.Uri
import androidx.core.net.toUri
import com.sildian.apps.togetrail.common.utils.nextUrlString
import com.sildian.apps.togetrail.common.network.StorageException
import kotlin.random.Random

class StorageRepositoryFake(
    private val error: StorageException? = null,
    private val imageUrl: Uri = Random.nextUrlString().toUri(),
) : StorageRepository {

    var deleteImageSuccessCount: Int = 0 ; private set

    override suspend fun uploadImage(imageLocalPath: String): Uri =
        error?.let { throw it } ?: imageUrl

    override suspend fun deleteImage(imageUrl: String) {
        error?.let { throw it } ?: deleteImageSuccessCount++
    }
}