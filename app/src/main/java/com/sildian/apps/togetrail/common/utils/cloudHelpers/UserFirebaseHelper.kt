package com.sildian.apps.togetrail.common.utils.cloudHelpers

import android.net.Uri
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest

/*************************************************************************************************
 * Provides with Firebase queries allowing to manage users
 ************************************************************************************************/

object UserFirebaseHelper {

    /**
     * Gets the current user connected to Firebase
     * @return the current user, or null if the user is not connected
     */

    fun getCurrentUser() : FirebaseUser? = FirebaseAuth.getInstance().currentUser

    /**
     * Signs a user out
     */

    fun signUserOut(){
        FirebaseAuth.getInstance().signOut()
    }

    /**
     * Updates a user's profile in Firebase
     * @param displayName : the new name
     * @param photoUri : the new photo Uri
     * @return a task result
     */

    fun updateUserProfile(displayName:String, photoUri:String?): Task<Void>? {
        val user = getCurrentUser()
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(displayName)
            .setPhotoUri(Uri.parse(photoUri))
            .build()
        return user?.updateProfile(profileUpdates)
    }

    /**
     * Updates a user's password in Firebase
     * @param password : the new password
     * @return a task result
     */

    fun updateUserPassword(password:String):Task<Void>? =
        getCurrentUser()?.updatePassword(password)

    /**
     * Definitely deletes a user from Firebase
     * @return a task result
     */

    fun deleteUser():Task<Void>? =
        getCurrentUser()?.delete()
}