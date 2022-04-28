package com.sildian.apps.togetrail.trail.map

import android.graphics.drawable.Drawable
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.viewModels
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
import com.google.android.material.button.MaterialButtonToggleGroup
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.common.utils.DateUtilities
import com.sildian.apps.togetrail.common.utils.MapMarkersUtilities
import com.sildian.apps.togetrail.common.utils.MetricsHelper
import com.sildian.apps.togetrail.databinding.FragmentTrailMapBinding
import com.sildian.apps.togetrail.event.model.core.Event
import com.sildian.apps.togetrail.event.model.viewModels.EventsViewModel
import com.sildian.apps.togetrail.main.MainActivity
import com.sildian.apps.togetrail.trail.model.core.Trail
import com.sildian.apps.togetrail.trail.model.core.TrailLevel
import com.sildian.apps.togetrail.trail.model.viewModels.TrailsViewModel
import dagger.hilt.android.AndroidEntryPoint

/*************************************************************************************************
 * Shows the list of trails on a map, and also the list of events
 ************************************************************************************************/

@AndroidEntryPoint
class TrailMapFragment :
    BaseTrailMapFragment<FragmentTrailMapBinding>(),
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

    private val trailsViewModel: TrailsViewModel by viewModels()
    private val eventsViewModel: EventsViewModel by viewModels()
    private var showTrails = true
    private var showEvents = false

    /**********************************Data monitoring*******************************************/

    override fun loadData() {
        initializeData()
        observeTrails()
        observeEvents()
    }

    private fun initializeData() {
        this.binding.trailMapFragment = this
    }

    private fun observeTrails() {
        this.trailsViewModel.data.observe(this) { trailsData ->
            trailsData?.error?.let { e ->
                onQueryError(e)
            } ?:
            trailsData?.data?.let { trails ->
                if (trails.isNotEmpty()) {
                    showTrailsOnMap()
                }
                else {
                    (baseActivity as MainActivity).showQueryResultEmptyMessage()
                }
            }
        }
    }

    private fun observeEvents() {
        this.eventsViewModel.data.observe(this) { eventsData ->
            eventsData?.error?.let { e ->
                onQueryError(e)
            } ?:
            eventsData?.data?.let { events ->
                if (events.isNotEmpty()) {
                    showEventsOnMap()
                } else {
                    (baseActivity as MainActivity).showQueryResultEmptyMessage()
                }
            }
        }
    }

    override fun updateData(data:Any?) {
        if (data == null) {
            if (this.showTrails) {
                loadTrails()
            }
            if (this.showEvents) {
                loadEvents()
            }
        }
    }

    private fun loadTrails(){
        val trailsQuery = (activity as MainActivity).trailsQuery
        this.trailsViewModel.loadTrailsRealTime(trailsQuery)
    }

    private fun loadEvents(){
        val eventsQuery = (activity as MainActivity).eventsQuery
        this.eventsViewModel.loadEventsRealTime(eventsQuery)
    }

    /************************************UI monitoring*******************************************/

    override fun getLayoutId(): Int = R.layout.fragment_trail_map

    override fun getMapViewId(): Int = R.id.fragment_trail_map_map_view

    override fun getInfoBottomSheetId(): Int = R.id.fragment_trail_map_bottom_sheet_info

    override fun getInfoFragmentId(): Int = R.id.fragment_trail_map_fragment_info

    override fun enableUI() {
        this.map?.uiSettings?.setAllGesturesEnabled(true)
        this.binding.fragmentTrailMapButtonSearch.isEnabled = true
        this.binding.fragmentTrailMapToggleFilter.isEnabled = true
    }

    override fun disableUI() {
        this.map?.uiSettings?.setAllGesturesEnabled(false)
        this.binding.fragmentTrailMapButtonSearch.isEnabled = false
        this.binding.fragmentTrailMapToggleFilter.isEnabled = false
    }

    override fun getMessageView(): View = this.binding.fragmentTrailMapViewMessage

    override fun getMessageAnchorView(): View? = null

    @Suppress("UNUSED_PARAMETER")
    fun onSearchButtonClick(view: View) {
        val point = this.map?.cameraPosition?.target
        if (point != null) {
            (activity as MainActivity).setQueriesToSearchAroundPoint(point)
            if (this.showTrails) {
                loadTrails()
            }
            if (this.showEvents) {
                loadEvents()
            }
        }
    }

    @Suppress("UNUSED_PARAMETER")
    fun onFilterToggleButtonChecked(group: MaterialButtonToggleGroup, checkedId: Int, isChecked: Boolean) {
        when (checkedId) {
            R.id.fragment_trail_map_toggle_filter_trails ->
                if (isChecked) {
                    this.showTrails = true
                    this.showEvents = false
                    loadTrails()
                }
            R.id.fragment_trail_map_toggle_filter_events ->
                if (isChecked) {
                    this.showTrails = false
                    this.showEvents = true
                    loadEvents()
                }
        }
    }

    /***********************************Map monitoring*******************************************/

    override fun onMapReadyActionsFinished() {
        this.map?.setInfoWindowAdapter(this)
        this.map?.setOnInfoWindowClickListener(this)
        when {
            this.showTrails ->
                if (this.trailsViewModel.data.value?.data.isNullOrEmpty()) {
                    loadTrails()
                } else {
                    showTrailsOnMap()
                }
            this.showEvents ->
                if (this.eventsViewModel.data.value?.data.isNullOrEmpty()) {
                    loadEvents()
                } else {
                    showEventsOnMap()
                }
        }
    }

    override fun onMapClick(point: LatLng) {
        hideInfoBottomSheet()
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        this.map?.animateCamera(CameraUpdateFactory.newLatLng(marker.position))
        return when(marker.tag) {
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

    override fun getInfoWindow(marker: Marker): View? {
        return when(marker.tag) {
            is Trail -> showTrailInfoWindow(marker, marker.tag as Trail)
            is Event -> showEventInfoWindow(marker, marker.tag as Event)
            else -> null
        }
    }

    override fun getInfoContents(marker: Marker): View? {
        return null
    }

    override fun onInfoWindowClick(marker: Marker) {
        when(marker.tag) {
            is Trail -> (activity as MainActivity).seeTrail(marker.tag as Trail)
            is Event -> (activity as MainActivity).seeEvent(marker.tag as Event)
        }
    }

    private fun showTrailInfoWindow(marker:Marker?, trail:Trail):View? {
        context?.let { context ->
            val view = layoutInflater.inflate(
                R.layout.map_info_window_trail, this.layout as ViewGroup, false)
            val photoImageView = view.findViewById<ImageView>(R.id.map_info_window_trail_image_view_photo)
            val nameText = view.findViewById<TextView>(R.id.map_info_window_trail_text_name)
            val levelText = view.findViewById<TextView>(R.id.map_info_window_trail_text_level)
            val durationText = view.findViewById<TextView>(R.id.map_info_window_trail_text_duration)
            val ascentText = view.findViewById<TextView>(R.id.map_info_window_trail_text_ascent)
            val locationText = view.findViewById<TextView>(R.id.map_info_window_trail_text_location)
            if (trail.getFirstPhotoUrl() != null) {
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
                    .into(photoImageView)
            }
            nameText.text = trail.name
            when(trail.level){
                TrailLevel.UNKNOWN -> levelText.setText(R.string.label_trail_level_unknown)
                TrailLevel.EASY -> levelText.setText(R.string.label_trail_level_easy)
                TrailLevel.MEDIUM -> levelText.setText(R.string.label_trail_level_medium)
                TrailLevel.HARD -> levelText.setText(R.string.label_trail_level_hard)
            }
            durationText.text=
                MetricsHelper.displayDuration(context, trail.duration)
            ascentText.text=
                MetricsHelper.displayAscent(context, trail.ascent, true, false)
            locationText.text = trail.location.toString()
            return view
        }
        return null
    }

    private fun showEventInfoWindow(marker:Marker?, event:Event):View? {
        val view = layoutInflater.inflate(
            R.layout.map_info_window_event, this.layout as ViewGroup, false)
        val photoImageView = view.findViewById<ImageView>(R.id.map_info_window_event_image_view_photo)
        val nameText = view.findViewById<TextView>(R.id.map_info_window_event_text_name)
        val beginDateText = view.findViewById<TextView>(R.id.map_info_window_event_text_begin_date)
        val nbDaysText = view.findViewById<TextView>(R.id.map_info_window_event_text_nb_days)
        val locationText = view.findViewById<TextView>(R.id.map_info_window_event_text_location)
        if (event.mainPhotoUrl!=null) {
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
                .into(photoImageView)
        }
        nameText.text=event.name
        event.beginDate?.let { beginDate ->
            beginDateText.text = DateUtilities.displayDateShort(beginDate)
        }
        event.getNbDays()?.let { nbDays ->
            val metric = if (nbDays > 1) {
                resources.getString(R.string.label_event_days)
            } else {
                resources.getString(R.string.label_event_day)
            }
            val nbDaysToDisplay = "$nbDays $metric"
            nbDaysText.text = nbDaysToDisplay
        }
        locationText.text=event.meetingPoint.toString()
        return view
    }

    /***********************************Trails monitoring****************************************/

    private fun showTrailsOnMap() {

        this.map?.clear()

        /*For each trail in the list, shows a marker*/

        this.trailsViewModel.data.value?.data?.forEach { trail ->
            if (trail.position != null) {
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

    private fun showEventsOnMap() {

        this.map?.clear()

        /*For each event in the list, shows a marker*/

        this.eventsViewModel.data.value?.data?.forEach { event ->
            if (event.position!=null) {
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
