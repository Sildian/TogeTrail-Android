package com.sildian.apps.togetrail.common.core.geo

import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.GeoPoint
import kotlinx.android.parcel.Parcelize
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin

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

fun Position.distanceTo(position: Position): Distance {
    val earthRadius = 6371.0
    val arg1: Double = sin(Math.toRadians(latitude)) * sin(Math.toRadians(position.latitude))
    val arg2: Double = cos(Math.toRadians(latitude)) * cos(Math.toRadians(position.latitude)) *
            cos(Math.toRadians(position.longitude - longitude))
    val distanceInKMeters: Double = earthRadius * acos(arg1 + arg2)
    return Distance(meters = (distanceInKMeters * 1000).toInt())
}

fun Position.derivationTo(position: Position): Derivation =
    altitude.derivationTo(altitude = position.altitude)

fun Position.derivationFrom(position: Position): Derivation =
    altitude.derivationFrom(altitude = position.altitude)

fun GeoPoint.toPosition(): Position =
    Position(
        latitude = latitude,
        longitude = longitude,
        altitude = Altitude(meters = 0),
    )

fun Position.toGePoint(): GeoPoint =
    GeoPoint(latitude, longitude)

fun LatLng.toPosition(): Position =
    Position(
        latitude = latitude,
        longitude = longitude,
        altitude = Altitude(meters = 0),
    )

fun Position.toLatLng(): LatLng =
    LatLng(latitude, longitude)