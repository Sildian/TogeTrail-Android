package com.sildian.apps.togetrail.hiker.data.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/*************************************************************************************************
 * Defines what kind of event an history item refers to
 ************************************************************************************************/

@Parcelize
enum class HikerHistoryType : Parcelable {
    UNKNOWN,
    HIKER_REGISTERED,                   //The hiker registered to TogeTrail
    TRAIL_CREATED,                      //The hiker created a new Trail
    EVENT_CREATED,                      //The hiker created a new event
    EVENT_ATTENDED                      //The hiker decided to attend an event
}