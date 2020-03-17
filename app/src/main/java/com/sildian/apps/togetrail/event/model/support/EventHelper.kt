package com.sildian.apps.togetrail.event.model.support

import com.sildian.apps.togetrail.event.model.core.Event

/*************************************************************************************************
 * Provides with functions allowing to build an event
 ************************************************************************************************/

object EventHelper {

    /**
     * Builds a new event from nothing but a name
     * @return the resulted event
     */

    fun buildFromNothing(): Event {
        return Event()
    }
}