package com.sildian.apps.togetrail.trail.map

import android.graphics.drawable.AnimationDrawable
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.Polyline

import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.common.utils.DateUtilities
import com.sildian.apps.togetrail.trail.model.Trail
import com.sildian.apps.togetrail.trail.model.TrailPoint
import kotlinx.android.synthetic.main.fragment_trail_map_record.view.*
import java.util.*
import java.util.concurrent.Executors

/*************************************************************************************************
 * Lets the user record a trail in real time
 ************************************************************************************************/

class TrailMapRecordFragment : BaseTrailMapFragment() {

    /**********************************Static items**********************************************/

    companion object{

        /**Record data**/ //TODO change the value later
        private const val RECORD_TIME_INTERVAL=5000     //The time interval between each point record (in milliseconds)
    }

    /***************************************Data*************************************************/

    private var isRecording=false                       //True when the trail is being recorded

    /**********************************UI component**********************************************/

    private val playButton by lazy {layout.fragment_trail_map_record_button_play}

    /************************************Life cycle**********************************************/

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        Log.d(TAG_FRAGMENT, "Fragment '${javaClass.simpleName}' created")
        this.trail= Trail()
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

    private fun initializePlayButton(){
        this.playButton.setOnClickListener {
            when(this.isRecording){
                false -> startRecord()
                true -> stopRecord()
            }
        }
    }

    /***********************************Map monitoring*******************************************/

    override fun proceedAdditionalOnMapReadyActions() {

    }

    override fun onMapClick(point: LatLng?) {

    }

    override fun onMarkerClick(marker: Marker?): Boolean {
        return true
    }

    override fun onPolylineClick(polyline: Polyline?) {

    }

    /*************************************Record actions*****************************************/

    /**
     * Starts recording the trail
     */

    private fun startRecord(){
        this.isRecording=true
        this.playButton.setImageResource(R.drawable.anim_record_play)
        val animation=this.playButton.drawable as AnimationDrawable
        animation.start()
        recordTrail()
    }

    /**
     * Stops recording the trail
     */

    private fun stopRecord(){
        this.isRecording=false
        this.playButton.setImageResource(R.drawable.anim_record_pause)
        val animation=this.playButton.drawable as AnimationDrawable
        animation.start()
    }

    /**
     * Records the trail
     * Runs a thread which registers a point each time the time interval is reached
     */

    private fun recordTrail(){
        Executors.newSingleThreadExecutor().execute {
            while(isRecording){
                getUserLocation()
                var remainingTime= RECORD_TIME_INTERVAL
                while(remainingTime > 0){
                    Thread.sleep(1000)
                    remainingTime-=1000
                }
            }
        }
    }

    /********************************Location monitoring*****************************************/

    private fun getUserLocation(){
        this.userLocation.lastLocation
            .addOnSuccessListener { userLocation->
                Log.d(TAG_LOCATION,
                    "Point registered at lat ${userLocation.latitude} lng ${userLocation.longitude}")
                addTrailPoint(userLocation)
            }
            .addOnFailureListener { e->
                //TODO handle
                Log.w(TAG_LOCATION, e.message.toString())
            }
    }

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
                    LatLng(lastPoint.latitude, lastPoint.longitude))
            )
        }
    }

    /**
     * Adds a trailPoint to the current trail
     * @param point : the point to be added
     */

    private fun addTrailPoint(point: Location){

        /*Adds a new trailPoint and updates the track on the map*/

        val trailPoint= TrailPoint(point.latitude, point.longitude, point.altitude.toInt(), Date())
        this.trail?.trailTrack?.trailPoints?.add(trailPoint)
        this.map?.clear()
        showTrailTrackOnMap()

        /*If this is the first trailPoint, reveals the actions buttons*/

        /*if(this.trail?.trailTrack?.trailPoints?.size==1) {
            revealActionsButtons()
        }*/
    }

    /******************************Nested Fragments monitoring***********************************/

    override fun showDefaultInfoFragment() {

    }
}
