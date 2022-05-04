package com.sildian.apps.togetrail.hiker.data.helpers

import com.sildian.apps.togetrail.hiker.data.core.Hiker
import com.sildian.apps.togetrail.trail.data.core.Trail

/*************************************************************************************************
 * Keeps the current logged user's related hiker info in memory
 ************************************************************************************************/

object CurrentHikerInfo {

    var currentHiker: Hiker? = null
    val currentHikerLikedTrail: ArrayList<Trail> = arrayListOf()
    val currentHikerMarkedTrail: ArrayList<Trail> = arrayListOf()
}