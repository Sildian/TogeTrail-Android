package com.sildian.apps.togetrail.common.utils.cloudHelpers

import android.net.Uri
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest

/*************************************************************************************************
 * Provides with Firebase queries allowing to manage user authentication
 ************************************************************************************************/

@Deprecated("Use [AuthService]")
object AuthFirebaseQueries {

    /**
     * Gets the current user connected to Firebase
     * @return the current user, or null if the user is not connected
     */

    fun getCurrentUser() : FirebaseUser? = FirebaseAuth.getInstance().currentUser

    /**
     * Signs a user out
     */

    fun signUserOut() {
        FirebaseAuth.getInstance().signOut()
    }

    /**
     * Updates a user's profile in Firebase
     * @param displayName : the new name
     * @param photoUri : the new photo Uri
     * @return a task result
     */

    fun updateUserProfile(displayName: String, photoUri: String?): Task<Void>? {
        val user = getCurrentUser()
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(displayName)
            .setPhotoUri(if(photoUri != null) Uri.parse(photoUri) else null)
            .build()
        return user?.updateProfile(profileUpdates)
    }

    /**
     * Sends an email to the user to let him reset his password
     * @return a task result
     */

    fun resetUserPassword(): Task<Void>? {
        val userEmail = getCurrentUser()?.email
        return if (userEmail != null) {
            FirebaseAuth.getInstance().sendPasswordResetEmail(userEmail)
        } else {
            null
        }
    }

    /**
     * Updates the user's email address
     * @param newEmailAddress : the new email address
     * @return a task result
     */

    fun changeUserEmailAddress(newEmailAddress: String): Task<Void>? =
        getCurrentUser()?.updateEmail(newEmailAddress)

    /**
     * Definitely deletes a user from Firebase
     * @return a task result
     */

    fun deleteUserAccount(): Task<Void>? =
        getCurrentUser()?.delete()
}