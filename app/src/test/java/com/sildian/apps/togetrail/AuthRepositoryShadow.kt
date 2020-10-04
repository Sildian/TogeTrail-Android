package com.sildian.apps.togetrail

import com.google.firebase.auth.FirebaseUser
import com.sildian.apps.togetrail.common.utils.cloudHelpers.AuthRepository
import org.robolectric.annotation.Implementation
import org.robolectric.annotation.Implements

/*************************************************************************************************
 * Shadow used to avoid requests to the server during data requester tests
 ************************************************************************************************/

@Implements(AuthRepository::class)
class AuthRepositoryShadow {

    @Implementation
    fun getCurrentUser(): FirebaseUser? {
        println("FAKE AuthRepository : Get current user")
        return BaseDataRequesterTest.getUserSample()
    }

    @Implementation
    fun signUserOut() {
        println("FAKE AuthRepository : Sign user out")
        BaseDataRequesterTest.isUserSignedOut = true
    }

    @Implementation
    suspend fun updateUserProfile(displayName:String, photoUri:String?) {
        println("FAKE AuthRepository : Update user profile")
        BaseDataRequesterTest.isUserUpdated = true
    }

    @Implementation
    suspend fun resetUserPassword() {
        println("FAKE AuthRepository : Reset user password")
        BaseDataRequesterTest.isUserPasswordReset = true
    }

    @Implementation
    suspend fun deleteUserAccount() {
        println("FAKE AuthRepository : Delete user account")
        BaseDataRequesterTest.isUserAccountDeleted = true
    }
}