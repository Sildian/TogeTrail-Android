package com.sildian.apps.togetrail.common.network

import android.os.Parcelable
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
    val name: String,
    val emailAddress: String,
    val photoUrl: String?,
) : Parcelable

fun FirebaseUser.toUser(): User {
    val name = displayName ?: throw IllegalStateException("Provided User name should not be null")
    val emailAddress = email ?: throw IllegalStateException("Provided User email address should not be null")
    val photoUrl = photoUrl?.toString()
    return User(
        name = name,
        emailAddress = emailAddress,
        photoUrl = photoUrl,
    )
}