package com.sildian.apps.togetrail.common.flows

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

/*************************************************************************************************
 * Base fragment for all fragment aiming to load and save data
 ************************************************************************************************/

abstract class BaseDataFlowFragment : Fragment() {

    /*********************************UI components**********************************************/

    protected lateinit var layout: View                  //The fragment's layout

    /************************************Life cycle**********************************************/

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?{
        this.layout=inflater.inflate(getLayoutId(), container, false)
        loadData()
        initializeUI()
        return this.layout
    }

    /*********************************Data monitoring*******************************************/

    open fun loadData(){}

    open fun updateData(data:Any?){}

    open fun saveData(){}

    open fun checkDataIsValid():Boolean = true

    /************************************UI monitoring*******************************************/

    abstract fun getLayoutId():Int

    open fun initializeUI(){}

    open fun refreshUI(){}
}