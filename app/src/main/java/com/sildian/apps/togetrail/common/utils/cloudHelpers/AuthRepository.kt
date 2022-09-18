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

    @Throws(Exception::class)
    suspend fun updateUserProfile(displayName: String, photoUri: String?)

    /**
     * Sends an email to the user to let him reset his password
     * @throws Exception if the request fails
     */

    @Throws(Exception::class)
    suspend fun resetUserPassword()

    /**
     * Sends an email to the user to let him change his email address
     * @param newEmailAddress : the new email address
     * @throws Exception if the request fails
     */

    @Throws(Exception::class)
    suspend fun changeUserEmailAddress(newEmailAddress: String)

    /**
     * Definitely deletes a user from Firebase
     * @throws Exception if the request fails
     */

    @Throws(Exception::class)
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

    override suspend fun updateUserProfile(displayName: String, photoUri: String?) {
        AuthFirebaseQueries.updateUserProfile(displayName, photoUri)?.await()
    }

    override suspend fun resetUserPassword() {
        AuthFirebaseQueries.resetUserPassword()?.await()
    }

    override suspend fun changeUserEmailAddress(newEmailAddress: String) {
        AuthFirebaseQueries.changeUserEmailAddress(newEmailAddress)
    }

    override suspend fun deleteUserAccount() {
        AuthFirebaseQueries.deleteUserAccount()?.await()
    }
}