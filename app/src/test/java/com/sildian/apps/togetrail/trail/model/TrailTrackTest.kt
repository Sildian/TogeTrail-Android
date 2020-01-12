package com.sildian.apps.togetrail.trail.model

import com.sildian.apps.togetrail.common.utils.DateUtilities
import org.junit.Test
import org.junit.Assert.*
import org.junit.Before

class TrailTrackTest {

    private val trailTrack=TrailTrack()

    @Before
    fun initializeTrailTrack(){
        val trailPoint1=TrailPoint(44.713398, 4.331021, 647,
            DateUtilities.getDateAndTime(2019, 11, 15, 11, 25))
        val trailPoint2=TrailPoint(44.711736, 4.339089, 738,
            DateUtilities.getDateAndTime(2019, 11, 15, 12, 30))
        val trailPoint3=TrailPoint(44.712132, 4.340248, 724,
            DateUtilities.getDateAndTime(2019, 11, 15, 13, 55))
        val trailPoint4=TrailPoint(44.711096, 4.340978, 766,
            DateUtilities.getDateAndTime(2019, 11, 15, 14, 15))
        val trailPoint5=TrailPoint(44.708152, 4.338279, 702,
            DateUtilities.getDateAndTime(2019, 11, 15, 15, 55))
        this.trailTrack.trailPoints.addAll(listOf(trailPoint1, trailPoint2, trailPoint3, trailPoint4, trailPoint5))
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
}