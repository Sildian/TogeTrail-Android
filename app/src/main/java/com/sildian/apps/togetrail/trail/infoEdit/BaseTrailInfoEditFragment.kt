package com.sildian.apps.togetrail.trail.infoEdit

import androidx.fragment.app.Fragment

/*************************************************************************************************
 * Base fragment for editing trail or trailPointOfInterest's info
 ************************************************************************************************/

abstract class BaseTrailInfoEditFragment : Fragment() {

    /*********************************Data monitoring********************************************/

    abstract fun saveData()
}
