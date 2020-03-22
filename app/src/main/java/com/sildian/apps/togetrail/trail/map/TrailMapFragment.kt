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
import com.sildian.apps.togetrail.event.model.core.Event
import com.sildian.apps.togetrail.main.MainActivity
import com.sildian.apps.togetrail.trail.model.core.Trail
import kotlinx.android.synthetic.main.fragment_trail_map.view.*

/*************************************************************************************************
 * Shows the list of trails on a map, and also the list of events
 * @param trails : the list of trails to be shown
 * @param events : the list of events to be shown
 ************************************************************************************************/

class TrailMapFragment (
    private var trails: List<Trail> = emptyList(),
    private var events: List<Event> = emptyList()
) :
    BaseTrailMapFragment()
{

    /**********************************Static items**********************************************/

    companion object {

        /**Logs**/
        private const val TAG = "TrailMapFragment"
    }

    /**********************************UI component**********************************************/

    private val searchButton by lazy {layout.fragment_trail_map_button_search}

    /************************************Life cycle**********************************************/

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?{
        super.onCreateView(inflater, container, savedInstanceState)
        Log.d(TAG, "Fragment '${javaClass.simpleName}' created")
        initializeSearchButton()
        return this.layout
    }

    /**********************************Data monitoring*******************************************/

    private fun handleTrailsQueryResult(trails: List<Trail>) {
        this.trails=trails
        this.map?.clear()
        showTrailsOnMap()
        showEventsOnMap()
    }

    private fun handleEventsQueryResult(events: List<Event>) {
        this.events=events
        this.map?.clear()
        showTrailsOnMap()
        showEventsOnMap()
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

    private fun initializeSearchButton(){
        this.searchButton.setOnClickListener {
            val point=this.map?.cameraPosition?.target
            if(point!=null) {
                (activity as MainActivity).setQueriesToSearchAroundPoint(point)
                (activity as MainActivity).loadTrails(this::handleTrailsQueryResult)
                (activity as MainActivity).loadEvents(this::handleEventsQueryResult)
            }
        }
    }

    /***********************************Map monitoring*******************************************/

    override fun onMapReadyActionsFinished() {
        if(this.trails.isEmpty()){
            (activity as MainActivity).loadTrails(this::handleTrailsQueryResult)
            (activity as MainActivity).loadEvents(this::handleEventsQueryResult)
        }else {
            showTrailsOnMap()
            showEventsOnMap()
        }
    }

    override fun onMapClick(point: LatLng?) {
        Log.d(TAG, "Clicked on map at point lat ${point?.latitude} lng ${point?.longitude}")
        hideInfoBottomSheet()
    }

    override fun onMarkerClick(marker: Marker?): Boolean {
        return when(marker?.tag){
            is Trail -> {
                Log.d(TAG, "Clicked on marker (Trail)")
                this.trail=marker.tag as Trail
                showTrailInfoFragment()
                true
            }
            is Event -> {
                Log.d(TAG, "Clicked on marker (Event)")
                true
            }
            else -> {
                Log.w(TAG, "Clicked on marker (Unknown category)")
                false
            }
        }
    }

    override fun onPolylineClick(polyline: Polyline?) {

    }

    /***********************************Trails monitoring****************************************/

    /**Shows the list of trails on the map**/

    private fun showTrailsOnMap(){

        /*For each trail in the list, shows a marker*/

        this.trails.forEach { trail ->
            if(trail.position!=null) {
                this.map?.addMarker(
                    MarkerOptions()
                        .position(LatLng(trail.position!!.latitude, trail.position!!.longitude))
                        .icon(
                            MapMarkersUtilities.createMapMarkerFromVector(
                                context, R.drawable.ic_location_trail_map)))
                    ?.tag = trail
            }
        }
    }

    /**Shows the current trail's detail**/

    fun showTrailDetail(){
        (activity as MainActivity).seeTrail(this.trail)
    }

    /***********************************Events monitoring****************************************/

    /**Shows the list of events on the map**/

    private fun showEventsOnMap(){

        /*For each event in the list, shows a marker*/

        this.events.forEach { event ->
            if(event.position!=null) {
                this.map?.addMarker(
                    MarkerOptions()
                        .position(LatLng(event.position!!.latitude, event.position!!.longitude))
                        .icon(
                            MapMarkersUtilities.createMapMarkerFromVector(
                                context, R.drawable.ic_location_event_map)))
                    ?.tag = event
            }
        }
    }
}
