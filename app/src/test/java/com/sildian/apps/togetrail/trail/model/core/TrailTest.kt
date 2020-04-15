package com.sildian.apps.togetrail.trail.model.core

import com.sildian.apps.togetrail.location.model.core.Country
import com.sildian.apps.togetrail.location.model.core.Location
import org.junit.Test

import org.junit.Assert.*

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
}