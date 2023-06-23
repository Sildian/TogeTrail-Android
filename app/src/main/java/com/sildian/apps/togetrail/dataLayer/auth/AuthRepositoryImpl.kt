package com.sildian.apps.togetrail.dataLayer.auth

import com.sildian.apps.togetrail.common.network.User
import com.sildian.apps.togetrail.common.network.authOperation
import com.sildian.apps.togetrail.common.network.toUser
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authService: AuthService,
) : AuthRepository {

    override fun getCurrentUser(): User? =
        authOperation {
            authService.currentUser?.toUser()
        }

    override fun signUserOut() {
        authOperation {
            authService.signUserOut()
        }
    }

    override suspend fun updateUser(name: String, photoUrl: String?) {
        authOperation {
            authService.updateUser(name = name, photoUrl = photoUrl)?.await()
        }
    }

    override suspend fun resetUserPassword() {
        authOperation {
            authService.resetUserPassword()?.await()
        }
    }

    override suspend fun updateUserEmailAddress(newEmailAddress: String) {
        authOperation {
            authService.updateUserEmailAddress(newEmailAddress = newEmailAddress)?.await()
        }
    }

    override suspend fun deleteUser() {
        authOperation {
            authService.deleteUser()?.await()
        }
    }
}