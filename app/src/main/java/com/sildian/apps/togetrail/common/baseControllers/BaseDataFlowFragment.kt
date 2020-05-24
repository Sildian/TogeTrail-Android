package com.sildian.apps.togetrail.common.baseControllers

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.common.utils.uiHelpers.DialogHelper

/*************************************************************************************************
 * Base fragment for all fragment aiming to load and save data
 ************************************************************************************************/

abstract class BaseDataFlowFragment : Fragment() {

    /**********************************Static items**********************************************/

    companion object {

        /**Logs**/
        private const val TAG = "BaseDataFlowFragment"
    }

    /*********************************UI components**********************************************/

    protected lateinit var layout: View                     //The fragment's layout
    protected lateinit var binding: ViewDataBinding         //Item for data binding

    /************************************Life cycle**********************************************/

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?{
        if(useDataBinding()){
            this.binding= DataBindingUtil.inflate(inflater, getLayoutId(), container, false)
            this.layout=this.binding.root
        }
        else{
            this.layout= inflater.inflate(getLayoutId(), container, false)
        }
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

    open fun useDataBinding():Boolean = false       //TODO Make it abstract

    open fun initializeUI(){}

    open fun refreshUI(){}

    /**************************************Queries***********************************************/

    /**
     * Handles query errors
     * @param e : the exception
     */

    protected fun handleQueryError(e: Exception) {
        Log.w(TAG, e.message.toString())
        DialogHelper.createInfoDialog(
            context!!,
            R.string.message_query_failure_title,
            R.string.message_query_failure_message
        ).show()
    }
}