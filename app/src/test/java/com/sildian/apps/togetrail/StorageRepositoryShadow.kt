package com.sildian.apps.togetrail

import com.google.firebase.FirebaseException
import com.sildian.apps.togetrail.common.utils.cloudHelpers.StorageRepository
import org.robolectric.annotation.Implementation
import org.robolectric.annotation.Implements

/*************************************************************************************************
 * Shadow used to avoid requests to the server during data requester tests
 ************************************************************************************************/

@Implements(StorageRepository::class)
class StorageRepositoryShadow {

    companion object {
        private const val EXCEPTION_MESSAGE_REQUEST_FAILURE = "FAKE StorageRepository : Request failure"
    }

    @Implementation
    suspend fun uploadImage(filePath: String): String? {
        println("FAKE StorageRepository : Upload image")
        if (!BaseDataRequesterTest.requestShouldFail) {
            BaseDataRequesterTest.isImageUploaded = true
            return BaseDataRequesterTest.PHOTO_URL
        }
        else {
            throw FirebaseException(EXCEPTION_MESSAGE_REQUEST_FAILURE)
        }
    }

    @Implementation
    suspend fun deleteImage(url: String) {
        println("FAKE StorageRepository : Delete image")
        if (!BaseDataRequesterTest.requestShouldFail) {
            BaseDataRequesterTest.isImageDeleted = true
        }
        else {
            throw FirebaseException(EXCEPTION_MESSAGE_REQUEST_FAILURE)
        }
    }
}