package com.sildian.apps.togetrail.common.core.geo

import android.os.Parcelable
import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.google.android.gms.maps.model.LatLng
import kotlinx.parcelize.Parcelize

@Parcelize
data class Position(
    val latitude: Double,
    val longitude: Double,
    val altitude: Altitude,
) : Parcelable {

    init {
        if (latitude !in -90.0..90.0) {
            throw IllegalArgumentException(
                "Provided latitude ($latitude) should be in the range of [-90, 90]"
            )
        }
        if (longitude !in -180.0..180.0) {
            throw IllegalArgumentException(
                "Provided longitude ($longitude) should be in the range of [-180, 180]"
            )
        }
    }

    override fun toString(): String =
        "$latitude ; $longitude"
}

fun Position.distanceTo(position: Position): Distance =
    Distance(
        meters = GeoFireUtils.getDistanceBetween(
            this.toGeoLocation(),
            position.toGeoLocation()
        ).toInt()
    )

fun Position.derivationTo(position: Position): Derivation =
    altitude.derivationTo(altitude = position.altitude)

fun Position.derivationFrom(position: Position): Derivation =
    altitude.derivationFrom(altitude = position.altitude)

fun GeoLocation.toPosition(): Position =
    Position(
        latitude = latitude,
        longitude = longitude,
        altitude = Altitude(meters = 0),
    )

fun Position.toGeoLocation(): GeoLocation =
    GeoLocation(latitude, longitude)

fun Position.toGeoHash(): String =
    GeoFireUtils.getGeoHashForLocation(this.toGeoLocation())

fun LatLng.toPosition(): Position =
    Position(
        latitude = latitude,
        longitude = longitude,
        altitude = Altitude(meters = 0),
    )

fun Position.toLatLng(): LatLng =
    LatLng(latitude, longitude)