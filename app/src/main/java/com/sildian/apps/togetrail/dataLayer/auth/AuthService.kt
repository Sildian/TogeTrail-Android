package com.sildian.apps.togetrail.dataLayer.auth

import android.net.Uri
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import javax.inject.Inject

class AuthService @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
) {

    val currentUser: FirebaseUser? = firebaseAuth.currentUser

    fun signUserOut() {
        firebaseAuth.signOut()
    }

    fun updateUser(name: String, photoUrl: String?): Task<Void>? {
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(name)
            .setPhotoUri(photoUrl?.let { Uri.parse(it) })
            .build()
        return currentUser?.updateProfile(profileUpdates)
    }

    fun resetUserPassword(): Task<Void>? =
        currentUser?.email?.let { userEmail ->
            firebaseAuth.sendPasswordResetEmail(userEmail)
        }

    fun updateUserEmailAddress(newEmailAddress: String): Task<Void>? =
        currentUser?.updateEmail(newEmailAddress)

    fun deleteUser(): Task<Void>? =
        currentUser?.delete()
}