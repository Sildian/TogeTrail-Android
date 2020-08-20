package com.sildian.apps.togetrail.event.model.core

import android.os.Parcel
import com.google.firebase.firestore.GeoPoint
import com.sildian.apps.togetrail.common.utils.DateUtilities
import com.sildian.apps.togetrail.location.model.core.Country
import com.sildian.apps.togetrail.location.model.core.Location
import com.sildian.apps.togetrail.location.model.core.Region
import org.junit.Test

import org.junit.Assert.*
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.*

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
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
    fun given_event_when_createFromParcel_then_checkResultIsEqual(){
        val parcel= Parcel.obtain()
        val event1=Event(
            id="Event1",
            name="Super event",
            position= GeoPoint(50.0, 50.0),
            meetingPoint= Location(
                Country("TOL", "Totoland"),
                Region("TOR", "Totoregion"),
                "Toto City"
            ),
            beginDate = Date(),
            endDate = Date(),
            authorId = "Hiker1"
        )
        event1.writeToParcel(parcel, event1.describeContents())
        parcel.setDataPosition(0)

        val event2=Event.create(parcel)

        assertEquals(event1, event2)
    }
}