package com.sildian.apps.togetrail.common.utils.cloudHelpers

import com.google.firebase.auth.FirebaseUser
import com.sildian.apps.togetrail.hiker.model.core.Hiker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

/*************************************************************************************************
 * Repository for Authentication
 ************************************************************************************************/

object AuthRepository {

    /**
     * Gets the current user's hiker profile
     * @return the current user's hiker profile or null if the user is not connected
     */

    fun getCurrentUserProfile(): Hiker? = AuthFirebaseHelper.currentUserProfile

    /**
     * Gets the current user connected to the app
     * @return the current user, or null if the user is not connected
     */

    fun getCurrentUser() : FirebaseUser? = AuthFirebaseHelper.getCurrentUser()

    /**
     * Signs a user in and stores the related hiker's profile
     * @param hiker : the hiker's profile
     */

    fun signUserIn(hiker: Hiker) {
        AuthFirebaseHelper.signUserIn(hiker)
    }

    /**Signs a user out**/

    fun signUserOut(){
        AuthFirebaseHelper.signUserOut()
    }

    /**
     * Updates a user's profile in the cloud
     * @param displayName : the new name
     * @param photoUri : the new photo Uri
     */

    @Throws(Exception::class)
    suspend fun updateUserProfile(displayName:String, photoUri:String?){
        withContext(Dispatchers.IO){
            try{
                AuthFirebaseHelper.updateUserProfile(displayName, photoUri)?.await()
            } catch (e: Exception) {
                throw e
            }
        }
    }

    /**Sends an email to the user to let you reset his password**/

    @Throws(Exception::class)
    suspend fun resetUserPassword(){
        withContext(Dispatchers.IO){
            try{
                AuthFirebaseHelper.resetUserPassword()?.await()
            } catch (e: Exception) {
                throw e
            }
        }
    }

    /**Definitely deletes a user from Firebase**/

    @Throws(Exception::class)
    suspend fun deleteUserAccount(){
        withContext(Dispatchers.IO){
            try {
                AuthFirebaseHelper.deleteUserAccount()?.await()
            } catch (e: Exception) {
                throw e
            }
        }
    }
}