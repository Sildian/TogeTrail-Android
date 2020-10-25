package com.sildian.apps.togetrail.trail.map

import android.graphics.drawable.Drawable
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.databinding.Observable
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.common.baseViewModels.ViewModelFactory
import com.sildian.apps.togetrail.common.utils.DateUtilities
import com.sildian.apps.togetrail.common.utils.MapMarkersUtilities
import com.sildian.apps.togetrail.common.utils.MetricsHelper
import com.sildian.apps.togetrail.event.model.core.Event
import com.sildian.apps.togetrail.event.model.support.EventsViewModel
import com.sildian.apps.togetrail.main.MainActivity
import com.sildian.apps.togetrail.trail.model.core.Trail
import com.sildian.apps.togetrail.trail.model.core.TrailLevel
import com.sildian.apps.togetrail.trail.model.support.TrailsViewModel
import kotlinx.android.synthetic.main.fragment_trail_map.view.*
import kotlinx.android.synthetic.main.map_info_window_event.view.*
import kotlinx.android.synthetic.main.map_info_window_trail.view.*

/*************************************************************************************************
 * Shows the list of trails on a map, and also the list of events
 ************************************************************************************************/

class TrailMapFragment :
    BaseTrailMapFragment(),
    GoogleMap.InfoWindowAdapter,
    GoogleMap.OnInfoWindowClickListener
{

    //TODO add progressbar while loading trails and events

    /**********************************Static items**********************************************/

    companion object {

        /**Logs**/
        private const val TAG = "TrailMapFragment"
    }

    /**************************************Data**************************************************/

    private lateinit var trailsViewModel:TrailsViewModel    //The list of trails to display
    private lateinit var eventsViewModel:EventsViewModel    //The list of events to display
    private var showTrails=true                             //True if trails must be shown
    private var showEvents=false                            //True if events must be shown

    /**********************************UI component**********************************************/

    private val searchButton by lazy {layout.fragment_trail_map_button_search}
    private val filterToggle by lazy {layout.fragment_trail_map_toggle_filter}
    private val messageView by lazy { layout.fragment_trail_map_view_message }

    /**********************************Data monitoring*******************************************/

    override fun loadData() {
        this.trailsViewModel= ViewModelProviders
            .of(this, ViewModelFactory)
            .get(TrailsViewModel::class.java)
        this.eventsViewModel= ViewModelProviders
            .of(this, ViewModelFactory)
            .get(EventsViewModel::class.java)
    }

    override fun updateData(data:Any?) {
        if(data==null) {
            if (this.showTrails) {
                loadTrails()
            }
            if (this.showEvents) {
                loadEvents()
            }
        }
    }

    private fun loadTrails(){
        val trailsQuery=(activity as MainActivity).trailsQuery
        this.trailsViewModel.loadTrailsFromDatabaseRealTime(trailsQuery)
    }

    private fun loadEvents(){
        val eventsQuery=(activity as MainActivity).eventsQuery
        this.eventsViewModel.loadEventsFromDatabaseRealTime(eventsQuery)
    }

    /************************************UI monitoring*******************************************/

    override fun getLayoutId(): Int = R.layout.fragment_trail_map

    override fun useDataBinding(): Boolean = false

    override fun getMapViewId(): Int = R.id.fragment_trail_map_map_view

    override fun getInfoBottomSheetId(): Int = R.id.fragment_trail_map_bottom_sheet_info

    override fun getInfoFragmentId(): Int = R.id.fragment_trail_map_fragment_info

    override fun enableUI() {
        this.map?.uiSettings?.setAllGesturesEnabled(true)
        this.searchButton.isEnabled=true
        this.filterToggle.isEnabled=true
    }

    override fun disableUI() {
        this.map?.uiSettings?.setAllGesturesEnabled(false)
        this.searchButton.isEnabled=false
        this.filterToggle.isEnabled=false
    }

    override fun getMessageView(): View = this.messageView

    override fun getMessageAnchorView(): View? = null

    override fun initializeUI() {
        initializeSearchButton()
        initializeFilterToggle()
        initializeTrailsRefreshUI()
        initializeEventsRefreshUI()
    }

    private fun initializeSearchButton(){
        this.searchButton.setOnClickListener {
            val point=this.map?.cameraPosition?.target
            if(point!=null) {
                (activity as MainActivity).setQueriesToSearchAroundPoint(point)
                if(this.showTrails) {
                    loadTrails()
                }
                if(this.showEvents) {
                    loadEvents()
                }
            }
        }
    }

    @Suppress("UNUSED_ANONYMOUS_PARAMETER")
    private fun initializeFilterToggle(){
        this.filterToggle.addOnButtonCheckedListener { group, checkedId, isChecked ->
            when(checkedId){
                R.id.fragment_trail_map_toggle_filter_trails ->
                    if(isChecked) {
                        this.showTrails=true
                        this.showEvents=false
                        loadTrails()
                    }
                R.id.fragment_trail_map_toggle_filter_events ->
                    if(isChecked) {
                        this.showTrails=false
                        this.showEvents=true
                        loadEvents()
                    }
            }
        }
    }

    private fun initializeTrailsRefreshUI(){
        this.trailsViewModel.addOnPropertyChangedCallback(object:Observable.OnPropertyChangedCallback(){
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                if(trailsViewModel.trails.isEmpty()){
                    (activity as MainActivity).showQueryResultEmptyMessage()
                }else {
                    showTrailsOnMap()
                }
            }
        })
    }

    private fun initializeEventsRefreshUI(){
        this.eventsViewModel.addOnPropertyChangedCallback(object:Observable.OnPropertyChangedCallback(){
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                if(eventsViewModel.events.isEmpty()){
                    (activity as MainActivity).showQueryResultEmptyMessage()
                }else {
                    showEventsOnMap()
                }
            }
        })
    }

    /***********************************Map monitoring*******************************************/

    override fun onMapReadyActionsFinished() {
        this.map?.setInfoWindowAdapter(this)
        this.map?.setOnInfoWindowClickListener(this)
        when{
            this.showTrails ->
                if(this.trailsViewModel.trails.isEmpty()) {
                    loadTrails()
                }else{
                    showTrailsOnMap()
                }
            this.showEvents ->
                if(this.eventsViewModel.events.isEmpty()){
                    loadEvents()
                }else{
                    showEventsOnMap()
                }
        }
    }

    override fun onMapClick(point: LatLng?) {
        hideInfoBottomSheet()
    }

    override fun onMarkerClick(marker: Marker?): Boolean {
        this.map?.animateCamera(CameraUpdateFactory.newLatLng(marker?.position))
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
            TrailLevel.UNKNOWN -> view.map_info_window_trail_text_level.setText(R.string.label_trail_level_unknown)
            TrailLevel.EASY -> view.map_info_window_trail_text_level.setText(R.string.label_trail_level_easy)
            TrailLevel.MEDIUM -> view.map_info_window_trail_text_level.setText(R.string.label_trail_level_medium)
            TrailLevel.HARD -> view.map_info_window_trail_text_level.setText(R.string.label_trail_level_hard)
        }
        view.map_info_window_trail_text_duration.text=
            MetricsHelper.displayDuration(context!!, trail.duration)
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

        this.trailsViewModel.trails.forEach { trail ->
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

        this.eventsViewModel.events.forEach { event ->
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
