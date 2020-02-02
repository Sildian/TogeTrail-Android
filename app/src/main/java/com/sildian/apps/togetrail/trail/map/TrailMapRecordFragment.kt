package com.sildian.apps.togetrail.trail.map

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.Polyline

import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.common.utils.GeoUtilities
import com.sildian.apps.togetrail.trail.model.TrailPoint
import com.sildian.apps.togetrail.trail.model.TrailPointOfInterest
import kotlinx.android.synthetic.main.fragment_trail_map_record.view.*
import java.util.concurrent.Executors

/*************************************************************************************************
 * Lets the user record a trail in real time
 ************************************************************************************************/

class TrailMapRecordFragment : BaseTrailMapGenerateFragment() {

    /**********************************Static items**********************************************/

    companion object{

        /**Record data**/

        //The time interval between each point record (in milliseconds)
        private const val VALUE_RECORD_TIME_INTERVAL=60000

        //The minimum required distance between two points to check before register (in meters)
        private const val VALUE_RECORD_MIN_DISTANCE=100
    }

    /***************************************Data*************************************************/

    private var isRecording=false                       //True when the trail is being recorded

    /**********************************UI component**********************************************/

    private val actionsButtonsLayout by lazy {layout.fragment_trail_map_record_layout_actions_buttons}
    private val addPoiButton by lazy {layout.fragment_trail_map_record_button_poi_add}
    private val playButton by lazy {layout.fragment_trail_map_record_button_play}

    /************************************Life cycle**********************************************/

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        Log.d(TAG_FRAGMENT, "Fragment '${javaClass.simpleName}' created")
        initializeAddPoiButton()
        initializePlayButton()
        return this.layout
    }

    override fun onDestroy() {
        super.onDestroy()
        isRecording=false
    }

    /************************************UI monitoring*******************************************/

    override fun getLayoutId(): Int = R.layout.fragment_trail_map_record

    override fun getMapViewId(): Int = R.id.fragment_trail_map_record_map_view

    override fun getInfoBottomSheetId(): Int = R.id.fragment_trail_map_record_bottom_sheet_info

    override fun getInfoFragmentId(): Int = R.id.fragment_trail_map_record_fragment_info

    override fun enableUI() {
        this.map?.uiSettings?.setAllGesturesEnabled(true)
        this.addPoiButton.isEnabled=true
        this.playButton.isEnabled=true
    }

    override fun disableUI() {
        this.map?.uiSettings?.setAllGesturesEnabled(false)
        this.addPoiButton.isEnabled=false
        this.playButton.isEnabled=false
    }

    private fun initializeAddPoiButton(){
        this.addPoiButton.setOnClickListener {
            addTrailPointOfInterest()
        }
    }

    private fun initializePlayButton(){
        this.playButton.setOnClickListener {
            when(this.isRecording){
                false -> startRecord()
                true -> stopRecord()
            }
        }
    }

    override fun revealActionsButtons(){
        this.actionsButtonsLayout.visibility=View.VISIBLE
        this.actionsButtonsLayout.layoutAnimation=
            AnimationUtils.loadLayoutAnimation(context, R.anim.layout_appear_right)
        this.actionsButtonsLayout.animate()
    }

    override fun hideActionsButtons() {

    }

    /***********************************Map monitoring*******************************************/

    override fun proceedAdditionalOnMapReadyActions() {
        this.map?.setInfoWindowAdapter(this)
        this.map?.setOnInfoWindowClickListener(this)
    }

    override fun onMapClick(point: LatLng?) {
        Log.d(TAG_MAP, "Click on map at point lat ${point?.latitude} lng ${point?.longitude}")
        hideInfoBottomSheet()
    }

    override fun onMarkerClick(marker: Marker?): Boolean {

        /*Shows an info nested fragment depending on the tag of the marker*/

        return when(marker?.tag){
            is TrailPointOfInterest->{
                Log.d(TAG_MAP, "Click on marker (TrailPointOfInterest)")
                val trailPointOfInterest=marker.tag as TrailPointOfInterest
                val trailPoiPosition=marker.snippet.toInt()
                showTrailPOIInfoFragment(trailPointOfInterest, trailPoiPosition)
                marker.showInfoWindow()
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

    override fun getInfoWindow(marker: Marker?): View? {
        return layoutInflater.inflate(R.layout.map_info_window_poi_remove, this.layout as ViewGroup, false)
    }

    override fun getInfoContents(marker: Marker?): View? {
        return null
    }

    override fun onInfoWindowClick(marker: Marker?) {
        if(marker?.tag is TrailPointOfInterest) {
            Log.d(TAG_MAP, "Click on info window (TrailPointOfInterest)")
            val trailPointOfInterest=marker.tag as TrailPointOfInterest
            removeTrailPointOfInterest(trailPointOfInterest)
        }
        else{
            Log.w(TAG_MAP, "Click on info window (Unknown category)")
        }
    }

    /*************************************Record actions*****************************************/

    /**
     * Starts recording the trail
     */

    private fun startRecord(){
        this.isRecording=true
        this.playButton.setImageResource(R.drawable.ic_record_pause_white)
        recordTrail()
    }

    /**
     * Stops recording the trail
     */

    private fun stopRecord(){
        this.isRecording=false
        this.playButton.setImageResource(R.drawable.ic_record_play_white)
    }

    /**
     * Records the trail
     * Runs a thread which registers a point each time the time interval is reached
     */

    private fun recordTrail(){
        Executors.newSingleThreadExecutor().execute {
            while(isRecording){
                registerUserLocation()
                var remainingTime= VALUE_RECORD_TIME_INTERVAL
                while(remainingTime > 0){
                    Thread.sleep(1000)
                    remainingTime-=1000
                }
            }
        }
    }

    /********************************Location monitoring*****************************************/

    /**
     * Gets the user location and use it to add a new trailPoint
     */

    private fun registerUserLocation(){

        /*Gets the user location*/

        this.userLocation.lastLocation
            .addOnSuccessListener { userLocation->
                val trailPoint= TrailPoint(
                    userLocation.latitude, userLocation.longitude, userLocation.altitude.toInt())

                /*If minimum distance to previous point is fulfilled, adds the new trailPoint*/

                if(checkMinDistanceToPreviousPointIsFulfilled(trailPoint)) {
                    Log.d(TAG_LOCATION,
                        "Point registered at lat ${trailPoint.latitude} lng ${trailPoint.longitude}")
                    addTrailPoint(trailPoint)
                }
                else{
                    Log.d(TAG_LOCATION,
                        "Point not registered, too closed to the previous point")
                }
            }
            .addOnFailureListener { e->
                //TODO handle
                Log.w(TAG_LOCATION, e.message.toString())
            }
    }

    /**
     * Checks that the distance between a trailPoint and the previous registered point is
     * higher than the minimum required distance
     * @param trailPoint : the new trailPoint to register
     * @return true if the minimum distance is fulfilled or if no point was previously recorded
     */

    private fun checkMinDistanceToPreviousPointIsFulfilled(trailPoint:TrailPoint):Boolean{
        return if(!this.trail?.trailTrack?.trailPoints.isNullOrEmpty()) {
            val previousPoint = this.trail?.trailTrack?.trailPoints?.last()
            if (previousPoint != null) {
                val pointA = LatLng(trailPoint.latitude, trailPoint.longitude)
                val pointB = LatLng(previousPoint.latitude, previousPoint.longitude)
                GeoUtilities.getDistance(pointA, pointB) >= VALUE_RECORD_MIN_DISTANCE
            } else {
                true
            }
        }else{
            true
        }
    }
}
