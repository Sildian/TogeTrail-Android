package com.sildian.apps.togetrail.common.network

import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.google.firebase.auth.FirebaseAuthUserCollisionException

inline fun <T> authOperation(block: () -> T): T =
    try {
        block()
    } catch (e: Throwable) {
        throw e.toAuthException()
    }

sealed class AuthException(message: String): Exception(message) {
    class InvalidUserException : AuthException(
        message = "The current user's account has been disabled, deleted, or its credentials are no longer valid."
    )
    class RecentLoginRequiredException : AuthException(
        message = "The current user has not been authenticated for a long time. A recent authentication is required for this operation."
    )
    class InvalidCredentialsException : AuthException(
        message = "The provided user email address is invalid."
    )
    class UserCollisionException : AuthException(
        message = "The provided user email address is already taken by an other account."
    )
    class UnknownException : AuthException(
        message = "An unknown error occurred."
    )
}

fun Throwable.toAuthException(): AuthException =
    when (this) {
        is FirebaseAuthInvalidUserException -> AuthException.InvalidUserException()
        is FirebaseAuthRecentLoginRequiredException -> AuthException.RecentLoginRequiredException()
        is FirebaseAuthInvalidCredentialsException -> AuthException.InvalidCredentialsException()
        is FirebaseAuthUserCollisionException -> AuthException.UserCollisionException()
        else -> AuthException.UnknownException()
    }