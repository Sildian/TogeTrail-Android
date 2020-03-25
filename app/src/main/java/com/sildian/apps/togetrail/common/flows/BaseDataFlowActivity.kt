package com.sildian.apps.togetrail.common.flows

import androidx.appcompat.app.AppCompatActivity

/*************************************************************************************************
 * Base activity for all activity aiming to load and save data
 ************************************************************************************************/

abstract class BaseDataFlowActivity : AppCompatActivity(), DataFlow {

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