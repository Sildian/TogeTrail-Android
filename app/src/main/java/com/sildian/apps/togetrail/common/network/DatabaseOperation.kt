package com.sildian.apps.togetrail.common.network

import com.google.firebase.firestore.FirebaseFirestoreException

inline fun <T> databaseOperation(block: () -> T): T =
    try {
        block()
    } catch (e: Throwable) {
        throw e.toDatabaseException()
    }

sealed class DatabaseException(message: String): Exception(message) {
    class UnauthenticatedUserException : DatabaseException(
        message = "The user is not authenticated. An authentication is required for this operation."
    )
    class UnauthorizedUserException : DatabaseException(
        message = "The current user is not authorized to perform this operation."
    )
    class NotFoundDocumentException : DatabaseException(
        message = "The requested document was not found in the database."
    )
    class AlreadyExistDocumentException : DatabaseException(
        message = "The document to be created already exists in the database."
    )
    class UnknownException : DatabaseException(
        message = "An unknown error occurred."
    )
}

fun Throwable.toDatabaseException(): DatabaseException {
    val firebaseFirestoreException = (this as? FirebaseFirestoreException)
        ?: return DatabaseException.UnknownException()
    return when (firebaseFirestoreException.code) {
        FirebaseFirestoreException.Code.UNAUTHENTICATED ->
            DatabaseException.UnauthenticatedUserException()
        FirebaseFirestoreException.Code.PERMISSION_DENIED ->
            DatabaseException.UnauthorizedUserException()
        FirebaseFirestoreException.Code.NOT_FOUND ->
            DatabaseException.NotFoundDocumentException()
        FirebaseFirestoreException.Code.ALREADY_EXISTS ->
            DatabaseException.AlreadyExistDocumentException()
        else ->
            DatabaseException.UnknownException()
    }
}