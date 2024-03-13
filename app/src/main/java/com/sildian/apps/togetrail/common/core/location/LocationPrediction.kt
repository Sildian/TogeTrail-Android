package com.sildian.apps.togetrail.common.core.location

import android.os.Parcelable
import com.google.android.libraries.places.api.model.AutocompletePrediction
import kotlinx.parcelize.Parcelize

@Parcelize
data class LocationPrediction(
    val id: String,
    val name: String,
) : Parcelable

fun AutocompletePrediction.toLocationPrediction(): LocationPrediction =
    LocationPrediction(
        id = placeId,
        name = getFullText(null).toString()
    )