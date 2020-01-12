package com.sildian.apps.togetrail.trail

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.Polyline

import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.trail.model.TrailPoint
import com.sildian.apps.togetrail.trail.model.TrailPointOfInterest

/*************************************************************************************************
 * Shows a specific trail on the map and allows :
 * - to see all its detail information
 * - to edit its information
 ************************************************************************************************/

class TrailMapDetailFragment : BaseTrailMapFragment() {

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

    /***********************************Map monitoring*******************************************/

    override fun handleOnPolylineClick(polyline: Polyline) {
        showTrailInfoFragment()
    }

    override fun handleOnMarkerClick(marker: Marker): Boolean {
        return when(marker.tag){
            is TrailPointOfInterest->{
                val trailPointOfInterest=marker.tag as TrailPointOfInterest
                showTrailPOIInfoFragment(trailPointOfInterest)
                true
            }
            is TrailPoint->{
                showTrailInfoFragment()
                true
            }
            else->
                false
        }
    }

    /******************************Nested Fragments monitoring***********************************/

    override fun showDefaultInfoFragment() {
        showTrailInfoFragment()
    }

    private fun showTrailInfoFragment(){
        this.infoFragment=TrailInfoFragment(this.trail)
        childFragmentManager.beginTransaction()
            .replace(R.id.fragment_trail_map_detail_fragment_info, this.infoFragment).commit()
        collapseInfoBottomSheet()
    }

    private fun showTrailPOIInfoFragment(trailPointOfInterest: TrailPointOfInterest){
        this.infoFragment=TrailPOIInfoFragment(trailPointOfInterest)
        childFragmentManager.beginTransaction()
            .replace(R.id.fragment_trail_map_detail_fragment_info, this.infoFragment).commit()
        collapseInfoBottomSheet()
    }
}
