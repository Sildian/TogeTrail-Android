package com.sildian.apps.togetrail.common.utils.cloudHelpers

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

/*************************************************************************************************
 * Repository for Storage
 ************************************************************************************************/

class StorageRepository {

    /**
     * Uploads an image into the cloud
     * @param filePath : the original image's path
     * @return the uploaded image's url on the server
     * @throws Exception if the request fails
     */

    @Throws(Exception::class)
    suspend fun uploadImage(filePath: String): String? =
        withContext(Dispatchers.IO) {
            try {
                StorageFirebaseHelper.uploadImage(filePath)
                    .await()
                    .storage
                    .downloadUrl
                    .await()
                    .toString()
            }
            catch (e: Exception) {
                throw e
            }
        }

    /**
     * Deletes an image from the cloud
     * @param url : the image's url in the cloud
     * @throws Exception if the request fails
     */

    @Throws(Exception::class)
    suspend fun deleteImage(url: String) {
        withContext(Dispatchers.IO){
            try {
                StorageFirebaseHelper.deleteImage(url)
                    .await()
            }
            catch (e: Exception) {
                throw e
            }
        }
    }
}