package com.sildian.apps.togetrail.common.core.location

import com.google.android.libraries.places.api.model.AddressComponent
import com.google.android.libraries.places.api.model.Place

const val PLACE_ADDRESS_COMPONENT_COUNTRY = "country"
const val PLACE_ADDRESS_COMPONENT_ADMIN_AREA_LV1 = "administrative_area_level_1"
const val ADMIN_AREA_NAME_SEPARATOR_1 = ' '
const val ADMIN_AREA_NAME_SEPARATOR_2 = '-'

fun Place.toLocation(): Location =
    Location(
        id = id.orEmpty(),
        country = extractCountry(),
        region = extractRegion(),
        fullAddress = extractFullAddress(),
    )

private fun Place.extractCountry(): Location.Country? =
    addressComponents?.asList()?.firstOrNull { addressComponent ->
        addressComponent?.types?.contains(PLACE_ADDRESS_COMPONENT_COUNTRY)?: false
    }?.takeIf {
        it.shortName.isNullOrBlank().not()
    }?.let { placeCountry ->
        Location.Country(code = placeCountry.extractCountryCode(), name = placeCountry.name)
    }

private fun Place.extractRegion(): Location.Region? =
    addressComponents?.asList()?.firstOrNull { addressComponent ->
        addressComponent?.types?.contains(PLACE_ADDRESS_COMPONENT_ADMIN_AREA_LV1)?: false
    }?.takeIf {
        it.shortName.isNullOrBlank().not()
    }?.let { placeAdminAreaLv1 ->
        Location.Region(code = placeAdminAreaLv1.extractRegionCode(), name = placeAdminAreaLv1.name)
    }

private fun Place.extractFullAddress(): String? = address

private fun AddressComponent.extractCountryCode(): String = shortName.orEmpty()

private fun AddressComponent.extractRegionCode(): String =
    when {
        shortName != name ->
            shortName.orEmpty()
        name.contains(ADMIN_AREA_NAME_SEPARATOR_1) || name.contains(ADMIN_AREA_NAME_SEPARATOR_2) -> {
            val separator = name.find {
                it == ADMIN_AREA_NAME_SEPARATOR_1 || it == ADMIN_AREA_NAME_SEPARATOR_2
            }?.toString().orEmpty()
            name.take(1).plus(name.substringAfter(separator).take(1))
        }
        else ->
            name.take(2)
    }