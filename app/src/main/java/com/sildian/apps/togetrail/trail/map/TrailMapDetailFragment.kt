package com.sildian.apps.togetrail.trail.map

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.Polyline

import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.trail.info.TrailInfoFragment
import com.sildian.apps.togetrail.trail.info.TrailPOIInfoFragment
import com.sildian.apps.togetrail.trail.model.TrailPoint
import com.sildian.apps.togetrail.trail.model.TrailPointOfInterest

/*************************************************************************************************
 * Shows a specific trail on the map and allows to see all its detail information
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

    override fun proceedAdditionalOnMapReadyActions() {

    }

    override fun onMapClick(point: LatLng?) {
        Log.d(TAG_MAP, "Click on map at point lat ${point?.latitude} lng ${point?.longitude}")
        hideInfoBottomSheet()
    }

    override fun onMarkerClick(marker: Marker?): Boolean {

        /*Shows an info nested fragment depending on the tag of the marker*/

        return when(marker?.tag){
            is TrailPointOfInterest->{
                Log.d(TAG_MAP, "Click on marker (TrailPointOfInterest)")
                val trailPointOfInterest=marker.tag as TrailPointOfInterest
                showTrailPOIInfoFragment(trailPointOfInterest)
                true
            }
            is TrailPoint->{
                Log.d(TAG_MAP, "Click on marker (TrailPoint)")
                showTrailInfoFragment()
                true
            }
            else-> {
                Log.w(TAG_MAP, "Click on marker (Unknown category)")
                false
            }
        }
    }

    override fun onPolylineClick(polyline: Polyline?) {
        Log.d(TAG_MAP, "Click on polyline")
        showTrailInfoFragment()
    }

    /***********************************Trail monitoring*****************************************/

    /**
     * Shows the current trail on the map and zoom in to the first point
     */

    override fun showTrailOnMap() {

        if(this.trail!=null) {

            super.showTrailOnMap()

            /*Gets the first trailPoint*/

            val firstPoint = this.trail?.trailTrack!!.trailPoints.first()

            /*Moves the camera to the first point and zoom in*/

            this.map.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(firstPoint.latitude, firstPoint.longitude), 14.0f)
            )
        }
    }

    /******************************Nested Fragments monitoring***********************************/

    override fun showDefaultInfoFragment() {
        showTrailInfoFragment()
    }

    private fun showTrailInfoFragment(){
        this.infoFragment=
            TrailInfoFragment(this.trail)
        childFragmentManager.beginTransaction()
            .replace(R.id.fragment_trail_map_detail_fragment_info, this.infoFragment).commit()
        collapseInfoBottomSheet()
    }

    private fun showTrailPOIInfoFragment(trailPointOfInterest: TrailPointOfInterest){
        this.infoFragment=
            TrailPOIInfoFragment(
                trailPointOfInterest
            )
        childFragmentManager.beginTransaction()
            .replace(R.id.fragment_trail_map_detail_fragment_info, this.infoFragment).commit()
        collapseInfoBottomSheet()
    }
}
