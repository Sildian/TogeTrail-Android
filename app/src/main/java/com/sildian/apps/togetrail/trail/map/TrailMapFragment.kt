package com.sildian.apps.togetrail.trail.map

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.common.utils.MapMarkersUtilities
import com.sildian.apps.togetrail.main.MainActivity
import com.sildian.apps.togetrail.trail.model.core.Trail
import com.sildian.apps.togetrail.trail.model.support.TrailFirebaseQueries

/*************************************************************************************************
 * Shows the list of trails on a map
 * @param trails : the list of trails to be shown
 ************************************************************************************************/

class TrailMapFragment (
    private var trails: List<Trail> = emptyList()
) :
    BaseTrailMapFragment(),
    TrailFirebaseQueries.OnTrailsQueryResultListener
{

    /**********************************UI component**********************************************/

    /************************************Life cycle**********************************************/

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?{
        super.onCreateView(inflater, container, savedInstanceState)
        Log.d(TAG_FRAGMENT, "Fragment '${javaClass.simpleName}' created")
        return this.layout
    }

    /**********************************Data monitoring*******************************************/

    override fun onTrailsQueryResult(trails: List<Trail>) {
        this.trails=trails
        showTrailsOnMap()
    }

    /************************************UI monitoring*******************************************/

    override fun getLayoutId(): Int = R.layout.fragment_trail_map

    override fun getMapViewId(): Int = R.id.fragment_trail_map_map_view

    override fun getInfoBottomSheetId(): Int = R.id.fragment_trail_map_bottom_sheet_info

    override fun getInfoFragmentId(): Int = R.id.fragment_trail_map_fragment_info

    override fun enableUI() {
        this.map?.uiSettings?.setAllGesturesEnabled(true)
    }

    override fun disableUI() {
        this.map?.uiSettings?.setAllGesturesEnabled(false)
    }

    /***********************************Map monitoring*******************************************/

    override fun onMapReadyActionsFinished() {
        if(this.trails.isEmpty()){
            (activity as MainActivity).loadTrails(this)
        }else {
            showTrailsOnMap()
        }
    }

    override fun onMapClick(point: LatLng?) {
        Log.d(TAG_MAP, "Click on map at point lat ${point?.latitude} lng ${point?.longitude}")
        hideInfoBottomSheet()
    }

    override fun onMarkerClick(marker: Marker?): Boolean {
        return when(marker?.tag){
            is Trail -> {
                Log.d(TAG_MAP, "Click on marker (Trail)")
                this.trail=marker.tag as Trail
                showTrailInfoFragment()
                true
            }
            else -> {
                Log.w(TAG_MAP, "Click on marker (Unknown category)")
                false
            }
        }
    }

    override fun onPolylineClick(polyline: Polyline?) {

    }

    /***********************************Trails monitoring****************************************/

    /**Shows the list of trails on the map**/

    private fun showTrailsOnMap(){

        this.map?.clear()

        /*For each trail in the list, shows a marker at the first trailPoint's location*/

        this.trails.forEach { trail ->
            val firstPoint=trail.trailTrack.trailPoints.first()
            this.map?.addMarker(MarkerOptions()
                .position(LatLng(firstPoint.latitude, firstPoint.longitude))
                .icon(MapMarkersUtilities.createMapMarkerFromVector(
                    context, R.drawable.ic_location_trail_map)))
                ?.tag=trail
        }
    }

    /**Shows the current trail's detail**/

    fun showTrailDetail(){
        (activity as MainActivity).seeTrail(this.trail)
    }
}
