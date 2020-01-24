package com.sildian.apps.togetrail.trail.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.trail.model.TrailFactory
import com.sildian.apps.togetrail.trail.model.TrailPoint
import com.sildian.apps.togetrail.trail.model.TrailPointOfInterest

/*************************************************************************************************
 * Base for all Trail fragments using a map and allowing to generate a new Trail with the app
 ************************************************************************************************/

abstract class BaseTrailMapGenerateFragment :
    BaseTrailMapFragment(),
    GoogleMap.InfoWindowAdapter,
    GoogleMap.OnInfoWindowClickListener
{

    /************************************Life cycle**********************************************/

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        initializeTrail()
        return this.layout
    }

    /**********************************Data monitoring*******************************************/

    private fun initializeTrail(){
        val name=resources.getString(R.string.message_trail_name_unknown)
        this.trail=TrailFactory.buildFromNothing(name)
    }

    /************************************UI monitoring*******************************************/

    abstract fun revealActionsButtons()

    abstract fun hideActionsButtons()

    /***********************************Trail monitoring*****************************************/

    /**
     * Shows the current trail on the map and moves the camera to the last point
     */

    override fun showTrailTrackOnMap() {

        if(this.trail!=null) {

            super.showTrailTrackOnMap()

            /*Gets the last trailPoint*/

            val lastPoint = this.trail?.trailTrack!!.trailPoints.last()

            /*Moves the camera to the last point*/

            this.map?.animateCamera(
                CameraUpdateFactory.newLatLng(
                    LatLng(lastPoint.latitude, lastPoint.longitude)
                )
            )
        }
    }

    /**
     * Adds a trailPoint to the current trail
     * @param trailPoint : the point to be added
     */

    protected fun addTrailPoint(trailPoint: TrailPoint){

        /*Adds a new trailPoint and updates the track on the map*/

        this.trail?.trailTrack?.trailPoints?.add(trailPoint)
        this.map?.clear()
        showTrailTrackOnMap()

        /*If this is the first trailPoint, reveals the actions buttons*/

        if(this.trail?.trailTrack?.trailPoints?.size==1) {
            revealActionsButtons()
        }
    }

    /**
     * Removes the last trailPoint from the current trail
     * If a trailPointOfInterest is attached, removes it as well
     */

    protected fun removeLastTrailPoint(){

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
                showTrailTrackOnMap()
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

    protected fun addTrailPointOfInterest(){

        if(this.trail!=null&&this.trail!!.trailTrack.trailPoints.isNotEmpty()) {

            /*Uses the last trailPoint to create a trailPointOfInterest*/

            val lastPoint = this.trail?.trailTrack?.trailPoints?.last()!!
            val trailPointOfInterest=
                TrailPointOfInterest(
                    lastPoint.latitude,
                    lastPoint.longitude,
                    lastPoint.elevation,
                    lastPoint.time,
                    resources.getString(R.string.message_trail_poi_name_unknown))
            this.trail?.trailTrack?.trailPointsOfInterest?.add(trailPointOfInterest)

            /*Then updates the track on the map*/

            this.map?.clear()
            showTrailTrackOnMap()

            /*And shows the info fragment related to the trailPointOfInterest*/

            val lastPoi=this.trail?.trailTrack?.trailPointsOfInterest?.last()!!
            showTrailPOIInfoFragment(lastPoi)
        }
    }

    /**
     * Removes a trailPointOfInterest from the current trail
     * @param trailPointOfInterest : the trailPointOfInterest to be removed
     */

    protected fun removeTrailPointOfInterest(trailPointOfInterest:TrailPointOfInterest){
        this.trail?.trailTrack?.trailPointsOfInterest?.remove(trailPointOfInterest)
        hideInfoBottomSheet()
        this.map?.clear()
        showTrailTrackOnMap()
    }
}