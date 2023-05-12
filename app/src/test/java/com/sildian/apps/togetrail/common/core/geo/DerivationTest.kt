package com.sildian.apps.togetrail.common.core.geo

import org.junit.Assert.*
import org.junit.Test
import java.text.NumberFormat
import kotlin.random.Random

class DerivationTest {

    @Test
    fun `GIVEN derivation WHEN invoking toString THEN result is formatted string with meters`() {
        //Given
        val derivation = Random.nextDerivation()

        //When
        val display = derivation.toString()

        //Then
        val expectedResult = NumberFormat.getInstance().format(derivation.meters) + " m"
        assertEquals(expectedResult, display)
    }

    @Test
    fun `GIVEN two derivations WHEN invoking plus operator THEN result is sum of the two derivations`() {
        //Given
        val derivationA = Random.nextDerivation()
        val derivationB = Random.nextDerivation()

        //When
        val result = derivationA + derivationB

        //Then
        val expectedResult = Derivation(meters = derivationA.meters + derivationB.meters)
        assertEquals(expectedResult, result)
    }

    @Test
    fun `GIVEN two derivations WHEN invoking minus operator THEN result is difference between the two derivations`() {
        //Given
        val derivationA = Random.nextDerivation()
        val derivationB = Random.nextDerivation()

        //When
        val result = derivationA - derivationB

        //Then
        val expectedResult = Derivation(meters = derivationA.meters - derivationB.meters)
        assertEquals(expectedResult, result)
    }
}