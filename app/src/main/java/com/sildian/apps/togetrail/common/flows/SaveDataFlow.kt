package com.sildian.apps.togetrail.common.flows

/*************************************************************************************************
 * Any controller with a purpose to save data must inherit from this interface
 ************************************************************************************************/

interface SaveDataFlow {

    /**
     * Triggered when data need to be saved
     */

    fun saveData()
}