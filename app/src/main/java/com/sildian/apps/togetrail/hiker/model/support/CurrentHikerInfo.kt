package com.sildian.apps.togetrail.hiker.model.support

import com.sildian.apps.togetrail.hiker.model.core.Hiker
import com.sildian.apps.togetrail.trail.model.core.Trail

/*************************************************************************************************
 * Keeps the current logged user's related hiker info in memory
 ************************************************************************************************/

object CurrentHikerInfo {

    var currentHiker: Hiker? = null
    val currentHikerLikedTrail: ArrayList<Trail> = arrayListOf()
    val currentHikerMarkedTrail: ArrayList<Trail> = arrayListOf()
}