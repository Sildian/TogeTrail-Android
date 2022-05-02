package com.sildian.apps.togetrail.common.utils.cloudHelpers

import com.google.firebase.auth.FirebaseUser
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/*************************************************************************************************
 * Repository for Authentication
 ************************************************************************************************/

/***************************************Definition***********************************************/

interface AuthRepository {

    /**
     * Gets the current user connected to the app
     * @return the current user, or null if the user is not connected
     */

    fun getCurrentUser(): FirebaseUser?

    /**Signs a user out**/

    fun signUserOut()

    /**
     * Updates a user's profile in the cloud
     * @param displayName : the new name
     * @param photoUri : the new photo Uri
     * @throws Exception if the request fails
     */

    suspend fun updateUserProfile(displayName: String, photoUri: String?)

    /**
     * Sends an email to the user to let him reset his password
     * @throws Exception if the request fails
     */

    suspend fun resetUserPassword()

    /**
     * Definitely deletes a user from Firebase
     * @throws Exception if the request fails
     */

    suspend fun deleteUserAccount()
}

/************************************Injection module********************************************/

@Module
@InstallIn(ViewModelComponent::class)
object AuthRepositoryModule {

    @Provides
    fun provideRealAuthRepository(): AuthRepository = RealAuthRepository()
}

/*********************************Real implementation*******************************************/

@ViewModelScoped
class RealAuthRepository @Inject constructor(): AuthRepository {

    override fun getCurrentUser(): FirebaseUser? = AuthFirebaseQueries.getCurrentUser()

    override fun signUserOut() {
        AuthFirebaseQueries.signUserOut()
    }

    @Throws(Exception::class)
    override suspend fun updateUserProfile(displayName: String, photoUri: String?) {
        try {
            AuthFirebaseQueries.updateUserProfile(displayName, photoUri)?.await()
        } catch (e: Exception) {
            throw e
        }
    }

    @Throws(Exception::class)
    override suspend fun resetUserPassword() {
        try {
            AuthFirebaseQueries.resetUserPassword()?.await()
        } catch (e: Exception) {
            throw e
        }
    }

    @Throws(Exception::class)
    override suspend fun deleteUserAccount() {
        try {
            AuthFirebaseQueries.deleteUserAccount()?.await()
        } catch (e: Exception) {
            throw e
        }
    }
}