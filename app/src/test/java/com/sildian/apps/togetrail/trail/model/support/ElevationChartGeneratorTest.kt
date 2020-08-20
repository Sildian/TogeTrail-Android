package com.sildian.apps.togetrail.trail.model.support

import android.content.Context
import com.sildian.apps.togetrail.trail.model.core.Trail
import com.sildian.apps.togetrail.trail.model.core.TrailPoint
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class ElevationChartGeneratorTest {

    private lateinit var context: Context

    @Before
    @Suppress("DEPRECATION")
    fun setup() {
        this.context = RuntimeEnvironment.application.applicationContext
    }

    @Test
    fun given_noTrail_when_generateChartData_then_checkDataIsNull() {
        val elevationChartGenerator = ElevationChartGenerator(this.context, null)
        elevationChartGenerator.generateChartData()
        assertNull(elevationChartGenerator.chartData)
    }

    @Test
    fun given_trailWithoutTrailPoints_when_generateChartData_then_checkDataIsNull() {
        val trail = Trail()
        val elevationChartGenerator = ElevationChartGenerator(this.context, trail)
        elevationChartGenerator.generateChartData()
        assertNull(elevationChartGenerator.chartData)
    }

    @Test
    fun given_trailWithoutElevatedTrailPoints_when_generateChartData_then_checkDataIsNull() {
        val trail = Trail()
        trail.trailTrack.trailPoints.add(TrailPoint(50.0, 50.0, null, null))
        trail.trailTrack.trailPoints.add(TrailPoint(51.0, 51.0, null, null))
        trail.trailTrack.trailPoints.add(TrailPoint(52.0, 52.0, null, null))
        val elevationChartGenerator = ElevationChartGenerator(this.context, trail)
        elevationChartGenerator.generateChartData()
        assertNull(elevationChartGenerator.chartData)
    }

    @Test
    fun given_trailWithElevatedPoints_when_generateChartData_then_checkDataMatchTrailPoints() {
        val trail = Trail()
        trail.trailTrack.trailPoints.add(TrailPoint(50.0, 50.0, 1200, null))
        trail.trailTrack.trailPoints.add(TrailPoint(51.0, 51.0, 2500, null))
        trail.trailTrack.trailPoints.add(TrailPoint(52.0, 52.0, 900, null))
        val elevationChartGenerator = ElevationChartGenerator(this.context, trail)
        elevationChartGenerator.generateChartData()
        assertEquals(1, elevationChartGenerator.chartData?.dataSetCount)
        assertEquals(3, elevationChartGenerator.chartData?.entryCount)
        assertEquals(2500.0, elevationChartGenerator.chartData?.yMax?.toDouble())
        assertEquals(900.0, elevationChartGenerator.chartData?.yMin?.toDouble())
    }
}