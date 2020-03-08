package com.sildian.apps.togetrail.event.model.core

import com.sildian.apps.togetrail.common.utils.DateUtilities
import org.junit.Test

import org.junit.Assert.*

class EventTest {

    @Test
    fun given_FutureEvent_when_getStatus_then_checkStatusIsFuture() {
        val event=Event(
            name="Super event",
            beginDate = DateUtilities.getDateAndTime(2020, 2, 7, 9, 0),
            endDate = DateUtilities.getDateAndTime(2020, 2, 8, 16, 0))
        val today=DateUtilities.getDateAndTime(2020, 2, 1, 10, 0)
        assertEquals(EventStatus.FUTURE, event.getStatus(today))
    }

    @Test
    fun given_OnGoingEvent_when_getStatus_then_checkStatusIsOnGoing() {
        val event=Event(
            name="Super event",
            beginDate = DateUtilities.getDateAndTime(2020, 2, 7, 9, 0),
            endDate = DateUtilities.getDateAndTime(2020, 2, 8, 16, 0))
        val today=DateUtilities.getDateAndTime(2020, 2, 7, 15, 0)
        assertEquals(EventStatus.ON_GOING, event.getStatus(today))
    }

    @Test
    fun given_PastEvent_when_getStatus_then_checkStatusIsPast() {
        val event=Event(
            name="Super event",
            beginDate = DateUtilities.getDateAndTime(2020, 2, 7, 9, 0),
            endDate = DateUtilities.getDateAndTime(2020, 2, 8, 16, 0))
        val today=DateUtilities.getDateAndTime(2020, 2, 8, 17, 0)
        assertEquals(EventStatus.PAST, event.getStatus(today))
    }

    @Test
    fun given_canceledEvent_when_getStatus_then_checkStatusIsCanceled() {
        val event=Event(
            name="Super event",
            beginDate = DateUtilities.getDateAndTime(2020, 2, 7, 9, 0),
            endDate = DateUtilities.getDateAndTime(2020, 2, 8, 16, 0),
            isCanceled = true)
        val today=DateUtilities.getDateAndTime(2020, 2, 1, 10, 0)
        assertEquals(EventStatus.CANCELED, event.getStatus(today))
    }

    @Test
    fun given_EventWithUnknownDate_when_getStatus_then_checkStatusIsUnknown() {
        val event=Event(name="Super event")
        val today=DateUtilities.getDateAndTime(2020, 2, 1, 10, 0)
        assertEquals(EventStatus.UNKNOWN, event.getStatus(today))
    }

    @Test
    fun givenEventWithNoDate_when_getNbDays_then_checkResultIsNull(){
        val event=Event(name="Super event")
        assertNull(event.getNbDays())
    }

    @Test
    fun givenEventWithBeginDate_when_getNbDays_then_checkResultIsNull(){
        val event=Event(
            name="Super event",
            beginDate = DateUtilities.getDateAndTime(2020, 2, 7, 9, 0))
        assertNull(event.getNbDays())
    }

    @Test
    fun givenEventWithEndDate_when_getNbDays_then_checkResultIsNull(){
        val event=Event(
            name="Super event",
            endDate = DateUtilities.getDateAndTime(2020, 2, 8, 16, 0))
        assertNull(event.getNbDays())
    }

    @Test
    fun givenEventWithBeginAndEndDate_when_getNbDays_then_checkResultIs2(){
        val event=Event(
            name="Super event",
            beginDate = DateUtilities.getDateAndTime(2020, 2, 7, 9, 0),
            endDate = DateUtilities.getDateAndTime(2020, 2, 8, 16, 0))
        assertEquals(2, event.getNbDays())
    }

    @Test
    fun givenEventWith2days_when_refreshTrailsIds_then_checkTrailsIdsSizeIs2(){
        val event=Event(
            name="Super event",
            beginDate = DateUtilities.getDateAndTime(2020, 2, 7, 9, 0),
            endDate = DateUtilities.getDateAndTime(2020, 2, 8, 16, 0))
        event.refreshTrailsIdsKeys()
        assertEquals(2, event.trailsIds.size)
        assertTrue(event.trailsIds.containsKey(1))
        assertTrue(event.trailsIds.containsKey(2))
    }

    @Test
    fun givenEventWith3daysAndRemove1day_when_refreshTrailsIds_then_checkTrailsIdsSizeIs2(){
        val event=Event(
            name="Super event",
            beginDate = DateUtilities.getDateAndTime(2020, 2, 7, 9, 0),
            endDate = DateUtilities.getDateAndTime(2020, 2, 9, 16, 0))
        event.refreshTrailsIdsKeys()
        event.endDate=DateUtilities.getDateAndTime(2020, 2, 8, 16, 0)
        event.refreshTrailsIdsKeys()
        assertEquals(2, event.trailsIds.size)
        assertTrue(event.trailsIds.containsKey(1))
        assertTrue(event.trailsIds.containsKey(2))
    }

    @Test
    fun givenEventWith2daysAndClears_when_refreshTrailsIds_then_checkTrailsIdsSizeIs0(){
        val event=Event(
            name="Super event",
            beginDate = DateUtilities.getDateAndTime(2020, 2, 7, 9, 0),
            endDate = DateUtilities.getDateAndTime(2020, 2, 8, 16, 0))
        event.refreshTrailsIdsKeys()
        event.beginDate=null
        event.endDate=null
        event.refreshTrailsIdsKeys()
        assertEquals(0, event.trailsIds.size)
    }
}