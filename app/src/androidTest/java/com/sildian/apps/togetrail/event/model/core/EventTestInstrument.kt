package com.sildian.apps.togetrail.event.model.core

import android.os.Parcel
import com.google.firebase.firestore.GeoPoint
import com.sildian.apps.togetrail.location.model.core.Country
import com.sildian.apps.togetrail.location.model.core.Location
import com.sildian.apps.togetrail.location.model.core.Region
import org.junit.Assert.*
import org.junit.Test
import java.util.*

class EventTestInstrument{

    @Test
    fun given_event_when_createFromParcel_then_checkResultIsEqual(){
        val parcel= Parcel.obtain()
        val event1=Event(
            id="Event1",
            name="Super event",
            position=GeoPoint(50.0, 50.0),
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