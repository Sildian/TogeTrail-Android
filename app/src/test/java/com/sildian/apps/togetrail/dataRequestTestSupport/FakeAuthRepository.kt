package com.sildian.apps.togetrail.dataRequestTestSupport

import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseUser
import com.sildian.apps.togetrail.common.utils.cloudHelpers.AuthRepository

/*************************************************************************************************
 * Fake repository for Authentication
 ************************************************************************************************/

class FakeAuthRepository: AuthRepository {

    companion object {
        private const val EXCEPTION_MESSAGE_REQUEST_FAILURE = "FAKE AuthRepository : Request failure"
    }

    override fun getCurrentUser(): FirebaseUser? {
        println("FAKE AuthRepository : Get current user")
        if (FirebaseSimulator.requestShouldFail) {
            throw FirebaseException(EXCEPTION_MESSAGE_REQUEST_FAILURE)
        }
        return FirebaseSimulator.currentUser
    }

    override fun signUserOut() {
        println("FAKE AuthRepository : Sign user out")
        if (FirebaseSimulator.requestShouldFail) {
            throw FirebaseException(EXCEPTION_MESSAGE_REQUEST_FAILURE)
        }
        FirebaseSimulator.currentUser = null
    }

    override suspend fun updateUserProfile(displayName:String, photoUri:String?) {
        println("FAKE AuthRepository : Update user profile")
        if (FirebaseSimulator.requestShouldFail) {
            throw FirebaseException(EXCEPTION_MESSAGE_REQUEST_FAILURE)
        }
        FirebaseSimulator.currentUser?.uid?.let { uid ->
            FirebaseSimulator.currentUser?.email?.let { email ->
                FirebaseSimulator.setCurrentUser(uid, email, displayName, photoUri)
            }
        }
    }

    override suspend fun resetUserPassword() {
        println("FAKE AuthRepository : Reset user password")
        if (FirebaseSimulator.requestShouldFail) {
            throw FirebaseException(EXCEPTION_MESSAGE_REQUEST_FAILURE)
        }
    }

    override suspend fun deleteUserAccount() {
        println("FAKE AuthRepository : Delete user account")
        if (FirebaseSimulator.requestShouldFail) {
            throw FirebaseException(EXCEPTION_MESSAGE_REQUEST_FAILURE)
        }
        FirebaseSimulator.currentUser = null
    }
}