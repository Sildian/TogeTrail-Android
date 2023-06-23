package com.sildian.apps.togetrail.dataLayer.storage

import com.google.android.gms.tasks.Task
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import java.io.File
import java.io.FileInputStream
import java.util.*
import javax.inject.Inject

class StorageService @Inject constructor(
   private val firebaseStorage: FirebaseStorage,
) {

    fun uploadImage(imageLocalPath: String): UploadTask {
        val imageId = UUID.randomUUID().toString()
        return firebaseStorage
            .reference
            .child(imageId)
            .putStream(FileInputStream(File(imageLocalPath)))
    }

    fun deleteImage(imageUrl: String): Task<Void> =
        firebaseStorage.getReferenceFromUrl(imageUrl).delete()
}