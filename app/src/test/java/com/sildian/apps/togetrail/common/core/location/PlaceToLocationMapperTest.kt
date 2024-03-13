package com.sildian.apps.togetrail.common.core.location

import com.google.android.libraries.places.api.model.AddressComponent
import com.google.android.libraries.places.api.model.AddressComponents
import com.google.android.libraries.places.api.model.Place
import com.sildian.apps.togetrail.common.utils.nextAlphaString
import com.sildian.apps.togetrail.common.utils.nextString
import junit.framework.TestCase.assertEquals
import org.junit.Test
import org.mockito.Mockito
import kotlin.random.Random

class PlaceToLocationMapperTest {

    @Test
    fun `GIVEN place with invalid country WHEN invoking toLocation THEN result is location without country`() {
        // Given
        val placeCountry = Random.nextPlaceCountry(shortName = "")
        val place = Random.nextPlace(
            country = placeCountry,
            adminAreaLv1 = null,
            address = null
        )

        // When
        val location = place.toLocation()

        // Then
        val expectedResult = Location(id = place.id!!, country = null)
        assertEquals(expectedResult, location)
    }

    @Test
    fun `GIVEN place with country WHEN invoking toLocation THEN result is location with country`() {
        // Given
        val placeCountry = Random.nextPlaceCountry()
        val place = Random.nextPlace(
            country = placeCountry,
            adminAreaLv1 = null,
            address = null
        )

        // When
        val location = place.toLocation()

        // Then
        val expectedResult = Location(
            id = place.id!!,
            country = Location.Country(code = placeCountry.shortName.orEmpty(), name = placeCountry.name)
        )
        assertEquals(expectedResult, location)
    }

    @Test
    fun `GIVEN place with invalid adminAreaLv1 WHEN invoking toLocation THEN result is location without region`() {
        // Given
        val placeCountry = Random.nextPlaceCountry(shortName = "")
        val place = Random.nextPlace(
            country = placeCountry,
            adminAreaLv1 = null,
            address = null
        )

        // When
        val location = place.toLocation()

        // Then
        val expectedResult = Location(id = place.id!!, region = null)
        assertEquals(expectedResult, location)
    }

    @Test
    fun `GIVEN place with adminAreaLv1 with valid shortName WHEN invoking toLocation THEN result is location with region`() {
        // Given
        val placeAdminAreaLv1 = Random.nextPlaceAdminAreaLv1(
            shortName = Random.nextAlphaString(size = 2),
            name = Random.nextString(size = 10),
        )
        val place = Random.nextPlace(
            country = null,
            adminAreaLv1 = placeAdminAreaLv1,
            address = null
        )

        // When
        val location = place.toLocation()

        // Then
        val expectedResult = Location(
            id = place.id!!,
            region = Location.Region(code = placeAdminAreaLv1.shortName.orEmpty(), name = placeAdminAreaLv1.name),
        )
        assertEquals(expectedResult, location)
    }

    @Test
    fun `GIVEN place with adminAreaLv1 with shortName equals name and has separator WHEN invoking toLocation THEN result is location with region with formatted code composed of the two first chars of each name part`() {
        // Given
        val nameParts = Pair(
            Random.nextString(size = Random.nextInt(from = 2, until = 10)),
            Random.nextString(size = Random.nextInt(from = 2, until = 10)),
        )
        val nameSeparator = listOf(ADMIN_AREA_NAME_SEPARATOR_1, ADMIN_AREA_NAME_SEPARATOR_2).random()
        val name = "${nameParts.first}$nameSeparator${nameParts.second}"
        val placeAdminAreaLv1 = Random.nextPlaceAdminAreaLv1(
            shortName = name,
            name = name,
        )
        val place = Random.nextPlace(
            country = null,
            adminAreaLv1 = placeAdminAreaLv1,
            address = null
        )

        // When
        val location = place.toLocation()

        // Then
        val expectedRegionCode = "${nameParts.first.take(1)}${nameParts.second.take(1)}"
        val expectedResult = Location(
            id = place.id!!,
            region = Location.Region(code = expectedRegionCode, name = placeAdminAreaLv1.name),
        )
        assertEquals(expectedResult, location)
    }

    @Test
    fun `GIVEN place with adminAreaLv1 with shortName equals name and has no separator WHEN invoking toLocation THEN result is location with region with formatted code composed of the two first characters of the name`() {
        // Given
        val name = Random.nextString(size = Random.nextInt(from = 2, until = 20))
        val placeAdminAreaLv1 = Random.nextPlaceAdminAreaLv1(
            shortName = name,
            name = name,
        )
        val place = Random.nextPlace(
            country = null,
            adminAreaLv1 = placeAdminAreaLv1,
            address = null
        )

        // When
        val location = place.toLocation()

        // Then
        val expectedRegionCode = name.take(2)
        val expectedResult = Location(
            id = place.id!!,
            region = Location.Region(code = expectedRegionCode, name = placeAdminAreaLv1.name),
        )
        assertEquals(expectedResult, location)
    }

    @Test
    fun `GIVEN place with address WHEN invoking toLocation THEN result is location with fullAddress`() {
        // Given
        val address = Random.nextString()
        val place = Random.nextPlace(
            country = null,
            adminAreaLv1 = null,
            address = address
        )

        // When
        val location = place.toLocation()

        // Then
        val expectedResult = Location(id = place.id!!, fullAddress = address)
        assertEquals(expectedResult, location)
    }

    private fun Random.nextPlace(
        id: String? = nextString(),
        country: AddressComponent? = nextPlaceCountry(),
        adminAreaLv1: AddressComponent? = nextPlaceAdminAreaLv1(),
        address: String? = nextString(),
    ): Place {
        val addressComponents = Mockito.mock(AddressComponents::class.java).apply {
            Mockito.`when`(asList()).thenReturn(listOf(country, adminAreaLv1))
        }
        return Mockito.mock(Place::class.java).apply {
            Mockito.`when`(this.id).thenReturn(id)
            Mockito.`when`(this.address).thenReturn(address)
            Mockito.`when`(this.addressComponents).thenReturn(addressComponents)
        }
    }

    private fun Random.nextPlaceCountry(
        shortName: String = nextAlphaString(size = 2),
        name: String = nextAlphaString(),
    ): AddressComponent =
        Mockito.mock(AddressComponent::class.java).apply {
            Mockito.`when`(this.types).thenReturn(listOf(PLACE_ADDRESS_COMPONENT_COUNTRY))
            Mockito.`when`(this.shortName).thenReturn(shortName)
            Mockito.`when`(this.name).thenReturn(name)
        }

    private fun Random.nextPlaceAdminAreaLv1(
        shortName: String = nextAlphaString(size = 2),
        name: String = nextAlphaString(),
    ): AddressComponent =
        Mockito.mock(AddressComponent::class.java).apply {
            Mockito.`when`(this.types).thenReturn(listOf(PLACE_ADDRESS_COMPONENT_ADMIN_AREA_LV1))
            Mockito.`when`(this.shortName).thenReturn(shortName)
            Mockito.`when`(this.name).thenReturn(name)
        }
}