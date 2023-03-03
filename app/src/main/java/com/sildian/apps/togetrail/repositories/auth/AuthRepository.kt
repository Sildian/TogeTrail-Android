package com.sildian.apps.togetrail.repositories.auth

import com.sildian.apps.togetrail.common.network.User

interface AuthRepository {
    fun getCurrentUser(): User?
    fun signUserOut()
    suspend fun updateUser(name: String, photoUrl: String?)
    suspend fun resetUserPassword()
    suspend fun updateUserEmailAddress(newEmailAddress: String)
    suspend fun deleteUser()
}