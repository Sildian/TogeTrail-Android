package com.sildian.apps.togetrail.trail.map

import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.Polyline

import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.trail.model.Trail
import kotlinx.android.synthetic.main.fragment_trail_map_record.view.*
import java.util.concurrent.Executors

/*************************************************************************************************
 * Lets the user record a trail in real time
 ************************************************************************************************/

class TrailMapRecordFragment : BaseTrailMapFragment() {

    /**********************************Static items**********************************************/

    companion object{

        /**Logs**/
        const val TAG_RECORD="TAG_RECORD"

        /**Record data**/
        private const val RECORD_TIME_INTERVAL=5000     //The time interval between each point record (in milliseconds)
    }

    /***************************************Data*************************************************/

    private var isRecording=false                       //True when the trail is recording

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
                Log.d(TAG_RECORD, "RECORD")
                var remainingTime= RECORD_TIME_INTERVAL
                while(remainingTime > 0){
                    Thread.sleep(1000)
                    remainingTime-=1000
                }
            }
        }
    }

    /***********************************Trail monitoring*****************************************/

    /******************************Nested Fragments monitoring***********************************/

    override fun showDefaultInfoFragment() {

    }
}
