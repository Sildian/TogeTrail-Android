package com.sildian.apps.togetrail.trail.ui.info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import com.sildian.apps.togetrail.common.baseControllers.BaseFragment

/*************************************************************************************************
 * Base for info fragments with special effects related to BottomSheetBehavior
 ************************************************************************************************/

abstract class BaseInfoFragment<T: ViewDataBinding>: BaseFragment<T>() {

    /**********************************UI component**********************************************/

    private var topView:View? = null        //The top view is revealed while dragging up the sheet
    private var bottomView:View? = null     //The bottom view contains all remaining info to display

    /************************************Life cycle**********************************************/

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        this.topView=layout.findViewById(getTopViewId())
        this.bottomView=layout.findViewById(getBottomViewId())
        return this.layout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.layout.post { hideView() }
    }

    /************************************UI monitoring*******************************************/

    abstract fun getTopViewId():Int

    abstract fun getBottomViewId():Int

    /**Hides the topView by moving the bottomView above**/

    fun hideView(){
        this.topView?.let { tView ->
            this.bottomView?.let { bView ->
                bView.translationY = tView.height.toFloat() * -1
            }
        }
    }

    /**
     * Drags the bottomView to progressively reveal the topView
     * @param offset : the offset of the BottomSheet, between 0 and 1
     */

    fun dragView(offset:Float){
        this.topView?.let { tView ->
            this.bottomView?.let { bView ->
                bView.translationY = ((1 - offset) * tView.height) * -1
            }
        }
    }
}