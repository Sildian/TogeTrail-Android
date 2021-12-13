package com.sildian.apps.togetrail.dataRequestTestSupport

import com.google.firebase.FirebaseException
import com.sildian.apps.togetrail.common.utils.cloudHelpers.StorageRepository
import org.robolectric.annotation.Implementation
import org.robolectric.annotation.Implements

/*************************************************************************************************
 * Shadow used to avoid requests to the server during data request tests
 ************************************************************************************************/

@Implements(StorageRepository::class)
class StorageRepositoryShadow {

    companion object {
        private const val EXCEPTION_MESSAGE_REQUEST_FAILURE = "FAKE StorageRepository : Request failure"
    }

    @Implementation
    suspend fun uploadImage(filePath: String): String? {
        println("FAKE StorageRepository : Upload image")
        if (FirebaseSimulator.requestShouldFail) {
            throw FirebaseException(EXCEPTION_MESSAGE_REQUEST_FAILURE)
        }
        FirebaseSimulator.storageUrls.add(filePath)
        return filePath
    }

    @Implementation
    suspend fun deleteImage(url: String) {
        println("FAKE StorageRepository : Delete image")
        if (FirebaseSimulator.requestShouldFail) {
            throw FirebaseException(EXCEPTION_MESSAGE_REQUEST_FAILURE)
        }
        FirebaseSimulator.storageUrls.remove(url)
    }
}