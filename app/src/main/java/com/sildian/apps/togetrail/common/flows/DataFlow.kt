package com.sildian.apps.togetrail.common.flows

/*************************************************************************************************
 * Any controller with a purpose to load and save data must inherit from this interface
 ************************************************************************************************/

interface DataFlow {

    /**Triggered when data need to be loaded**/

    fun loadData()

    /**Triggered when data need to be saved**/

    fun saveData()
}