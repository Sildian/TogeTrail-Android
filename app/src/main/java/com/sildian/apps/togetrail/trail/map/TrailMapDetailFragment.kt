package com.sildian.apps.togetrail.trail.map

import android.view.View
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.sildian.apps.togetrail.R
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
    private val messageView by lazy { layout.fragment_trail_map_detail_view_message }

    /************************************UI monitoring*******************************************/

    override fun getLayoutId(): Int = R.layout.fragment_trail_map_detail

    override fun useDataBinding(): Boolean = false

    override fun getMapViewId(): Int = R.id.fragment_trail_map_detail_map_view

    override fun getInfoBottomSheetId(): Int = R.id.fragment_trail_map_detail_bottom_sheet_info

    override fun getInfoFragmentId(): Int = R.id.fragment_trail_map_detail_fragment_info

    override fun enableUI() {
        this.map?.uiSettings?.setAllGesturesEnabled(true)
    }

    override fun disableUI() {
        this.map?.uiSettings?.setAllGesturesEnabled(false)
    }

    override fun getMessageView(): View = this.messageView

    override fun getMessageAnchorView(): View? = null

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
                val trailPoiPosition=marker.snippet.toInt()
                showTrailPOIInfoFragment(trailPoiPosition)
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

    override fun showTrailTrackOnMap() {

        if(this.trailViewModel?.trail?.value != null) {

            super.showTrailTrackOnMap()

            /*Gets the first trailPoint*/

            val firstPoint = this.trailViewModel?.trail?.value?.trailTrack?.getFirstTrailPoint()

            /*Moves the camera to the first point and zoom in*/

            if(firstPoint!=null) {
                this.map?.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(firstPoint.latitude, firstPoint.longitude), 14.0f))
            }
        }
    }
}
