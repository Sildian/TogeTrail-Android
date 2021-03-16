package com.sildian.apps.togetrail.common.baseControllers

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
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
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        this.binding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false)
        this.binding.lifecycleOwner = this
        this.layout = this.binding.root
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

    open fun updateData(data: Any?){}

    open fun saveData(){}

    open fun checkDataIsValid():Boolean = true

    /************************************UI monitoring*******************************************/

    abstract fun getLayoutId():Int

    open fun initializeUI(){}

    open fun refreshUI(){}

    /**********************************Query results handling************************************/

    fun onSaveSuccess() {
        this.baseActivity?.onSaveSuccess()
    }

    fun onQueryError(e: Exception) {
        this.baseActivity?.onQueryError(e)
    }
}