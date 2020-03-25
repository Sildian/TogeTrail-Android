package com.sildian.apps.togetrail.common.flows

import androidx.fragment.app.Fragment

/*************************************************************************************************
 * Base fragment for all fragment aiming to load and save data
 ************************************************************************************************/

abstract class BaseDataFlowFragment : Fragment(), DataFlow {

    override fun loadData() {
        //Nothing here, override in children
    }

    override fun updateData() {
        //Nothing here, override in children
    }

    override fun saveData() {
        //Nothing here, override in children
    }
}