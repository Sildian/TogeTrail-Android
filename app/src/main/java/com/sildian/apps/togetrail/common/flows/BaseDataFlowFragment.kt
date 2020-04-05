package com.sildian.apps.togetrail.common.flows

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

/*************************************************************************************************
 * Base fragment for all fragment aiming to load and save data
 ************************************************************************************************/

abstract class BaseDataFlowFragment : Fragment(), DataFlow {

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

    override fun loadData() {
        //Nothing here, override in children
    }

    override fun updateData(data:Any?) {
        //Nothing here, override in children
    }

    override fun saveData() {
        //Nothing here, override in children
    }

    /************************************UI monitoring*******************************************/

    abstract fun getLayoutId():Int
}