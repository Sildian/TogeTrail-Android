package com.sildian.apps.togetrail.trail.map

import android.graphics.drawable.Drawable
import android.util.Log
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
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

    /**************************************Data**************************************************/

    private var showTrails=true                     //True if trails must be shown
    private var showEvents=false                    //True if events must be shown

    /**********************************UI component**********************************************/

    private val searchButton by lazy {layout.fragment_trail_map_button_search}
    private val filterToggle by lazy {layout.fragment_trail_map_toggle_filter}

    /**********************************Data monitoring*******************************************/

    override fun updateData(data:Any?) {
        if(data is List<*>){
            when{
                data.firstOrNull() is Trail -> handleTrailsQueryResult(data as List<Trail>)
                data.firstOrNull() is Event -> handleEventsQueryResult(data as List<Event>)
            }
        }
        else if(data==null) {
            if (this.showTrails) {
                (activity as MainActivity).loadTrailsFromDatabase()
            }
            if (this.showEvents) {
                (activity as MainActivity).loadEventsFromDatabase()
            }
        }
    }

    private fun handleTrailsQueryResult(trails: List<Trail>) {
        this.trails=trails
        showTrailsOnMap()
    }

    private fun handleEventsQueryResult(events: List<Event>) {
        this.events=events
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

    override fun initializeUI() {
        initializeSearchButton()
        initializeFilterToggle()
    }

    override fun refreshUI() {
        //Nothing
    }

    private fun initializeSearchButton(){
        this.searchButton.setOnClickListener {
            val point=this.map?.cameraPosition?.target
            if(point!=null) {
                (activity as MainActivity).setQueriesToSearchAroundPoint(point)
                if(this.showTrails) {
                    (activity as MainActivity).loadTrailsFromDatabase()
                }
                if(this.showEvents) {
                    (activity as MainActivity).loadEventsFromDatabase()
                }
            }
        }
    }

    private fun initializeFilterToggle(){
        this.filterToggle.addOnButtonCheckedListener { group, checkedId, isChecked ->
            when(checkedId){
                R.id.fragment_trail_map_toggle_filter_trails ->
                    if(isChecked) {
                        this.showTrails=true
                        this.showEvents=false
                        (activity as MainActivity).loadTrailsFromDatabase()
                    }
                R.id.fragment_trail_map_toggle_filter_events ->
                    if(isChecked) {
                        this.showTrails=false
                        this.showEvents=true
                        (activity as MainActivity).loadEventsFromDatabase()
                    }
            }
        }
    }

    /***********************************Map monitoring*******************************************/

    override fun onMapReadyActionsFinished() {
        this.map?.setInfoWindowAdapter(this)
        this.map?.setOnInfoWindowClickListener(this)
        when{
            this.showTrails ->
                if(this.trails.isEmpty()) {
                    (activity as MainActivity).loadTrailsFromDatabase()
                }else{
                    showTrailsOnMap()
                }
            this.showEvents ->
                if(this.events.isEmpty()){
                    (activity as MainActivity).loadEventsFromDatabase()
                }else{
                    showEventsOnMap()
                }
        }
    }

    override fun onMapClick(point: LatLng?) {
        hideInfoBottomSheet()
    }

    override fun onMarkerClick(marker: Marker?): Boolean {
        return when(marker?.tag){
            is Trail -> {
                marker.showInfoWindow()
                true
            }
            is Event -> {
                marker.showInfoWindow()
                true
            }
            else -> {
                false
            }
        }
    }

    override fun getInfoWindow(marker: Marker?): View? {
        return when(marker?.tag){
            is Trail -> showTrailInfoWindow(marker, marker.tag as Trail)
            is Event -> showEventInfoWindow(marker, marker.tag as Event)
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

    private fun showTrailInfoWindow(marker:Marker?, trail:Trail):View?{
        val view=layoutInflater.inflate(
            R.layout.map_info_window_trail, this.layout as ViewGroup, false)
        if(trail.getFirstPhotoUrl()!=null) {
            Glide.with(view)
                .load(trail.getFirstPhotoUrl())
                .apply(RequestOptions.centerCropTransform())
                .placeholder(R.drawable.ic_trail_black)
                .listener(object : RequestListener<Drawable>{
                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                        Log.w(TAG, e?.message.toString())
                        return false
                    }
                    override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        dataSource?.let {
                            if (dataSource != DataSource.MEMORY_CACHE) {
                                marker?.showInfoWindow()
                            }
                        }
                        return false
                    }
                })
                .into(view.map_info_window_trail_image_view_photo)
        }
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

    private fun showEventInfoWindow(marker:Marker?, event:Event):View?{
        val view=layoutInflater.inflate(
            R.layout.map_info_window_event, this.layout as ViewGroup, false)
        if(event.mainPhotoUrl!=null) {
            Glide.with(view)
                .load(event.mainPhotoUrl)
                .apply(RequestOptions.centerCropTransform())
                .placeholder(R.drawable.ic_trail_black)
                .listener(object : RequestListener<Drawable>{
                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                        Log.w(TAG, e?.message.toString())
                        return false
                    }
                    override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        dataSource?.let {
                            if (dataSource != DataSource.MEMORY_CACHE) {
                                marker?.showInfoWindow()
                            }
                        }
                        return false
                    }
                })
                .into(view.map_info_window_event_image_view_photo)
        }
        view.map_info_window_event_text_name.text=event.name
        event.beginDate?.let { beginDate ->
            view.map_info_window_event_text_begin_date.text = DateUtilities.displayDateShort(beginDate)
        }
        event.getNbDays()?.let { nbDays ->
            val metric = if (nbDays > 1) {
                resources.getString(R.string.label_event_days)
            } else {
                resources.getString(R.string.label_event_day)
            }
            val nbDaysToDisplay = "$nbDays $metric"
            view.map_info_window_event_text_nb_days.text = nbDaysToDisplay
        }
        view.map_info_window_event_text_location.text=event.meetingPoint.toString()
        return view
    }

    /***********************************Trails monitoring****************************************/

    /**Shows the list of trails on the map**/

    private fun showTrailsOnMap(){

        this.map?.clear()

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

    /***********************************Events monitoring****************************************/

    /**Shows the list of events on the map**/

    private fun showEventsOnMap(){

        this.map?.clear()

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
