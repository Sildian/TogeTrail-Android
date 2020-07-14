package com.sildian.apps.togetrail.common.baseControllers

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment

/*************************************************************************************************
 * Base for all fragments
 ************************************************************************************************/

abstract class BaseFragment : Fragment() {

    /*********************************UI components**********************************************/

    protected var baseActivity: BaseActivity? = null            //The activity reference
    protected lateinit var layout: View                         //The fragment's layout
    protected lateinit var binding: ViewDataBinding             //Item for data binding

    /************************************Life cycle**********************************************/

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.baseActivity = context as BaseActivity
    }

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

    override fun onDetach() {
        this.baseActivity = null
        super.onDetach()
    }

    /*********************************Data monitoring*******************************************/

    open fun loadData(){}

    open fun updateData(data:Any?){}

    open fun saveData(){}

    open fun checkDataIsValid():Boolean = true

    /************************************UI monitoring*******************************************/

    abstract fun getLayoutId():Int

    abstract fun useDataBinding():Boolean

    open fun initializeUI(){}

    open fun refreshUI(){}

    /*************************************Errors handling****************************************/

    /**
     * Handles query errors
     * @param e : the exception
     */

    fun handleQueryError(e: Exception) {
        this.baseActivity?.handleQueryError(e)
    }
}