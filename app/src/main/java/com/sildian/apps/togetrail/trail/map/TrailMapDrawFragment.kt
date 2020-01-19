package com.sildian.apps.togetrail.trail.map

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.Polyline
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.trail.info.TrailInfoFragment
import com.sildian.apps.togetrail.trail.info.TrailPOIInfoFragment
import com.sildian.apps.togetrail.trail.model.Trail
import com.sildian.apps.togetrail.trail.model.TrailPoint
import com.sildian.apps.togetrail.trail.model.TrailPointOfInterest
import kotlinx.android.synthetic.main.fragment_trail_map_draw.view.*

/*************************************************************************************************
 * Lets the user create a trail by drawing the track on the map and adding points of interest
 ************************************************************************************************/

class TrailMapDrawFragment :
    BaseTrailMapFragment(),
    GoogleMap.InfoWindowAdapter,
    GoogleMap.OnInfoWindowClickListener
{

    /**********************************UI component**********************************************/

    private val actionsButtonsLayout by lazy {layout.fragment_trail_map_draw_layout_actions_buttons}
    private val removePointButton by lazy {layout.fragment_trail_map_draw_button_point_remove}
    private val addPoiButton by lazy {layout.fragment_trail_map_draw_button_poi_add}

    /************************************Life cycle**********************************************/

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        Log.d(TAG_FRAGMENT, "Fragment '${javaClass.simpleName}' created")
        this.trail= Trail()
        initializeRemovePointButton()
        initializeAddPoiButton()
        return this.layout
    }

    /************************************UI monitoring*******************************************/

    override fun getLayoutId(): Int = R.layout.fragment_trail_map_draw

    override fun getMapViewId(): Int = R.id.fragment_trail_map_draw_map_view

    override fun getInfoBottomSheetId(): Int = R.id.fragment_trail_map_draw_bottom_sheet_info

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

    private fun revealActionsButtons(){
        this.actionsButtonsLayout.visibility=View.VISIBLE
        this.actionsButtonsLayout.layoutAnimation=
            AnimationUtils.loadLayoutAnimation(context, R.anim.anim_layout_appear_right)
        this.actionsButtonsLayout.animate()
    }

    private fun hideActionsButtons(){
        val hideAnimation=AnimationUtils.loadAnimation(context, R.anim.anim_vanish_down)
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

    override fun proceedAdditionalOnMapReadyActions() {
        this.map?.setInfoWindowAdapter(this)
        this.map?.setOnInfoWindowClickListener(this)
    }

    override fun onMapClick(point: LatLng?) {
        if(this.infoBottomSheet.state!=BottomSheetBehavior.STATE_HIDDEN) {
            hideInfoBottomSheet()
        }
        else {
            if (point != null) {
                Log.d(TAG_MAP, "Click on map at point lat ${point.latitude} lng ${point.longitude}")
                addTrailPoint(point)
            } else {
                //TODO handle
                Log.w(TAG_MAP, "Click on map at unknown point")
            }
        }
    }

    override fun onMarkerClick(marker: Marker?): Boolean {

        /*Shows an info nested fragment depending on the tag of the marker*/

        return when(marker?.tag){
            is TrailPointOfInterest->{
                Log.d(TAG_MAP, "Click on marker (TrailPointOfInterest)")
                val trailPointOfInterest=marker.tag as TrailPointOfInterest
                showTrailPOIInfoFragment(trailPointOfInterest)
                marker.showInfoWindow()
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

    override fun getInfoWindow(marker: Marker?): View? {
        return layoutInflater.inflate(R.layout.map_info_window_poi_remove, this.layout as ViewGroup, false)
    }

    override fun getInfoContents(marker: Marker?): View? {
        return null
    }

    override fun onInfoWindowClick(marker: Marker?) {
        if(marker?.tag is TrailPointOfInterest) {
            Log.d(TAG_MAP, "Click on info window (TrailPointOfInterest)")
            val trailPointOfInterest=marker.tag as TrailPointOfInterest
            removeTrailPointOfInterest(trailPointOfInterest)
        }
        else{
            Log.w(TAG_MAP, "Click on info window (Unknown category)")
        }
    }

    /***********************************Trail monitoring*****************************************/

    /**
     * Shows the current trail on the map and moves the camera to the last point
     */

    override fun showTrailOnMap() {

        if(this.trail!=null) {

            super.showTrailOnMap()

            /*Gets the last trailPoint*/

            val lastPoint = this.trail?.trailTrack!!.trailPoints.last()

            /*Moves the camera to the last point*/

            this.map?.animateCamera(
                CameraUpdateFactory.newLatLng(
                    LatLng(lastPoint.latitude, lastPoint.longitude))
            )
        }
    }

    /**
     * Adds a trailPoint to the current trail
     * @param point : the point to be added
     */

    private fun addTrailPoint(point: LatLng){

        /*Adds a new trailPoint and updates the track on the map*/

        val trailPoint= TrailPoint(point.latitude, point.longitude)
        this.trail?.trailTrack?.trailPoints?.add(trailPoint)
        this.map?.clear()
        showTrailOnMap()

        /*If this is the first trailPoint, reveals the actions buttons*/

        if(this.trail?.trailTrack?.trailPoints?.size==1) {
            revealActionsButtons()
        }
    }

    /**
     * Removes the last trailPoint from the current trail
     * If a trailPointOfInterest is attached, removes it as well
     */

    private fun removeLastTrailPoint(){

        if(this.trail!=null&&this.trail!!.trailTrack.trailPoints.isNotEmpty()) {

            /*Removes the last trailPoint*/

            val lastPoint = this.trail?.trailTrack?.trailPoints?.last()
            this.trail?.trailTrack?.trailPoints?.remove(lastPoint)

            /*Checks if a trailPointOfInterest is attached and eventually removes it*/

            val trailPoiIndex=this.trail?.trailTrack?.findTrailPointOfInterest(lastPoint!!)
            if(trailPoiIndex!=null){
                this.trail?.trailTrack?.trailPointsOfInterest?.removeAt(trailPoiIndex)
            }

            /*Then updates the track on the map*/

            this.map?.clear()
            if(this.trail!!.trailTrack.trailPoints.isNotEmpty()) {
                showTrailOnMap()
            }

            /*If there is no remaining trailPoint, hides the actions buttons*/

            if(this.trail!!.trailTrack.trailPoints.isEmpty()) {
                hideActionsButtons()
            }
        }
    }

    /**
     * Adds a trailPointOfInterest to the current trail
     * The new trailPointOfInterest is located at the last trailPoint's position
     */

    private fun addTrailPointOfInterest(){

        if(this.trail!=null&&this.trail!!.trailTrack.trailPoints.isNotEmpty()) {

            /*Uses the last trailPoint to create a trailPointOfInterest*/

            val lastPoint = this.trail?.trailTrack?.trailPoints?.last()!!
            val trailPointOfInterest=
                TrailPointOfInterest(
                    lastPoint.latitude,
                    lastPoint.longitude,
                    lastPoint.elevation,
                    lastPoint.time)
            this.trail?.trailTrack?.trailPointsOfInterest?.add(trailPointOfInterest)

            /*Then updates the track on the map*/

            this.map?.clear()
            showTrailOnMap()
        }
    }

    /**
     * Removes a trailPointOfInterest from the current trail
     * @param trailPointOfInterest : the trailPointOfInterest to be removed
     */

    private fun removeTrailPointOfInterest(trailPointOfInterest:TrailPointOfInterest){
        this.trail?.trailTrack?.trailPointsOfInterest?.remove(trailPointOfInterest)
        hideInfoBottomSheet()
        this.map?.clear()
        showTrailOnMap()
    }

    /******************************Nested Fragments monitoring***********************************/

    override fun showDefaultInfoFragment() {
        showTrailInfoFragment()
    }

    private fun showTrailInfoFragment(){
        this.infoFragment=
            TrailInfoFragment(this.trail)
        childFragmentManager.beginTransaction()
            .replace(R.id.fragment_trail_map_draw_fragment_info, this.infoFragment).commit()
        collapseInfoBottomSheet()
    }

    private fun showTrailPOIInfoFragment(trailPointOfInterest: TrailPointOfInterest){
        this.infoFragment=
            TrailPOIInfoFragment(
                trailPointOfInterest
            )
        childFragmentManager.beginTransaction()
            .replace(R.id.fragment_trail_map_draw_fragment_info, this.infoFragment).commit()
        collapseInfoBottomSheet()
    }
}
