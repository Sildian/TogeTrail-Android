package com.sildian.apps.togetrail.trail.map

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.Polyline
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.trail.model.core.TrailPoint
import com.sildian.apps.togetrail.trail.model.core.TrailPointOfInterest
import kotlinx.android.synthetic.main.fragment_trail_map_draw.view.*

/*************************************************************************************************
 * Lets the user create a trail by drawing the track on the map and adding points of interest
 ************************************************************************************************/

class TrailMapDrawFragment : BaseTrailMapGenerateFragment(){

    /**********************************Static items**********************************************/

    companion object {

        /**Logs**/
        private const val TAG = "TrailMapDrawFragment"
    }

    /**********************************UI component**********************************************/

    private val actionsButtonsLayout by lazy {layout.fragment_trail_map_draw_layout_actions_buttons}
    private val removePointButton by lazy {layout.fragment_trail_map_draw_button_point_remove}
    private val addPoiButton by lazy {layout.fragment_trail_map_draw_button_poi_add}

    /************************************Life cycle**********************************************/

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        Log.d(TAG, "Fragment '${javaClass.simpleName}' created")
        initializeRemovePointButton()
        initializeAddPoiButton()
        return this.layout
    }

    /************************************UI monitoring*******************************************/

    override fun getLayoutId(): Int = R.layout.fragment_trail_map_draw

    override fun getMapViewId(): Int = R.id.fragment_trail_map_draw_map_view

    override fun getInfoBottomSheetId(): Int = R.id.fragment_trail_map_draw_bottom_sheet_info

    override fun getInfoFragmentId(): Int = R.id.fragment_trail_map_draw_fragment_info

    override fun enableUI() {
        this.map?.uiSettings?.setAllGesturesEnabled(true)
        this.removePointButton.isEnabled=true
        this.addPoiButton.isEnabled=true
    }

    override fun disableUI() {
        this.map?.uiSettings?.setAllGesturesEnabled(false)
        this.removePointButton.isEnabled=false
        this.addPoiButton.isEnabled=false
    }

    private fun initializeRemovePointButton(){
        this.removePointButton.setOnClickListener {
            removeLastTrailPoint()
        }
    }

    private fun initializeAddPoiButton(){
        this.addPoiButton.setOnClickListener {
            addTrailPointOfInterest()
        }
    }

    override fun revealActionsButtons(){
        this.actionsButtonsLayout.visibility=View.VISIBLE
        this.actionsButtonsLayout.layoutAnimation=
            AnimationUtils.loadLayoutAnimation(context, R.anim.layout_appear_right)
        this.actionsButtonsLayout.animate()
    }

    override fun hideActionsButtons(){
        val hideAnimation=AnimationUtils.loadAnimation(context, R.anim.vanish_down)
        hideAnimation.setAnimationListener(object:Animation.AnimationListener{
            override fun onAnimationRepeat(anim: Animation?) {

            }

            override fun onAnimationEnd(anim: Animation?) {
                actionsButtonsLayout.visibility=View.GONE
            }

            override fun onAnimationStart(anim: Animation?) {

            }
        })
        this.actionsButtonsLayout.startAnimation(hideAnimation)
    }

    /***********************************Map monitoring*******************************************/

    override fun onMapReadyActionsFinished() {
        this.map?.setInfoWindowAdapter(this)
        this.map?.setOnInfoWindowClickListener(this)
    }

    override fun onMapClick(point: LatLng?) {
        if(this.infoBottomSheet.state!=BottomSheetBehavior.STATE_HIDDEN) {
            hideInfoBottomSheet()
        }
        else {
            if (point != null) {
                Log.d(TAG, "Clicked on map at point lat ${point.latitude} lng ${point.longitude}")
                val trailPoint=
                    TrailPoint(
                        point.latitude,
                        point.longitude
                    )
                addTrailPoint(trailPoint)
            } else {
                //TODO handle
                Log.w(TAG, "Clicked on map at unknown point")
            }
        }
    }

    override fun onMarkerClick(marker: Marker?): Boolean {

        /*Shows an info nested fragment depending on the tag of the marker*/

        return when(marker?.tag){
            is TrailPointOfInterest ->{
                Log.d(TAG, "Clicked on marker (TrailPointOfInterest)")
                val trailPointOfInterest=marker.tag as TrailPointOfInterest
                val trailPoiPosition=marker.snippet.toInt()
                showTrailPOIInfoFragment(trailPointOfInterest, trailPoiPosition)
                marker.showInfoWindow()
                true
            }
            is TrailPoint ->{
                Log.d(TAG, "Clicked on marker (TrailPoint)")
                showTrailInfoFragment()
                true
            }
            else-> {
                Log.w(TAG, "Clicked on marker (Unknown category)")
                false
            }
        }
    }

    override fun getInfoWindow(marker: Marker?): View? {
        return layoutInflater.inflate(R.layout.map_info_window_poi_remove, this.layout as ViewGroup, false)
    }

    override fun getInfoContents(marker: Marker?): View? {
        return null
    }

    override fun onInfoWindowClick(marker: Marker?) {
        if(marker?.tag is TrailPointOfInterest) {
            Log.d(TAG, "Clicked on info window (TrailPointOfInterest)")
            val trailPointOfInterest=marker.tag as TrailPointOfInterest
            removeTrailPointOfInterest(trailPointOfInterest)
        }
        else{
            Log.w(TAG, "Clicked on info window (Unknown category)")
        }
    }
}
