package com.sildian.apps.togetrail.common.utils.cloudHelpers

import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

/*************************************************************************************************
 * Repository for Authentication
 ************************************************************************************************/

class AuthRepository {

    /**
     * Gets the current user connected to the app
     * @return the current user, or null if the user is not connected
     */

    fun getCurrentUser() : FirebaseUser? = AuthFirebaseHelper.getCurrentUser()

    /**Signs a user out**/

    fun signUserOut(){
        AuthFirebaseHelper.signUserOut()
    }

    /**
     * Updates a user's profile in the cloud
     * @param displayName : the new name
     * @param photoUri : the new photo Uri
     * @throws Exception if the request fails
     */

    @Throws(Exception::class)
    suspend fun updateUserProfile(displayName:String, photoUri:String?) {
        withContext(Dispatchers.IO){
            try{
                AuthFirebaseHelper.updateUserProfile(displayName, photoUri)?.await()
            }
            catch (e: Exception) {
                throw e
            }
        }
    }

    /**
     * Sends an email to the user to let him reset his password
     * @throws Exception if the request fails
     */

    @Throws(Exception::class)
    suspend fun resetUserPassword() {
        withContext(Dispatchers.IO) {
            try{
                AuthFirebaseHelper.resetUserPassword()?.await()
            }
            catch (e: Exception) {
                throw e
            }
        }
    }

    /**
     * Definitely deletes a user from Firebase
     * @throws Exception if the request fails
     */

    @Throws(Exception::class)
    suspend fun deleteUserAccount() {
        withContext(Dispatchers.IO) {
            try {
                AuthFirebaseHelper.deleteUserAccount()?.await()
            }
            catch (e: Exception) {
                throw e
            }
        }
    }
}