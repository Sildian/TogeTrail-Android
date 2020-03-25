package com.sildian.apps.togetrail.trail.model.core

import android.os.Parcel
import com.google.firebase.firestore.GeoPoint
import com.sildian.apps.togetrail.location.model.core.Location
import org.junit.Assert.*
import org.junit.Test

class TrailTestInstrument{

    @Test
    fun given_trail_when_createFromParcel_then_checkResultIsEqual(){
        val parcel= Parcel.obtain()
        val trail1=Trail(
            id="Trail1",
            name="Super Trail",
            source="TogeTrail",
            position=GeoPoint(50.0, 50.0),
            location= Location(
                "TotoLand",
                "TotoRegion"
            ),
            duration=60,
            distance=1000,
            ascent=500,
            descent=300,
            maxElevation = 1500,
            minElevation = 1100,
            authorId = "Hiker1"
        )
        trail1.writeToParcel(parcel, trail1.describeContents())
        parcel.setDataPosition(0)

        val trail2=Trail.create(parcel)

        assertEquals(trail1, trail2)
    }
}