package com.sildian.apps.togetrail.common.core.geo

import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.google.android.gms.maps.model.LatLng
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
        val positionA = Random.nextPosition()
        val positionB = Random.nextPosition()

        //When
        val distance = positionA.distanceTo(positionB)

        //Then
        val geoLocationA = GeoLocation(positionA.latitude, positionA.longitude)
        val geoLocationB = GeoLocation(positionB.latitude, positionB.longitude)
        val expectedResult = Distance(
            meters = GeoFireUtils.getDistanceBetween(geoLocationA, geoLocationB).toInt()
        )
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
    fun `GIVEN geoLocation WHEN mapping toPosition THEN result is position with correct lat lng`() {
        //Given
        val latitude = Random.nextLatitude()
        val longitude = Random.nextLongitude()
        val geoLocation = GeoLocation(latitude, longitude)

        //When
        val position = geoLocation.toPosition()

        //Then
        val expectedResult = Position(
            latitude = latitude,
            longitude = longitude,
            altitude = Altitude(meters = 0)
        )
        assertEquals(expectedResult, position)
    }

    @Test
    fun `GIVEN position WHEN mapping toGeoLocation THEN result is geoLocation with correct lat lng`() {
        //Given
        val latitude = Random.nextLatitude()
        val longitude = Random.nextLongitude()
        val position = Position(
            latitude = latitude,
            longitude = longitude,
            altitude = Random.nextAltitude()
        )

        //When
        val geoLocation = position.toGeoLocation()

        //Then
        val expectedResult = GeoLocation(latitude, longitude)
        assertEquals(expectedResult, geoLocation)
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