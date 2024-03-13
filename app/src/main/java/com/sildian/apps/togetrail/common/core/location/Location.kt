package com.sildian.apps.togetrail.common.core.location

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Location(
    val id: String,
    val country: Country? = null,
    val region: Region? = null,
    val fullAddress: String? = null,
) : Parcelable {

    override fun toString(): String =
        when {
            country != null && region != null ->
                "$region, $country"
            country != null ->
                "$country"
            else ->
                fullAddress.orEmpty()
        }

    @Parcelize
    data class Country(
        val code: String? = null,
        val name: String? = null,
    ) : Parcelable {

        override fun toString(): String = name.orEmpty()

        override fun equals(other: Any?): Boolean =
            other is Country && other.code == code

        override fun hashCode(): Int =
            code.hashCode()
    }

    @Parcelize
    data class Region(
        val code: String? = null,
        val name: String? = null,
    ) : Parcelable {

        override fun toString(): String = name.orEmpty()

        override fun equals(other: Any?): Boolean =
            other is Region && other.code == code

        override fun hashCode(): Int =
            code.hashCode()
    }
}