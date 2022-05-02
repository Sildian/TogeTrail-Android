package com.sildian.apps.togetrail.dataRequestTestSupport

import com.google.firebase.FirebaseException
import com.sildian.apps.togetrail.common.utils.cloudHelpers.StorageRepository

/*************************************************************************************************
 * Fake repository for Storage
 ************************************************************************************************/

class FakeStorageRepository: StorageRepository {

    companion object {
        private const val EXCEPTION_MESSAGE_REQUEST_FAILURE = "FAKE StorageRepository : Request failure"
    }

    override suspend fun uploadImage(filePath: String): String? {
        println("FAKE StorageRepository : Upload image")
        if (FirebaseSimulator.requestShouldFail) {
            throw FirebaseException(EXCEPTION_MESSAGE_REQUEST_FAILURE)
        }
        FirebaseSimulator.storageUrls.add(filePath)
        return filePath
    }

    override suspend fun deleteImage(url: String) {
        println("FAKE StorageRepository : Delete image")
        if (FirebaseSimulator.requestShouldFail) {
            throw FirebaseException(EXCEPTION_MESSAGE_REQUEST_FAILURE)
        }
        FirebaseSimulator.storageUrls.remove(url)
    }
}