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
import com.sildian.apps.togetrail.trail.model.Trail
import com.sildian.apps.togetrail.trail.model.TrailPoint
import com.sildian.apps.togetrail.trail.model.TrailPointOfInterest
import kotlinx.android.synthetic.main.fragment_trail_map_draw.view.*

/*************************************************************************************************
 * Lets the user create a trail by drawing the track on the map and adding points of interest
 ************************************************************************************************/

class TrailMapDrawFragment : BaseTrailMapFragment() {

    /**********************************UI component**********************************************/

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

    /***********************************Map monitoring*******************************************/

    override fun onMapClick(point: LatLng?) {
        if(point!=null){
            Log.d(TAG_MAP, "Click on map at point lat ${point.latitude} lng ${point.longitude}")
            addTrailPoint(point)
        }
        else{
            //TODO handle
            Log.w(TAG_MAP, "Click on map at unknown point")
        }
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
     * Shows the current trail on the map and moves the camera to the last point
     */

    override fun showTrailOnMap() {

        if(this.trail!=null) {

            super.showTrailOnMap()

            /*Gets the last trailPoint*/

            val lastPoint = this.trail?.trailTrack!!.trailPoints.last()

            /*Moves the camera to the last point*/

            this.map.animateCamera(
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
        val trailPoint= TrailPoint(point.latitude, point.longitude)
        this.trail?.trailTrack?.trailPoints?.add(trailPoint)
        this.map.clear()
        showTrailOnMap()
    }

    /**
     * Removes the last trailPoint from the current trail
     */

    private fun removeLastTrailPoint(){
        if(this.trail!=null&&this.trail!!.trailTrack.trailPoints.isNotEmpty()) {
            val lastPoint = this.trail?.trailTrack?.trailPoints?.last()
            this.trail?.trailTrack?.trailPoints?.remove(lastPoint)
            this.map.clear()
            if(this.trail!!.trailTrack.trailPoints.isNotEmpty()) {
                showTrailOnMap()
            }
        }
    }

    /**
     * Adds a trailPointOfInterest to the current trail
     * The new trailPointOfInterest is located at the last trailPoint's position
     */

    private fun addTrailPointOfInterest(){
        if(this.trail!=null&&this.trail!!.trailTrack.trailPoints.isNotEmpty()) {
            val lastPoint = this.trail?.trailTrack?.trailPoints?.last()!!
            val trailPointOfInterest=
                TrailPointOfInterest(
                    lastPoint.latitude,
                    lastPoint.longitude,
                    lastPoint.elevation,
                    lastPoint.time)
            this.trail?.trailTrack?.trailPointsOfInterest?.add(trailPointOfInterest)
            this.map.clear()
            showTrailOnMap()
        }
    }

    /******************************Nested Fragments monitoring***********************************/

    override fun showDefaultInfoFragment() {

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
