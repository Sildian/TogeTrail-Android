package com.sildian.apps.togetrail.trail

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sildian.apps.togetrail.R

/*************************************************************************************************
 * Shows the list of trails on a map
 ************************************************************************************************/

class TrailMapFragment : BaseTrailMapFragment()
{

    /**********************************UI component**********************************************/

    /************************************Life cycle**********************************************/

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?{
        super.onCreateView(inflater, container, savedInstanceState)
        Log.d(TAG_FRAGMENT, "Fragment '${javaClass.simpleName}' created")
        return this.layout
    }

    /************************************UI monitoring*******************************************/

    override fun getLayoutId(): Int = R.layout.fragment_trail_map

    override fun getMapViewId(): Int = R.id.fragment_trail_map_map_view

    override fun getInfoBottomSheetId(): Int = R.id.fragment_trail_map_bottom_sheet_info

    /****************************Nested Fragments monitoring*************************************/

    override fun showInfoFragment(fragmentId: Int) {

    }

    override fun showDefaultInfoFragment() {

    }
}
