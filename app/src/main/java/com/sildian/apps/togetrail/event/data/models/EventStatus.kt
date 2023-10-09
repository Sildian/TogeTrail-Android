package com.sildian.apps.togetrail.event.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/*************************************************************************************************
 * Defines the status of an event
 ************************************************************************************************/

@Deprecated("Not used any longer")
@Parcelize
enum class EventStatus (val value:Int) : Parcelable {
    FUTURE (1),
    ON_GOING (2),
    PAST(3),
    CANCELED (4),
    UNKNOWN (5)
}