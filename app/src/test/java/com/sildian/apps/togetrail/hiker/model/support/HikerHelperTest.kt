package com.sildian.apps.togetrail.hiker.model.support

import com.google.firebase.auth.FirebaseUser
import org.junit.Assert.*
import org.junit.Test
import org.mockito.Mockito

class HikerHelperTest{

    @Test
    fun given_toto_when_buildFromFirebaseUser_then_checkResultedHikerIsToto(){
        val firebaseUser= Mockito.mock(FirebaseUser::class.java)
        Mockito.`when`(firebaseUser.uid).thenReturn("USERTOTO")
        Mockito.`when`(firebaseUser.email).thenReturn("toto@toto.com")
        Mockito.`when`(firebaseUser.displayName).thenReturn("Toto")
        val hiker=HikerHelper.buildFromFirebaseUser(firebaseUser)
        assertEquals("USERTOTO", hiker.id)
        assertEquals("toto@toto.com", hiker.email)
        assertEquals("Toto", hiker.name)
    }
}