package com.sildian.apps.togetrail.hiker.data.helpers

import com.google.firebase.auth.FirebaseUser
import org.junit.Assert.*
import org.junit.Test
import org.mockito.Mockito

class HikerBuilderTest{

    @Test
    fun given_toto_when_buildFromFirebaseUser_then_checkResultedHikerIsToto(){
        val firebaseUser= Mockito.mock(FirebaseUser::class.java)
        Mockito.`when`(firebaseUser.uid).thenReturn("USERTOTO")
        Mockito.`when`(firebaseUser.email).thenReturn("toto@toto.com")
        Mockito.`when`(firebaseUser.displayName).thenReturn("Toto")
        val hiker=HikerBuilder()
            .withFirebaseUser(firebaseUser)
            .build()
        assertEquals("USERTOTO", hiker.id)
        assertEquals("toto@toto.com", hiker.email)
        assertEquals("Toto", hiker.name)
    }
}