package com.sildian.apps.togetrail.common.network

import com.google.firebase.firestore.FirebaseFirestoreException
import com.sildian.apps.togetrail.common.core.nextString
import org.junit.Assert
import org.junit.Test
import org.mockito.Mockito
import kotlin.random.Random

class DatabaseOperationTest {

    @Test(expected = DatabaseException.UnauthenticatedUserException::class)
    fun `GIVEN Unauthenticated FirebaseFirestoreException WHEN invoking databaseOperation THEN throw UnauthenticatedUserException`() {
        // Given
        val exception = Random.nextFirebaseFirestoreException(
            code = FirebaseFirestoreException.Code.UNAUTHENTICATED
        )

        // When
        databaseOperation { throw exception }
    }

    @Test(expected = DatabaseException.UnauthorizedUserException::class)
    fun `GIVEN PermissionDenied FirebaseFirestoreException WHEN invoking databaseOperation THEN throw UnauthorizedUserException`() {
        // Given
        val exception = Random.nextFirebaseFirestoreException(
            code = FirebaseFirestoreException.Code.PERMISSION_DENIED
        )

        // When
        databaseOperation { throw exception }
    }

    @Test(expected = DatabaseException.NotFoundDocumentException::class)
    fun `GIVEN NotFound FirebaseFirestoreException WHEN invoking databaseOperation THEN throw NotFoundDocumentException`() {
        // Given
        val exception = Random.nextFirebaseFirestoreException(
            code = FirebaseFirestoreException.Code.NOT_FOUND
        )

        // When
        databaseOperation { throw exception }
    }

    @Test(expected = DatabaseException.AlreadyExistDocumentException::class)
    fun `GIVEN AlreadyExist FirebaseFirestoreException WHEN invoking databaseOperation THEN throw AlreadyExistDocumentException`() {
        // Given
        val exception = Random.nextFirebaseFirestoreException(
            code = FirebaseFirestoreException.Code.ALREADY_EXISTS
        )

        // When
        databaseOperation { throw exception }
    }

    @Test(expected = DatabaseException.UnknownException::class)
    fun `GIVEN any exception WHEN invoking databaseOperation THEN throw UnknownException`() {
        // Given
        val exception = IllegalArgumentException()

        // When
        databaseOperation { throw exception }
    }

    @Test
    fun `GIVEN data WHEN invoking databaseOperation THEN return data`() {
        // Given
        val data = Random.nextString()

        // When
        val result = databaseOperation { data }

        // Then
        Assert.assertEquals(data, result)
    }

    private fun Random.nextFirebaseFirestoreException(
        code: FirebaseFirestoreException.Code = FirebaseFirestoreException.Code.values().random(),
    ): FirebaseFirestoreException =
        Mockito.mock(FirebaseFirestoreException::class.java).apply {
            Mockito.`when`(this.code).thenReturn(code)
        }
}