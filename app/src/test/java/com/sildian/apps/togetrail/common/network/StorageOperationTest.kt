package com.sildian.apps.togetrail.common.network

import com.google.firebase.storage.StorageException as FirebaseStorageException
import com.sildian.apps.togetrail.common.core.nextUrlString
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.Mockito
import kotlin.random.Random

class StorageOperationTest {

    @Test(expected = StorageException.UnauthenticatedUserException::class)
    fun `GIVEN NotAuthenticated FirebaseStorageException WHEN invoking storageOperation THEN throw UnauthenticatedUserException`() {
        // Given
        val exception = Random.nextFirebaseStorageException(
            errorCode = FirebaseStorageException.ERROR_NOT_AUTHENTICATED
        )

        // When
        storageOperation { throw exception }
    }

    @Test(expected = StorageException.UnauthorizedUserException::class)
    fun `GIVEN NotAuthorized FirebaseStorageException WHEN invoking storageOperation THEN throw UnauthorizedUserException`() {
        // Given
        val exception = Random.nextFirebaseStorageException(
            errorCode = FirebaseStorageException.ERROR_NOT_AUTHORIZED
        )

        // When
        storageOperation { throw exception }
    }

    @Test(expected = StorageException.UnknownException::class)
    fun `GIVEN any exception WHEN invoking storageOperation THEN throw UnknownException`() {
        // Given
        val exception = IllegalArgumentException()

        // When
        storageOperation { throw exception }
    }

    @Test
    fun `GIVEN url WHEN invoking storageOperation THEN return url`() {
        // Given
        val url = Random.nextUrlString()

        // When
        val result = storageOperation { url }

        // Then
        assertEquals(url, result)
    }

    private fun Random.nextFirebaseStorageException(errorCode: Int): FirebaseStorageException =
        Mockito.mock(FirebaseStorageException::class.java).apply {
            Mockito.`when`(this.errorCode).thenReturn(errorCode)
        }
}