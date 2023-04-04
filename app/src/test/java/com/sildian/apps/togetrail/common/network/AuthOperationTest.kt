package com.sildian.apps.togetrail.common.network

import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.sildian.apps.togetrail.common.core.nextString
import org.junit.Assert
import org.junit.Test
import kotlin.random.Random

class AuthOperationTest {

    @Test(expected = AuthException.InvalidUserException::class)
    fun `GIVEN FirebaseAuthInvalidUserException WHEN invoking authOperation THEN throw AuthInvalidUserException`() {
        // Given
        val exception = FirebaseAuthInvalidUserException(Random.nextString(), Random.nextString())

        // When
        authOperation { throw exception }
    }

    @Test(expected = AuthException.RecentLoginRequiredException::class)
    fun `GIVEN FirebaseAuthRecentLoginRequiredException WHEN invoking authOperation THEN throw AuthRecentLoginRequiredException`() {
        // Given
        val exception = FirebaseAuthRecentLoginRequiredException(Random.nextString(), Random.nextString())

        // When
        authOperation { throw exception }
    }

    @Test(expected = AuthException.InvalidCredentialsException::class)
    fun `GIVEN FirebaseAuthInvalidCredentialsException WHEN invoking authOperation THEN throw AuthInvalidCredentialsException`() {
        // Given
        val exception = FirebaseAuthInvalidCredentialsException(Random.nextString(), Random.nextString())

        // When
        authOperation { throw exception }
    }

    @Test(expected = AuthException.UserCollisionException::class)
    fun `GIVEN FirebaseAuthUserCollisionException WHEN invoking authOperation THEN throw AuthUserCollisionException`() {
        // Given
        val exception = FirebaseAuthUserCollisionException(Random.nextString(), Random.nextString())

        // When
        authOperation { throw exception }
    }

    @Test(expected = AuthException.UnknownException::class)
    fun `GIVEN any exception WHEN invoking authOperation THEN throw UnknownException`() {
        // Given
        val exception = IllegalArgumentException()

        // When
        authOperation { throw exception }
    }

    @Test
    fun `GIVEN user WHEN invoking authOperation THEN return user`() {
        // Given
        val user = Random.nextUser()

        // When
        val result = authOperation { user }

        // Then
        Assert.assertEquals(user, result)
    }
}