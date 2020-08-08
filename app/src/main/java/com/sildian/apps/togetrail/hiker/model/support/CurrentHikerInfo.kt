package com.sildian.apps.togetrail.hiker.model.support

import com.sildian.apps.togetrail.hiker.model.core.Hiker

/*************************************************************************************************
 * Keeps the current logged user's related hiker info in memory
 ************************************************************************************************/

object CurrentHikerInfo {

    var currentHiker: Hiker? = null
}