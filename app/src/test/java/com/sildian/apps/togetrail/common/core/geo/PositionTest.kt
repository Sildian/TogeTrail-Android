package com.sildian.apps.togetrail.common.core.geo

import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.GeoPoint
import org.junit.Assert.*
import org.junit.Test
import kotlin.random.Random

class PositionTest {

    @Test(expected = IllegalArgumentException::class)
    fun `GIVEN latitude out of range WHEN initializing position THEN throw IllegalArgumentException`() {
        //Given
        val latitude = listOf(
            Random.nextDouble(from = -Double.MAX_VALUE, until = -90.0),
            Random.nextDouble(from = 91.0, until = Double.MAX_VALUE)
        ).random()
        val longitude = Random.nextLongitude()

        //When
        Position(latitude = latitude, longitude = longitude, altitude = Random.nextAltitude())
    }

    @Test(expected = IllegalArgumentException::class)
    fun `GIVEN longitude out of range WHEN initializing position THEN throw IllegalArgumentException`() {
        //Given
        val latitude = Random.nextLatitude()
        val longitude = listOf(
            Random.nextDouble(from = -Double.MAX_VALUE, until = -180.0),
            Random.nextDouble(from = 181.0, until = Double.MAX_VALUE)
        ).random()

        //When
        Position(latitude = latitude, longitude = longitude, altitude = Random.nextAltitude())
    }

    @Test
    fun `GIVEN valid lat lng WHEN initializing position THEN result is position with correct lat lng`() {
        //Given
        val latitude = Random.nextLatitude()
        val longitude = Random.nextLongitude()

        //When
        val position = Random.nextPosition(latitude = latitude, longitude = longitude)

        //Then
        assertEquals(latitude, position.latitude, 0.0)
        assertEquals(longitude, position.longitude, 0.0)
    }

    @Test
    fun `GIVEN position WHEN invoking toString THEN result is lat lng`() {
        //Given
        val position = Random.nextPosition()

        //When
        val display = position.toString()

        //Then
        val expectedResult = "${position.latitude} ; ${position.longitude}"
        assertEquals(expectedResult, display)
    }

    @Test
    fun `GIVEN two positions WHEN invoking distanceTo THEN result is distance between the two positions`() {
        //Given
        val positionA = Random.nextPosition(latitude = 40.0, longitude = -5.0)
        val positionB = Random.nextPosition(latitude = 41.261388, longitude = -3.3125)

        //When
        val distance = positionA.distanceTo(positionB)

        //Then
        val expectedResult = Distance(meters = 199872)
        assertEquals(expectedResult, distance)
    }

    @Test
    fun `GIVEN positions A and B WHEN invoking derivationTo THEN result is derivation from A to B`() {
        //Given
        val positionA = Random.nextPosition(altitude = Random.nextAltitude())
        val positionB = Random.nextPosition(altitude = Random.nextAltitude())

        //When
        val derivation = positionA.derivationTo(position = positionB)

        //Then
        val expectedResult = Derivation(meters = positionB.altitude.meters - positionA.altitude.meters)
        assertEquals(expectedResult, derivation)
    }

    @Test
    fun `GIVEN positions A and B WHEN invoking derivationFrom THEN result is derivation from B to A`() {
        //Given
        val positionA = Random.nextPosition(altitude = Random.nextAltitude())
        val positionB = Random.nextPosition(altitude = Random.nextAltitude())

        //When
        val derivation = positionA.derivationFrom(position = positionB)

        //Then
        val expectedResult = Derivation(meters = positionA.altitude.meters - positionB.altitude.meters)
        assertEquals(expectedResult, derivation)
    }

    @Test
    fun `GIVEN geoPoint WHEN mapping toPosition THEN result is position with correct lat lng`() {
        //Given
        val latitude = Random.nextLatitude()
        val longitude = Random.nextLongitude()
        val geoPoint = GeoPoint(latitude, longitude)

        //When
        val position = geoPoint.toPosition()

        //Then
        val expectedResult = Position(
            latitude = latitude,
            longitude = longitude,
            altitude = Altitude(meters = 0)
        )
        assertEquals(expectedResult, position)
    }

    @Test
    fun `GIVEN position WHEN mapping toGeoPoint THEN result is geoPoint with correct lat lng`() {
        //Given
        val latitude = Random.nextLatitude()
        val longitude = Random.nextLongitude()
        val position = Position(
            latitude = latitude,
            longitude = longitude,
            altitude = Random.nextAltitude()
        )

        //When
        val geoPoint = position.toGePoint()

        //Then
        val expectedResult = GeoPoint(latitude, longitude)
        assertEquals(expectedResult, geoPoint)
    }

    @Test
    fun `GIVEN latLng WHEN mapping toPosition THEN result is position with correct lat lng`() {
        //Given
        val latitude = Random.nextLatitude()
        val longitude = Random.nextLongitude()
        val latLng = LatLng(latitude, longitude)

        //When
        val position = latLng.toPosition()

        //Then
        val expectedResult = Position(
            latitude = latitude,
            longitude = longitude,
            altitude = Altitude(meters = 0)
        )
        assertEquals(expectedResult, position)
    }

    @Test
    fun `GIVEN position WHEN mapping toLatLng THEN result is latLng with correct lat lng`() {
        //Given
        val latitude = Random.nextLatitude()
        val longitude = Random.nextLongitude()
        val position = Position(
            latitude = latitude,
            longitude = longitude,
            altitude = Random.nextAltitude()
        )

        //When
        val latLng = position.toLatLng()

        //Then
        val expectedResult = LatLng(latitude, longitude)
        assertEquals(expectedResult, latLng)
    }
}