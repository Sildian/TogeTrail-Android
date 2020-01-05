package com.sildian.apps.togetrail.trail

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.sildian.apps.togetrail.R

/*************************************************************************************************
 * Shows a specific trail on the map and allows :
 * - to see all its detail information
 * - to edit its information
 ************************************************************************************************/

class TrailDetailFragment : BaseTrailMapFragment() {

    /**********************************UI component**********************************************/


    /**************************************UI ids************************************************/

    override fun getLayoutId(): Int = R.layout.fragment_trail_detail
    override fun getMapViewId(): Int = R.id.fragment_trail_detail_map_view

    /************************************Life cycle**********************************************/

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?{
        super.onCreateView(inflater, container, savedInstanceState)
        Log.d(TAG_FRAGMENT, "Fragment '${javaClass.simpleName}' created")
        showTrailOnMap()
        return this.layout
    }
}
