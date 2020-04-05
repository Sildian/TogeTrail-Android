package com.sildian.apps.togetrail.common.flows

/*************************************************************************************************
 * Any controller with a purpose to load and save data must inherit from this interface
 ************************************************************************************************/

interface DataFlow {

    /**Triggered when data need to be loaded**/

    fun loadData()

    /**Triggered when data need to be updated
     * @param data : the data to update, if needed
     */

    fun updateData(data:Any?)

    /**Triggered when data need to be saved**/

    fun saveData()

    /**Triggered when UI need to be initialized (when the controller starts)**/

    fun initializeUI()

    /**Triggered when UI need to refresh (after a data update)**/

    fun refreshUI()
}