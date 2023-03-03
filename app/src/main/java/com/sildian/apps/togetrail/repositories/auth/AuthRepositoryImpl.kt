package com.sildian.apps.togetrail.repositories.auth

import com.sildian.apps.togetrail.common.network.User
import com.sildian.apps.togetrail.common.network.toUser
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@ViewModelScoped
class AuthRepositoryImpl @Inject constructor(
    private val authService: AuthService,
) : AuthRepository {

    override fun getCurrentUser(): User? =
        authService.currentUser?.toUser()

    override fun signUserOut() {
        authService.signUserOut()
    }

    override suspend fun updateUser(name: String, photoUrl: String?) {
        authService.updateUser(name = name, photoUrl = photoUrl)?.await()
    }

    override suspend fun resetUserPassword() {
        authService.resetUserPassword()?.await()
    }

    override suspend fun updateUserEmailAddress(newEmailAddress: String) {
        authService.updateUserEmailAddress(newEmailAddress = newEmailAddress)?.await()
    }

    override suspend fun deleteUser() {
        authService.deleteUser()?.await()
    }
}