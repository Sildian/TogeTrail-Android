package com.sildian.apps.togetrail.common.listeners

/*************************************************************************************************
 * Any controller with a purpose to save data must inherit from this interface
 ************************************************************************************************/

interface OnSaveDataListener {

    /**
     * Event triggered when data need to be saved
     */

    fun onSaveData()
}