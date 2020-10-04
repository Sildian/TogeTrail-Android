package com.sildian.apps.togetrail

import com.sildian.apps.togetrail.common.utils.cloudHelpers.StorageRepository
import org.robolectric.annotation.Implementation
import org.robolectric.annotation.Implements

/*************************************************************************************************
 * Shadow used to avoid requests to the server during data requester tests
 ************************************************************************************************/

@Implements(StorageRepository::class)
class StorageRepositoryShadow {

    @Implementation
    suspend fun uploadImage(filePath: String): String? {
        println("FAKE StorageRepository : Upload image")
        BaseDataRequesterTest.isImageUploaded = true
        return BaseDataRequesterTest.PHOTO_URL
    }

    @Implementation
    suspend fun deleteImage(url: String) {
        println("FAKE StorageRepository : Delete image")
        BaseDataRequesterTest.isImageDeleted = true
    }
}