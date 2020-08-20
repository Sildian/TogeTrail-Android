package com.sildian.apps.togetrail.trail.model.core

import android.os.Parcel
import com.google.firebase.firestore.GeoPoint
import com.sildian.apps.togetrail.location.model.core.Country
import com.sildian.apps.togetrail.location.model.core.Location
import com.sildian.apps.togetrail.location.model.core.Region
import org.junit.Test

import org.junit.Assert.*
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class TrailTest {

    @Test
    fun given_validTrail_when_isDataValid_then_checkResultIsTrue(){
        val trail=Trail(
            name="Super Trail",
            location = Location(country = Country("FR", "France")),
            level=TrailLevel.MEDIUM
        )
        assertTrue(trail.isDataValid())
    }

    @Test
    fun given_nonValidTrail_when_isDataValid_then_checkResultIsFalse(){
        val trail=Trail(
            name="Super Trail"
        )
        assertFalse(trail.isDataValid())
    }

    @Test
    fun given_photosUrls_when_getAllPhotosUrls_then_checkResultContainsAllPhotosUrls() {
        val photo1="https://photo1.png"
        val photo2="https://photo2.png"
        val photo3="https://photo3.png"
        val trail=Trail(mainPhotoUrl = photo1)
        val poi1=TrailPointOfInterest(photoUrl = photo2)
        val poi2=TrailPointOfInterest(photoUrl = photo3)
        trail.trailTrack.trailPointsOfInterest.addAll(listOf(poi1, poi2))
        val photos=trail.getAllPhotosUrls()
        assertEquals(listOf(photo1, photo2, photo3), photos)
    }

    @Test
    fun given_photosUrlsToTrailAndPOI_when_getFirstPhotoUrl_then_checkResultContainsTrailPhotoUrl() {
        val photo1="https://photo1.png"
        val photo2="https://photo2.png"
        val photo3="https://photo3.png"
        val trail=Trail(mainPhotoUrl = photo1)
        val poi1=TrailPointOfInterest(photoUrl = photo2)
        val poi2=TrailPointOfInterest(photoUrl = photo3)
        trail.trailTrack.trailPointsOfInterest.addAll(listOf(poi1, poi2))
        val photo=trail.getFirstPhotoUrl()
        assertEquals(photo1, photo)
    }

    @Test
    fun given_photosUrlsToLastPOI_when_getFirstPhotoUrl_then_checkResultContainsLastPOIPhotoUrl() {
        val photo1="https://photo1.png"
        val trail=Trail()
        val poi1=TrailPointOfInterest()
        val poi2=TrailPointOfInterest(photoUrl = photo1)
        trail.trailTrack.trailPointsOfInterest.addAll(listOf(poi1, poi2))
        val photo=trail.getFirstPhotoUrl()
        assertEquals(photo1, photo)
    }

    @Test
    fun given_trail_when_createFromParcel_then_checkResultIsEqual(){
        val parcel= Parcel.obtain()
        val trail1=Trail(
            id="Trail1",
            name="Super Trail",
            source="TogeTrail",
            position= GeoPoint(50.0, 50.0),
            location= Location(
                Country("TOL", "Totoland"),
                Region("TOR", "Totoregion"),
                "Toto City"
            ),
            duration=60,
            distance=1000,
            ascent=500,
            descent=300,
            maxElevation = 1500,
            minElevation = 1100,
            authorId = "Hiker1"
        )
        trail1.writeToParcel(parcel, trail1.describeContents())
        parcel.setDataPosition(0)

        val trail2=Trail.create(parcel)

        assertEquals(trail1, trail2)
    }
}