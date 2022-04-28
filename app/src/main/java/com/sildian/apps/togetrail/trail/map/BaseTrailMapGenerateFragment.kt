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
                    this.trailViewModel.data.value?.data?.name = answer
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
            this.trailViewModel.data.value?.data?.trailTrack?.getLastTrailPoint()?.let { lastPoint ->
                this.map?.animateCamera(
                    CameraUpdateFactory.newLatLng(
                        LatLng(lastPoint.latitude, lastPoint.longitude)
                    )
                )
            }
        }
    }

    protected fun addTrailPoint(trailPoint: TrailPoint) {
        this.trailViewModel.data.value?.data?.trailTrack?.trailPoints?.add(trailPoint)
        this.map?.clear()
        showTrailTrackOnMap()
        if (this.trailViewModel.data.value?.data?.trailTrack?.trailPoints?.size == 1) {
            revealActionsButtons()
        }
    }

    protected fun removeLastTrailPoint() {
        this.trailViewModel.data.value?.data?.let { trail ->
            if (trail.trailTrack.trailPoints.isNotEmpty()) {
                trail.trailTrack.getLastTrailPoint()?.let { lastPoint ->
                    trail.trailTrack.trailPoints.remove(lastPoint)
                    val trailPoiIndex = trail.trailTrack.findTrailPointOfInterest(lastPoint)
                    if (trailPoiIndex != null){
                        trail.trailTrack.trailPointsOfInterest.removeAt(trailPoiIndex)
                    }
                }
                this.map?.clear()
                if (trail.trailTrack.trailPoints.isNotEmpty()) {
                    showTrailTrackOnMap()
                }
                if (trail.trailTrack.trailPoints.isEmpty()) {
                    hideActionsButtons()
                }
            }
        }
    }

    protected fun addTrailPointOfInterest() {
        this.trailViewModel.data.value?.data?.let { trail ->
            if (trail.trailTrack.trailPoints.isNotEmpty()) {
                trail.trailTrack.getLastTrailPoint()?.let { lastPoint ->
                    val trailPointOfInterest = TrailPointOfInterest(
                        lastPoint.latitude,
                        lastPoint.longitude,
                        lastPoint.elevation,
                        lastPoint.time
                    )
                    trail.trailTrack.trailPointsOfInterest.add(trailPointOfInterest)
                    this.map?.clear()
                    showTrailTrackOnMap()
                    val lastPoi = trail.trailTrack.trailPointsOfInterest.last()
                    val lastPoiIndex = trail.trailTrack.trailPointsOfInterest.indexOf(lastPoi)
                    showTrailPOIInfoFragment(lastPoiIndex)
                }
            }
        }
    }

    protected fun removeTrailPointOfInterest(trailPointOfInterest: TrailPointOfInterest) {
        this.trailViewModel.data.value?.data?.trailTrack?.trailPointsOfInterest?.remove(trailPointOfInterest)
        hideInfoBottomSheet()
        this.map?.clear()
        showTrailTrackOnMap()
    }
}