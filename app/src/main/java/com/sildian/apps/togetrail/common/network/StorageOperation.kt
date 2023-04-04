package com.sildian.apps.togetrail.common.network

import com.google.firebase.storage.StorageException as FirebaseStorageException

inline fun <T> storageOperation(block: () -> T): T =
    try {
        block()
    } catch (e: Throwable) {
        throw e.toStorageException()
    }

sealed class StorageException(message: String): Exception(message) {
    class UnauthenticatedUserException : StorageException(
        message = "The user is not authenticated. An authentication is required for this operation."
    )
    class UnauthorizedUserException : StorageException(
        message = "The current user is not authorized to perform this operation."
    )
    class UnknownException : StorageException(
        message = "An unknown error occurred."
    )
}

fun Throwable.toStorageException(): StorageException {
    val firebaseStorageException = (this as? FirebaseStorageException)
        ?: return StorageException.UnknownException()
    return when (firebaseStorageException.errorCode) {
        FirebaseStorageException.ERROR_NOT_AUTHENTICATED ->
            StorageException.UnauthenticatedUserException()
        FirebaseStorageException.ERROR_NOT_AUTHORIZED ->
            StorageException.UnauthorizedUserException()
        else ->
            StorageException.UnknownException()
    }
}