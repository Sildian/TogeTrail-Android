package com.sildian.apps.togetrail.repositories.auth

interface AuthRepository {
    fun getCurrentUser(): User?
    fun signUserOut()
    suspend fun updateUser(name: String, photoUrl: String?)
    suspend fun resetUserPassword()
    suspend fun updateUserEmailAddress(newEmailAddress: String)
    suspend fun deleteUser()
}