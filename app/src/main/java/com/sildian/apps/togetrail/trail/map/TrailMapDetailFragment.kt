package com.sildian.apps.togetrail.trail.map

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.trail.model.core.Trail
import com.sildian.apps.togetrail.trail.model.core.TrailPoint
import com.sildian.apps.togetrail.trail.model.core.TrailPointOfInterest
import com.sildian.apps.togetrail.trail.model.support.TrailViewModel
import kotlinx.android.synthetic.main.fragment_trail_map_detail.view.*

/*************************************************************************************************
 * Shows a specific trail on the map and allows to see all its detail information
 ************************************************************************************************/

class TrailMapDetailFragment(trailViewModel: TrailViewModel, isEditable:Boolean=false)
    : BaseTrailMapFragment(trailViewModel, isEditable)
{

    /************************************UI components*******************************************/

    private val seeInfoButton by lazy {layout.fragment_trail_map_detail_button_info_see}

    /************************************UI monitoring*******************************************/

    override fun getLayoutId(): Int = R.layout.fragment_trail_map_detail

    override fun getMapViewId(): Int = R.id.fragment_trail_map_detail_map_view

    override fun getInfoBottomSheetId(): Int = R.id.fragment_trail_map_detail_bottom_sheet_info

    override fun getInfoFragmentId(): Int = R.id.fragment_trail_map_detail_fragment_info

    override fun enableUI() {
        this.map?.uiSettings?.setAllGesturesEnabled(true)
    }

    override fun disableUI() {
        this.map?.uiSettings?.setAllGesturesEnabled(false)
    }

    override fun initializeUI() {
        initializeSeeInfoButton()
    }

    private fun initializeSeeInfoButton(){
        this.seeInfoButton.setOnClickListener {
            showTrailInfoFragment()
        }
    }

    /***********************************Map monitoring*******************************************/

    override fun onMapReadyActionsFinished() {
        showTrailTrackOnMap()
    }

    override fun onMapClick(point: LatLng?) {
        hideInfoBottomSheet()
    }

    override fun onMarkerClick(marker: Marker?): Boolean {

        /*Shows an info nested fragment depending on the tag of the marker*/

        return when(marker?.tag){
            is TrailPointOfInterest ->{
                val trailPointOfInterest=marker.tag as TrailPointOfInterest
                val trailPoiPosition=marker.snippet.toInt()
                showTrailPOIInfoFragment(trailPointOfInterest, trailPoiPosition)
                true
            }
            is TrailPoint ->{
                showTrailInfoFragment()
                true
            }
            else-> {
                false
            }
        }
    }

    /***********************************Trail monitoring*****************************************/

    /**
     * Shows the current trail on the map and zoom in to the first point
     */

    override fun showTrailTrackOnMap() {

        if(this.trailViewModel?.trail!=null) {

            super.showTrailTrackOnMap()

            /*Gets the first trailPoint*/

            val firstPoint = this.trailViewModel?.trail?.trailTrack?.getFirstTrailPoint()

            /*Moves the camera to the first point and zoom in*/

            if(firstPoint!=null) {
                this.map?.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(firstPoint.latitude, firstPoint.longitude), 14.0f))
            }
        }
    }
}
