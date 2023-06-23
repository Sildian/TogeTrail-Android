package com.sildian.apps.togetrail.dataLayer.auth

import com.sildian.apps.togetrail.common.network.AuthException
import com.sildian.apps.togetrail.common.network.User
import com.sildian.apps.togetrail.common.network.nextUser
import kotlin.random.Random

class AuthRepositoryFake(
    private val error: AuthException? = null,
    private val currentUser: User? = Random.nextUser(),
) : AuthRepository {

    var signUserOutSuccessCount: Int = 0 ; private set
    var updateUserSuccessCount: Int = 0 ; private set
    var resetUserPasswordSuccessCount: Int = 0 ; private set
    var updateUserEmailAddressSuccessCount: Int = 0 ; private set
    var deleteUserSuccessCount: Int = 0 ; private set

    override fun getCurrentUser(): User? =
        error?.let { throw it } ?: currentUser

    override fun signUserOut() {
        error?.let { throw it } ?: signUserOutSuccessCount++
    }

    override suspend fun updateUser(name: String, photoUrl: String?) {
        error?.let { throw it } ?: updateUserSuccessCount++
    }

    override suspend fun resetUserPassword() {
        error?.let { throw it } ?: resetUserPasswordSuccessCount++
    }

    override suspend fun updateUserEmailAddress(newEmailAddress: String) {
        error?.let { throw it } ?: updateUserEmailAddressSuccessCount++
    }

    override suspend fun deleteUser() {
        error?.let { throw it } ?: deleteUserSuccessCount++
    }
}