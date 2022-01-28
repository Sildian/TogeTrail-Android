package com.sildian.apps.togetrail.trail.map

import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.common.utils.uiHelpers.DialogHelper
import com.sildian.apps.togetrail.trail.model.core.TrailPoint
import com.sildian.apps.togetrail.trail.model.core.TrailPointOfInterest

/*************************************************************************************************
 * Base for all Trail fragments using a map and allowing to generate a new Trail with the app
 ************************************************************************************************/

abstract class BaseTrailMapGenerateFragment<T: ViewDataBinding>:
    BaseTrailMapFragment<T>(),
    GoogleMap.InfoWindowAdapter,
    GoogleMap.OnInfoWindowClickListener
{

    /************************************Data monitoring*****************************************/

    override fun loadData() {
        super.loadData()
        initializeData()
    }

    private fun initializeData() {
        this.isEditable = true
    }

    /************************************UI monitoring*******************************************/

    abstract fun revealActionsButtons()

    abstract fun hideActionsButtons()

    override fun initializeUI() {
        showRequestTrailNameDialog()
    }

    private fun showRequestTrailNameDialog() {
        context?.let { context ->
            DialogHelper.createRequestInfoDialog(context, R.string.message_trail_info_request_title) { answer ->
                if (!answer.isNullOrEmpty()) {
                    this.trailViewModel.data.value?.name = answer
                    this.trailViewModel.notifyDataChanged()
                } else {
                    showRequestTrailNameDialog()
                }
            }.show()
        }
    }

    /***********************************Map monitoring*******************************************/

    override fun onMapReadyActionsFinished() {
        this.map?.setInfoWindowAdapter(this)
        this.map?.setOnInfoWindowClickListener(this)
        zoomToUserLocation()
    }

    override fun onMarkerClick(marker: Marker): Boolean {

        /*Shows an info nested fragment depending on the tag of the marker*/

        return when (marker.tag){
            is TrailPointOfInterest -> {
                marker.snippet?.let { poiPosition ->
                    showTrailPOIInfoFragment(poiPosition.toInt())
                    marker.showInfoWindow()
                    true
                }
                false
            }
            is TrailPoint -> {
                showTrailInfoFragment()
                true
            }
            else-> {
                false
            }
        }
    }

    override fun getInfoWindow(marker: Marker): View? {
        return layoutInflater.inflate(R.layout.map_info_window_poi_remove, this.layout as ViewGroup, false)
    }

    override fun getInfoContents(marker: Marker): View? {
        return null
    }

    override fun onInfoWindowClick(marker: Marker) {
        if (marker.tag is TrailPointOfInterest) {
            val trailPointOfInterest = marker.tag as TrailPointOfInterest
            removeTrailPointOfInterest(trailPointOfInterest)
        }
    }

    /***********************************Trail monitoring*****************************************/

    override fun showTrailTrackOnMap() {

        if (this.trailViewModel.data.value != null) {

            super.showTrailTrackOnMap()

            /*Gets the last trailPoint*/

            val lastPoint = this.trailViewModel.data.value?.trailTrack?.getLastTrailPoint()

            /*Moves the camera to the last point*/

            if(lastPoint!=null) {
                this.map?.animateCamera(
                    CameraUpdateFactory.newLatLng(
                        LatLng(lastPoint.latitude, lastPoint.longitude)
                    )
                )
            }
        }
    }

    protected fun addTrailPoint(trailPoint: TrailPoint){

        /*Adds a new trailPoint and updates the track on the map*/

        this.trailViewModel.data.value?.trailTrack?.trailPoints?.add(trailPoint)
        this.map?.clear()
        showTrailTrackOnMap()

        /*If this is the first trailPoint, reveals the actions buttons*/

        if (this.trailViewModel.data.value?.trailTrack?.trailPoints?.size==1) {
            revealActionsButtons()
        }
    }

    protected fun removeLastTrailPoint(){

        if (this.trailViewModel.data.value != null && this.trailViewModel.data.value!!.trailTrack.trailPoints.isNotEmpty()) {

            /*Removes the last trailPoint*/

            val lastPoint = this.trailViewModel.data.value?.trailTrack?.getLastTrailPoint()
            this.trailViewModel.data.value?.trailTrack?.trailPoints?.remove(lastPoint)

            /*Checks if a trailPointOfInterest is attached and eventually removes it*/

            val trailPoiIndex = this.trailViewModel.data.value?.trailTrack?.findTrailPointOfInterest(lastPoint!!)
            if (trailPoiIndex != null){
                this.trailViewModel.data.value?.trailTrack?.trailPointsOfInterest?.removeAt(trailPoiIndex)
            }

            /*Then updates the track on the map*/

            this.map?.clear()
            if (this.trailViewModel.data.value!!.trailTrack.trailPoints.isNotEmpty()) {
                showTrailTrackOnMap()
            }

            /*If there is no remaining trailPoint, hides the actions buttons*/

            if (this.trailViewModel.data.value!!.trailTrack.trailPoints.isEmpty()) {
                hideActionsButtons()
            }
        }
    }

    protected fun addTrailPointOfInterest(){

        if (this.trailViewModel.data.value != null && this.trailViewModel.data.value!!.trailTrack.trailPoints.isNotEmpty()) {

            /*Uses the last trailPoint to create a trailPointOfInterest*/

            val lastPoint = this.trailViewModel.data.value?.trailTrack?.getLastTrailPoint()

            if(lastPoint!=null) {

                val trailPointOfInterest =
                    TrailPointOfInterest(
                        lastPoint.latitude,
                        lastPoint.longitude,
                        lastPoint.elevation,
                        lastPoint.time
                    )
                this.trailViewModel.data.value?.trailTrack?.trailPointsOfInterest?.add(trailPointOfInterest)

                /*Then updates the track on the map*/

                this.map?.clear()
                showTrailTrackOnMap()

                /*And shows the info fragment related to the trailPointOfInterest*/

                val lastPoi = this.trailViewModel.data.value?.trailTrack?.trailPointsOfInterest?.last()!!
                val lastPoiIndex = this.trailViewModel.data.value?.trailTrack?.trailPointsOfInterest?.indexOf(lastPoi)!!
                showTrailPOIInfoFragment(lastPoiIndex)
            }
        }
    }

    protected fun removeTrailPointOfInterest(trailPointOfInterest: TrailPointOfInterest){
        this.trailViewModel.data.value?.trailTrack?.trailPointsOfInterest?.remove(trailPointOfInterest)
        hideInfoBottomSheet()
        this.map?.clear()
        showTrailTrackOnMap()
    }
}