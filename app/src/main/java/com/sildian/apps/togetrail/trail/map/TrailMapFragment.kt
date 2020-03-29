package com.sildian.apps.togetrail.trail.map

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.common.utils.DateUtilities
import com.sildian.apps.togetrail.common.utils.MapMarkersUtilities
import com.sildian.apps.togetrail.common.utils.MetricsHelper
import com.sildian.apps.togetrail.event.model.core.Event
import com.sildian.apps.togetrail.main.MainActivity
import com.sildian.apps.togetrail.trail.model.core.Trail
import com.sildian.apps.togetrail.trail.model.core.TrailLevel
import kotlinx.android.synthetic.main.fragment_trail_map.view.*
import kotlinx.android.synthetic.main.map_info_window_event.view.*
import kotlinx.android.synthetic.main.map_info_window_trail.view.*

/*************************************************************************************************
 * Shows the list of trails on a map, and also the list of events
 * @param trails : the list of trails to be shown
 * @param events : the list of events to be shown
 ************************************************************************************************/

class TrailMapFragment (
    private var trails: List<Trail> = emptyList(),
    private var events: List<Event> = emptyList()
) :
    BaseTrailMapFragment(),
    GoogleMap.InfoWindowAdapter,
    GoogleMap.OnInfoWindowClickListener
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

    override fun updateData() {
        (activity as MainActivity).loadTrails(this::handleTrailsQueryResult)
        (activity as MainActivity).loadEvents(this::handleEventsQueryResult)
    }

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
        this.map?.setInfoWindowAdapter(this)
        this.map?.setOnInfoWindowClickListener(this)
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
                marker.showInfoWindow()
                true
            }
            is Event -> {
                Log.d(TAG, "Clicked on marker (Event)")
                marker.showInfoWindow()
                true
            }
            else -> {
                Log.w(TAG, "Clicked on marker (Unknown category)")
                false
            }
        }
    }

    override fun getInfoWindow(marker: Marker?): View? {
        return when(marker?.tag){
            is Trail -> showTrailInfoWindow(marker.tag as Trail)
            is Event -> showEventInfoWindow(marker.tag as Event)
            else -> null
        }
    }

    override fun getInfoContents(marker: Marker?): View? {
        return null
    }

    override fun onInfoWindowClick(marker: Marker?) {
        when(marker?.tag){
            is Trail -> (activity as MainActivity).seeTrail(marker.tag as Trail)
            is Event -> (activity as MainActivity).seeEvent(marker.tag as Event)
        }
    }

    private fun showTrailInfoWindow(trail:Trail):View?{
        val view=layoutInflater.inflate(
            R.layout.map_info_window_trail, this.layout as ViewGroup, false)
        view.map_info_window_trail_text_name.text=trail.name
        when(trail.level){
            TrailLevel.EASY -> view.map_info_window_trail_text_level.setText(R.string.label_trail_level_easy)
            TrailLevel.MEDIUM -> view.map_info_window_trail_text_level.setText(R.string.label_trail_level_medium)
            TrailLevel.HARD -> view.map_info_window_trail_text_level.setText(R.string.label_trail_level_hard)
        }
        view.map_info_window_trail_text_duration.text=
            MetricsHelper.displayDuration(context!!, trail.duration?.toLong())
        view.map_info_window_trail_text_ascent.text=
            MetricsHelper.displayAscent(context!!, trail.ascent, true, false)
        view.map_info_window_trail_text_location.text=trail.location.toString()
        return view
    }

    private fun showEventInfoWindow(event:Event):View?{
        val view=layoutInflater.inflate(
            R.layout.map_info_window_event, this.layout as ViewGroup, false)
        view.map_info_window_event_text_name.text=event.name
        view.map_info_window_event_text_begin_date.text= DateUtilities.displayDateShort(event.beginDate!!)
        val nbDays=event.getNbDays()
        val metric=if(nbDays!!>1){
            resources.getString(R.string.label_event_days)
        }else{
            resources.getString(R.string.label_event_day)
        }
        val nbDaysToDisplay="$nbDays $metric"
        view.map_info_window_event_text_nb_days.text=nbDaysToDisplay
        view.map_info_window_event_text_location.text=event.meetingPoint.toString()
        return view
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
