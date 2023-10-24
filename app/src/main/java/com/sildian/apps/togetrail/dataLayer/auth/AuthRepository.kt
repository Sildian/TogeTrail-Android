package com.sildian.apps.togetrail.dataLayer.auth

import com.sildian.apps.togetrail.common.network.AuthException
import com.sildian.apps.togetrail.common.network.User

interface AuthRepository {
    @Throws(AuthException::class, IllegalStateException::class)
    fun getCurrentUser(): User?
    @Throws(AuthException::class)
    fun signUserOut()
    @Throws(AuthException::class)
    suspend fun updateUser(name: String, photoUrl: String?)
    @Throws(AuthException::class)
    suspend fun resetUserPassword()
    @Throws(AuthException::class)
    suspend fun updateUserEmailAddress(newEmailAddress: String)
    @Throws(AuthException::class)
    suspend fun deleteUser()
}