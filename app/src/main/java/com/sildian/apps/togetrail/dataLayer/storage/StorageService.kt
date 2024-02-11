package com.sildian.apps.togetrail.dataLayer.storage

import android.net.Uri
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import java.util.*
import javax.inject.Inject

class StorageService @Inject constructor(
   private val firebaseStorage: FirebaseStorage,
) {

    fun uploadImage(imageUri: String): UploadTask {
        val imageId = UUID.randomUUID().toString()
        return firebaseStorage
            .reference
            .child(imageId)
            .putFile(Uri.parse(imageUri))
    }

    fun deleteImage(imageUrl: String): Task<Void> =
        firebaseStorage.getReferenceFromUrl(imageUrl).delete()
}