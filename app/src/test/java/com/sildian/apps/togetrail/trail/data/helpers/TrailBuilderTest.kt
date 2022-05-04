package com.sildian.apps.togetrail.trail.data.helpers

import android.content.Context
import io.ticofab.androidgpxparser.parser.GPXParser
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class TrailBuilderTest {

    private lateinit var context: Context

    @Before
    @Suppress("DEPRECATION")
    fun setup() {
        this.context = RuntimeEnvironment.application.applicationContext
    }

    @Test
    fun given_name_when_buildFromDefault_then_checkResultIsOk(){
        val trail= TrailBuilder()
            .withDefault()
            .build()
        assertEquals("TogeTrail", trail.source)
    }

    @Test
    fun given_gpxSampleTest1_when_buildFromGpx_then_checkResultOk() {
        val gpxParser= GPXParser()
        val inputStream = this.context.assets.open("gpx_sample_test_1")
        val gpx = gpxParser.parse(inputStream)
        assertNotNull(gpx)

        val trail= TrailBuilder()
            .withGpx(gpx)
            .build()
        assertEquals("Test", trail.name)
        assertEquals("Sildian apps", trail.source)
        assertTrue(trail.trailTrack.trailPoints.size > 0)
        assertEquals(44.713993, trail.position?.latitude)
        assertEquals(4.330099, trail.position?.longitude)
        assertEquals(642, trail.trailTrack.trailPoints[0].elevation)
        assertEquals(9, trail.trailTrack.trailPointsOfInterest.size)
        assertEquals("Point 1", trail.trailTrack.trailPointsOfInterest[0].name)
    }

    @Test
    fun given_gpxSampleTest2_when_buildFromGpx_then_checkNoTrackExceptionIsRaised() {
        val gpxParser= GPXParser()
        val inputStream = this.context.assets.open("gpx_sample_test_2")
        val gpx = gpxParser.parse(inputStream)
        assertNotNull(gpx)

        try {
            TrailBuilder()
                .withGpx(gpx)
                .build()
            assertEquals("TRUE", "FALSE")
        }
        catch(e: TrailBuildException){
            assertEquals(TrailBuildException.ErrorCode.NO_TRACK, e.errorCode)
        }
    }

    @Test
    fun given_gpxSampleTest3_when_buildFromGpx_then_checkTooManyTracksExceptionIsRaised() {
        val gpxParser= GPXParser()
        val inputStream = this.context.assets.open("gpx_sample_test_3")
        val gpx = gpxParser.parse(inputStream)
        assertNotNull(gpx)

        try {
            TrailBuilder()
                .withGpx(gpx)
                .build()
            assertEquals("TRUE", "FALSE")
        }
        catch(e: TrailBuildException){
            assertEquals(TrailBuildException.ErrorCode.TOO_MANY_TRACKS, e.errorCode)
        }
    }
}