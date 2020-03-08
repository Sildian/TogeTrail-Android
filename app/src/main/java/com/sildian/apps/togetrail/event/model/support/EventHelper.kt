package com.sildian.apps.togetrail.event.model.support

import com.sildian.apps.togetrail.event.model.core.Event

/*************************************************************************************************
 * Provides with functions allowing to build an event
 ************************************************************************************************/

object EventHelper {

    /**
     * Builds a new event from nothing but a name.
     * @param name : the name of the event
     * @return the resulted event
     */

    fun buildFromNothing(name:String): Event {
        return Event(
            name = name
        )
    }
}