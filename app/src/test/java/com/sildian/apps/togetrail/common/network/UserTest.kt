package com.sildian.apps.togetrail.common.network

import android.net.Uri
import com.google.firebase.auth.FirebaseUser
import com.sildian.apps.togetrail.common.core.nextAlphaString
import com.sildian.apps.togetrail.common.core.nextEmailAddressString
import com.sildian.apps.togetrail.common.core.nextUrlString
import org.junit.Assert.*
import org.junit.Test
import org.mockito.Mockito
import kotlin.random.Random

class UserTest {

    @Test(expected = IllegalStateException::class)
    fun `GIVEN firebaseUser without displayName WHEN invoking toUser THEN throws IllegalStateException`() {
        // Given
        val firebaseUser = Random.nextFirebaseUser(displayName = null)

        // When
        firebaseUser.toUser()
    }

    @Test(expected = IllegalStateException::class)
    fun `GIVEN firebaseUser without email WHEN invoking toUser THEN throws IllegalStateException`() {
        // Given
        val firebaseUser = Random.nextFirebaseUser(email = null)

        // When
        firebaseUser.toUser()
    }

    @Test
    fun `GIVEN valid firebaseUser WHEN invoking toUser THEN return user `() {
        // Given
        val firebaseUser = Random.nextFirebaseUser()

        // When
        val user = firebaseUser.toUser()

        // Then
        val expectedResult = User(
            name = firebaseUser.displayName!!,
            emailAddress = firebaseUser.email!!,
            photoUrl = firebaseUser.photoUrl?.toString(),
        )
        assertEquals(expectedResult, user)
    }

    private fun Random.nextFirebaseUser(
        displayName: String? = nextAlphaString(),
        email: String? = nextEmailAddressString(),
        photoUrl: String? = nextUrlString(),
    ): FirebaseUser =
        Mockito.mock(FirebaseUser::class.java).apply {
            Mockito.`when`(this.displayName).thenReturn(displayName)
            Mockito.`when`(this.email).thenReturn(email)
            Mockito.`when`(this.photoUrl).thenReturn(Uri.parse(photoUrl))
        }
}