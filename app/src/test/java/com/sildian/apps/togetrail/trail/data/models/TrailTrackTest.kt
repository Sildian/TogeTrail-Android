package com.sildian.apps.togetrail.trail.data.models

import com.sildian.apps.togetrail.common.utils.DateUtilities
import org.junit.Test
import org.junit.Assert.*
import org.junit.Before

class TrailTrackTest {

    private val trailTrack=
        TrailTrack()

    @Before
    fun initializeTrailTrack(){
        val trailPoint1= TrailPoint(
            44.713398, 4.331021, 647,
            DateUtilities.getDateAndTime(2019, 11, 15, 11, 25)
        )
        val trailPoint2= TrailPoint(
            44.711736, 4.339089, 738,
            DateUtilities.getDateAndTime(2019, 11, 15, 12, 30)
        )
        val trailPoint3= TrailPoint(
            44.712132, 4.340248, 724,
            DateUtilities.getDateAndTime(2019, 11, 15, 13, 55)
        )
        val trailPoint4= TrailPoint(
            44.711096, 4.340978, 766,
            DateUtilities.getDateAndTime(2019, 11, 15, 14, 15)
        )
        val trailPoint5= TrailPoint(
            44.708152, 4.338279, 702,
            DateUtilities.getDateAndTime(2019, 11, 15, 15, 55)
        )
        this.trailTrack.trailPoints.addAll(listOf(trailPoint1, trailPoint2, trailPoint3, trailPoint4, trailPoint5))
        val trailPointOfInterest=
            TrailPointOfInterest(
                trailPoint3.latitude,
                trailPoint3.longitude
            )
        this.trailTrack.trailPointsOfInterest.add(trailPointOfInterest)
    }

    @Test
    fun given_trailTrack_when_getDuration_then_checkResultIs270() {
        assertEquals(270, this.trailTrack.getDuration())
    }

    @Test
    fun given_trailTrack_when_getDistance_then_checkResultIs1282() {
        assertEquals(1282, this.trailTrack.getDistance())
    }

    @Test
    fun given_trailTrack_when_getAscent_then_checkResultIs133() {
        assertEquals(133, this.trailTrack.getAscent())
    }

    @Test
    fun given_trailTrack_when_getDescent_then_checkResultIs78() {
        assertEquals(78, this.trailTrack.getDescent())
    }

    @Test
    fun given_trailTrack_when_getMaxElevation_then_wheckResultIs766() {
        assertEquals(766, this.trailTrack.getMaxElevation())
    }

    @Test
    fun given_trailTrack_when_getMinElevation_then_checkResultIs647() {
        assertEquals(647, this.trailTrack.getMinElevation())
    }

    @Test
    fun given_trailPoint1_when_findTrailPointOfInterest_then_checkResultIsNull(){
        assertNull(this.trailTrack.findTrailPointOfInterest(this.trailTrack.trailPoints[0]))
    }

    @Test
    fun given_nothing_when_getFirstPoint_then_checkLatLng(){
        val firstPoint=this.trailTrack.getFirstTrailPoint()
        assertNotNull(firstPoint)
        assertEquals(44.713398, firstPoint?.latitude)
        assertEquals(4.331021, firstPoint?.longitude)
    }

    @Test
    fun given_nothing_when_getLastPoint_then_checkLatLng(){
        val lastPoint=this.trailTrack.getLastTrailPoint()
        assertNotNull(lastPoint)
        assertEquals(44.708152, lastPoint?.latitude)
        assertEquals(4.338279, lastPoint?.longitude)
    }

    @Test
    fun given_trailPoint3_when_findTrailPointOfInterest_then_checkResultIs0(){
        assertEquals(0,
            this.trailTrack.findTrailPointOfInterest(this.trailTrack.trailPoints[2]))
    }
}