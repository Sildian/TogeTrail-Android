package com.sildian.apps.togetrail

import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseUser
import com.sildian.apps.togetrail.common.utils.cloudHelpers.AuthRepository
import org.robolectric.annotation.Implementation
import org.robolectric.annotation.Implements

/*************************************************************************************************
 * Shadow used to avoid requests to the server during data requester tests
 ************************************************************************************************/

@Implements(AuthRepository::class)
class AuthRepositoryShadow {

    companion object {
        private const val EXCEPTION_MESSAGE_REQUEST_FAILURE = "FAKE AuthRepository : Request failure"
    }

    @Implementation
    fun getCurrentUser(): FirebaseUser? {
        println("FAKE AuthRepository : Get current user")
        if (!BaseDataRequesterTest.requestShouldFail) {
            return BaseDataRequesterTest.getUserSample()
        }
        else {
            throw FirebaseException(EXCEPTION_MESSAGE_REQUEST_FAILURE)
        }
    }

    @Implementation
    fun signUserOut() {
        println("FAKE AuthRepository : Sign user out")
        if (!BaseDataRequesterTest.requestShouldFail) {
            BaseDataRequesterTest.isUserSignedOut = true
        }
        else {
            throw FirebaseException(EXCEPTION_MESSAGE_REQUEST_FAILURE)
        }
    }

    @Implementation
    suspend fun updateUserProfile(displayName:String, photoUri:String?) {
        println("FAKE AuthRepository : Update user profile")
        if (!BaseDataRequesterTest.requestShouldFail) {
            BaseDataRequesterTest.isUserUpdated = true
        }
        else {
            throw FirebaseException(EXCEPTION_MESSAGE_REQUEST_FAILURE)
        }
    }

    @Implementation
    suspend fun resetUserPassword() {
        println("FAKE AuthRepository : Reset user password")
        if (!BaseDataRequesterTest.requestShouldFail) {
            BaseDataRequesterTest.isUserPasswordReset = true
        }
        else {
            throw FirebaseException(EXCEPTION_MESSAGE_REQUEST_FAILURE)
        }
    }

    @Implementation
    suspend fun deleteUserAccount() {
        println("FAKE AuthRepository : Delete user account")
        if (!BaseDataRequesterTest.requestShouldFail) {
            BaseDataRequesterTest.isUserAccountDeleted = true
        }
        else {
            throw FirebaseException(EXCEPTION_MESSAGE_REQUEST_FAILURE)
        }
    }
}