package com.sildian.apps.togetrail.common.network

import android.os.Parcelable
import com.google.firebase.auth.FirebaseUser
import kotlinx.parcelize.Parcelize
import kotlin.jvm.Throws

@Parcelize
data class User(
    val id: String,
    val name: String,
    val emailAddress: String,
    val photoUrl: String?,
) : Parcelable

@Throws(IllegalStateException::class)
fun FirebaseUser.toUser(): User {
    val id = uid
    val name = displayName ?: throw IllegalStateException("User name should be provided")
    val emailAddress = email ?: throw IllegalStateException("User email address should be provided")
    val photoUrl = photoUrl?.toString()
    return User(
        id = id,
        name = name,
        emailAddress = emailAddress,
        photoUrl = photoUrl,
    )
}