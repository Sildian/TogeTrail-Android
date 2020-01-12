package com.sildian.apps.togetrail.trail

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetBehavior

import com.sildian.apps.togetrail.R
import kotlinx.android.synthetic.main.fragment_trail_map_detail.view.*

/*************************************************************************************************
 * Shows a specific trail on the map and allows :
 * - to see all its detail information
 * - to edit its information
 ************************************************************************************************/

class TrailMapDetailFragment : BaseTrailMapFragment() {

    /**********************************Static items**********************************************/

    companion object{

        /**Nested Fragments Ids***/
        const val ID_NESTED_FRAGMENT_INFO_TRAIL=1
        const val ID_NESTED_FRAGMENT_INFO_POINT_OF_INTEREST=2
    }

    /**********************************UI component**********************************************/

    /************************************Life cycle**********************************************/

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?{
        super.onCreateView(inflater, container, savedInstanceState)
        Log.d(TAG_FRAGMENT, "Fragment '${javaClass.simpleName}' created")
        showTrailOnMap()
        return this.layout
    }

    /************************************UI monitoring*******************************************/

    override fun getLayoutId(): Int = R.layout.fragment_trail_map_detail

    override fun getMapViewId(): Int = R.id.fragment_trail_map_detail_map_view

    override fun getInfoBottomSheetId(): Int = R.id.fragment_trail_map_detail_bottom_sheet_info

    /******************************Nested Fragments monitoring***********************************/

    /**
     * Shows a nested info fragment
     * @param fragmentId : defines which fragment to display (choice within ID_NESTED_FRAGMENT_xxx)
     */

    override fun showInfoFragment(fragmentId:Int){
        when(fragmentId){
            ID_NESTED_FRAGMENT_INFO_TRAIL ->this.infoFragment=TrailInfoFragment()
            //TODO handle other fragments
        }
        childFragmentManager.beginTransaction()
            .replace(R.id.fragment_trail_map_detail_fragment_info, this.infoFragment).commit()
    }

    /**
     * Shows the default info fragment and sets the bottomSheet's state to 'collapse'
     */

    override fun showDefaultInfoFragment() {
        this.infoFragment=TrailInfoFragment()
        childFragmentManager.beginTransaction()
            .replace(R.id.fragment_trail_map_detail_fragment_info, this.infoFragment).commit()
        collapseInfoBottomSheet()
    }
}
