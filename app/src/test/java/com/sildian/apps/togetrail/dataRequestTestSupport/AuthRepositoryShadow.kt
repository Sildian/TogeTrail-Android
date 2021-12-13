package com.sildian.apps.togetrail.dataRequestTestSupport

import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseUser
import com.sildian.apps.togetrail.common.utils.cloudHelpers.AuthRepository
import org.robolectric.annotation.Implementation
import org.robolectric.annotation.Implements

/*************************************************************************************************
 * Shadow used to avoid requests to the server during data request tests
 ************************************************************************************************/

@Implements(AuthRepository::class)
class AuthRepositoryShadow {

    companion object {
        private const val EXCEPTION_MESSAGE_REQUEST_FAILURE = "FAKE AuthRepository : Request failure"
    }

    @Implementation
    fun getCurrentUser(): FirebaseUser? {
        println("FAKE AuthRepository : Get current user")
        if (FirebaseSimulator.requestShouldFail) {
            throw FirebaseException(EXCEPTION_MESSAGE_REQUEST_FAILURE)
        }
        return FirebaseSimulator.currentUser
    }

    @Implementation
    fun signUserOut() {
        println("FAKE AuthRepository : Sign user out")
        if (FirebaseSimulator.requestShouldFail) {
            throw FirebaseException(EXCEPTION_MESSAGE_REQUEST_FAILURE)
        }
        FirebaseSimulator.currentUser = null
    }

    @Implementation
    suspend fun updateUserProfile(displayName:String, photoUri:String?) {
        println("FAKE AuthRepository : Update user profile")
        if (FirebaseSimulator.requestShouldFail) {
            throw FirebaseException(EXCEPTION_MESSAGE_REQUEST_FAILURE)
        }
        FirebaseSimulator.currentUser?.email?.let { email ->
            FirebaseSimulator.setCurrentUser(email, displayName, photoUri)
        }
    }

    @Implementation
    suspend fun resetUserPassword() {
        println("FAKE AuthRepository : Reset user password")
        if (FirebaseSimulator.requestShouldFail) {
            throw FirebaseException(EXCEPTION_MESSAGE_REQUEST_FAILURE)
        }
    }

    @Implementation
    suspend fun deleteUserAccount() {
        println("FAKE AuthRepository : Delete user account")
        if (FirebaseSimulator.requestShouldFail) {
            throw FirebaseException(EXCEPTION_MESSAGE_REQUEST_FAILURE)
        }
        FirebaseSimulator.currentUser = null
    }
}