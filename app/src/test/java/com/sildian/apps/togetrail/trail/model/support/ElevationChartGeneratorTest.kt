package com.sildian.apps.togetrail.trail.model.support

import com.sildian.apps.togetrail.trail.model.core.Trail
import com.sildian.apps.togetrail.trail.model.core.TrailPoint
import org.junit.Assert.*
import org.junit.Test

//TODO will need to implement Robolectric to properly run tests

class ElevationChartGeneratorTest {

    @Test
    fun given_noTrail_when_generateChartData_then_checkDataIsNull() {
        val elevationChartGenerator = ElevationChartGenerator(null)
        elevationChartGenerator.generateChartData()
        assertNull(elevationChartGenerator.chartData)
    }

    @Test
    fun given_trailWithoutTrailPoints_when_generateChartData_then_checkDataIsNull() {
        val trail = Trail()
        val elevationChartGenerator = ElevationChartGenerator(trail)
        elevationChartGenerator.generateChartData()
        assertNull(elevationChartGenerator.chartData)
    }

    @Test
    fun given_trailWithoutElevatedTrailPoints_when_generateChartData_then_checkDataIsNull() {
        val trail = Trail()
        trail.trailTrack.trailPoints.add(TrailPoint(50.0, 50.0, null, null))
        trail.trailTrack.trailPoints.add(TrailPoint(51.0, 51.0, null, null))
        trail.trailTrack.trailPoints.add(TrailPoint(52.0, 52.0, null, null))
        val elevationChartGenerator = ElevationChartGenerator(trail)
        elevationChartGenerator.generateChartData()
        assertNull(elevationChartGenerator.chartData)
    }

    @Test
    fun given_trailWithElevatedPoints_when_generateChartData_then_checkDataMatchTrailPoints() {
        val trail = Trail()
        trail.trailTrack.trailPoints.add(TrailPoint(50.0, 50.0, 1200, null))
        trail.trailTrack.trailPoints.add(TrailPoint(51.0, 51.0, 2500, null))
        trail.trailTrack.trailPoints.add(TrailPoint(52.0, 52.0, 900, null))
        val elevationChartGenerator = ElevationChartGenerator(trail)
        elevationChartGenerator.generateChartData()
        assertEquals(3, elevationChartGenerator.chartData?.dataSetCount)
        assertEquals(2500, elevationChartGenerator.chartData?.yMax)
        assertEquals(900, elevationChartGenerator.chartData?.yMin)
    }
}