package com.sildian.apps.togetrail.trail.model

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.sildian.apps.togetrail.R
import io.ticofab.androidgpxparser.parser.GPXParser
import org.junit.Test
import org.junit.Assert.*

class TrailFactoryTest {

    @Test
    fun given_name_when_buildFromNothing_then_checkResultIsOk(){
        val context = ApplicationProvider.getApplicationContext<Context>()
        val name=context.resources.getString(R.string.message_trail_name_unknown)
        val trail=TrailFactory.buildFromNothing(name)
        assertEquals(name, trail.name)
        assertEquals("TogeTrail", trail.source)
    }

    @Test
    fun given_gpxSampleTest1_when_buildFromGpx_then_checkResultOk() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val gpxParser=GPXParser()
        val inputStream = context.assets.open("gpx_sample_test_1")
        val gpx = gpxParser.parse(inputStream)
        assertNotNull(gpx)

        val trail=TrailFactory.buildFromGpx(gpx)
        assertEquals("Test", trail.name)
        assertEquals("Sildian apps", trail.source)
        assertTrue(trail.trailTrack.trailPoints.size>0)
        assertEquals(642, trail.trailTrack.trailPoints[0].elevation)
        assertEquals(9, trail.trailTrack.trailPointsOfInterest.size)
        assertEquals("Point 1", trail.trailTrack.trailPointsOfInterest[0].name)
    }

    @Test
    fun given_gpxSampleTest2_when_buildFromGpx_then_checkNoTrackExceptionIsRaised() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val gpxParser=GPXParser()
        val inputStream = context.assets.open("gpx_sample_test_2")
        val gpx = gpxParser.parse(inputStream)
        assertNotNull(gpx)

        try {
            val trail = TrailFactory.buildFromGpx(gpx)
        }
        catch(e:TrailFactory.TrailBuildNoTrackException){
            assertEquals("No track is available in the gpx.", e.message)
        }
    }

    @Test
    fun given_gpxSampleTest3_when_buildFromGpx_then_checkTooManyTracksExceptionIsRaised() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val gpxParser=GPXParser()
        val inputStream = context.assets.open("gpx_sample_test_3")
        val gpx = gpxParser.parse(inputStream)
        assertNotNull(gpx)

        try {
            val trail = TrailFactory.buildFromGpx(gpx)
        }
        catch(e:TrailFactory.TrailBuildTooManyTracksException){
            assertEquals("Gpx with more than one track are not supported.", e.message)
        }
    }
}