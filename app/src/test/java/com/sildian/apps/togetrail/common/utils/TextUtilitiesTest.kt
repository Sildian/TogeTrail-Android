package com.sildian.apps.togetrail.common.utils

import org.junit.Assert.*
import org.junit.Test

class TextUtilitiesTest {

    @Test
    fun given_emptyEmail_when_isValidEmail_then_returnFalse() {
        assertFalse("".isValidEmail())
    }

    @Test
    fun given_BlankEmail_when_isValidEmail_then_returnFalse() {
        assertFalse(" ".isValidEmail())
    }

    @Test
    fun given_invalidEmail_when_isValidEmail_then_returnFalse() {
        assertFalse("toto".isValidEmail())
    }

    @Test
    fun given_validEmail_when_isValidEmail_then_returnTrue() {
        assertTrue("toto@gmail.com".isValidEmail())
    }
}