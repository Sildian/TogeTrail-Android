package com.sildian.apps.togetrail.hiker.model.core

import com.sildian.apps.togetrail.common.utils.DateUtilities
import org.junit.Test

import org.junit.Assert.*

class HikerTest {

    @Test
    fun given_toto_when_getAge_then_checkAgeIs34() {
        val birthDate=DateUtilities.getDate(1985, 6, 20)
        val hiker=Hiker(name="Toto", birthDate = birthDate)
        val currentDate=DateUtilities.getDate(2020, 1, 16)
        assertEquals(34, hiker.getAge(currentDate))
    }
}