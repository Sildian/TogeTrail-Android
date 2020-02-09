package com.sildian.apps.togetrail.common.utils.storageHelpers

import com.google.android.gms.tasks.Task
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import java.io.File
import java.io.FileInputStream
import java.util.*

/*************************************************************************************************
 * Provides with Firebase queries allowing to store images
 ************************************************************************************************/

object ImageFirebaseStorageHelper {

    /**
     * Uploads an image into Firebase
     * @param filePath : the original image's path
     * @return a task result
     */

    fun uploadImage(filePath:String): UploadTask{
        val imageId = UUID.randomUUID().toString()
        val imageReference = FirebaseStorage.getInstance().reference.child(imageId)
        return imageReference.putStream(FileInputStream(File(filePath)))
    }

    /**
     * Deletes an image from Firebase
     * @param url : the image's url in Firebase
     * @return a task result
     */

    fun deleteImage(url:String): Task<Void> {
        return FirebaseStorage.getInstance().getReferenceFromUrl(url).delete()
    }
}