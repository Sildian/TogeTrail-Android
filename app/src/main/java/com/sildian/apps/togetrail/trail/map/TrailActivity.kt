package com.sildian.apps.togetrail.trail.map

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.main.MainActivity
import com.sildian.apps.togetrail.trail.infoEdit.TrailInfoEditActivity
import com.sildian.apps.togetrail.trail.model.Trail
import com.sildian.apps.togetrail.trail.model.TrailFactory
import io.ticofab.androidgpxparser.parser.GPXParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException

/*************************************************************************************************
 * This activity monitors the trails and lets the user see or edit a trail
 ************************************************************************************************/

class TrailActivity : AppCompatActivity() {

    /**********************************Static items**********************************************/

    companion object{

        /**Logs**/
        const val TAG_ACTIVITY="TAG_ACTIVITY"
        const val TAG_FILE="TAG_FILE"

        /**Fragments Ids***/
        const val ID_FRAGMENT_TRAIL_DETAIL=1
        const val ID_FRAGMENT_TRAIL_DRAW=2
        const val ID_FRAGMENT_TRAIL_RECORD=3

        /**Trail actions defining what the user is performing**/
        const val ACTION_TRAIL_SEE=1
        const val ACTION_TRAIL_CREATE_FROM_GPX=2
        const val ACTION_TRAIL_DRAW=3
        const val ACTION_TRAIL_RECORD=4

        /**Bundle keys for intent**/
        const val KEY_BUNDLE_TRAIL_EDIT_ACTION="KEY_BUNDLE_TRAIL_EDIT_ACTION"
        const val KEY_BUNDLE_TRAIL="KEY_BUNDLE_TRAIL"
        const val KEY_BUNDLE_TRAIL_POI_POSITION="KEY_BUNDLE_TRAIL_POI_POSITION"

        /**Request keys for intent**/
        const val KEY_REQUEST_LOAD_GPX=1001
        const val KEY_REQUEST_EDIT_TRAIL_INFO=1002
    }

    /**************************************Data**************************************************/

    private var currentAction= ACTION_TRAIL_SEE                 //Action defining what the user is performing
    private var trail:Trail?=null                               //Current trail shown

    /**********************************UI component**********************************************/

    private lateinit var fragment: BaseTrailMapFragment

    /************************************Life cycle**********************************************/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG_ACTIVITY, "Activity '${javaClass.simpleName}' created")
        setContentView(R.layout.activity_trail)
        readDataFromIntent(intent)
        startTrailAction()
    }

    /********************************Navigation control******************************************/

    override fun onBackPressed() {
        when{
            this.fragment.getInfoBottomSheetState()!=BottomSheetBehavior.STATE_HIDDEN->
                this.fragment.hideInfoBottomSheet()
            else->
                super.onBackPressed()
        }
    }

    /***********************************Data monitoring******************************************/

    private fun readDataFromIntent(intent: Intent?){
        if(intent!=null){
            if(intent.hasExtra(MainActivity.KEY_BUNDLE_TRAIL_ACTION)){
                this.currentAction=
                    intent.getIntExtra(MainActivity.KEY_BUNDLE_TRAIL_ACTION, ACTION_TRAIL_SEE)
            }
        }
    }

    private fun createTrailFromGpx(uri:Uri?){

        if(uri!=null){

            /*Parses the gpx from the given uri, then updates the current trail*/

            val gpxParser= GPXParser()
            val inputStream = contentResolver.openInputStream(uri)
            try {
                val gpx = gpxParser.parse(inputStream)
                this.trail=TrailFactory.buildFromGpx(gpx)
                this.fragment.updateTrailAndShowInfo(this.trail)
            }

            /*Handles exceptions*/

            catch(e:IOException){
                Log.w(TAG_FILE, e.message.toString())
                //TODO handle
            }
            catch(e:XmlPullParserException){
                Log.w(TAG_FILE, e.message.toString())
                //TODO handle
            }
            catch(e:TrailFactory.TrailBuildNoTrackException){
                Log.w(TAG_FILE, e.message.toString())
                //TODO handle
            }
            catch(e:TrailFactory.TrailBuildTooManyTracksException){
                Log.w(TAG_FILE, e.message.toString())
                //TODO handle
            }
        }
        else{
            //TODO handle
        }
    }

    private fun updateTrail(trail:Trail){
        this.trail=trail
        this.fragment.updateTrail(this.trail)
    }

    /******************************Fragments monitoring******************************************/

    /**
     * Shows a fragment
     * @param fragmentId : defines which fragment to display (choice within ID_FRAGMENT_xxx)
     */

    private fun showFragment(fragmentId:Int){
        when(fragmentId){
            ID_FRAGMENT_TRAIL_DETAIL ->
                this.fragment= TrailMapDetailFragment()
            ID_FRAGMENT_TRAIL_DRAW ->
                this.fragment = TrailMapDrawFragment()
            ID_FRAGMENT_TRAIL_RECORD ->
                this.fragment = TrailMapRecordFragment()
        }
        supportFragmentManager.beginTransaction()
            .replace(R.id.activity_trail_fragment, this.fragment).commit()
    }

    /***********************************Trail actions********************************************/

    private fun startTrailAction(){
        when(this.currentAction){
            ACTION_TRAIL_SEE ->
                showFragment(ID_FRAGMENT_TRAIL_DETAIL)
            ACTION_TRAIL_CREATE_FROM_GPX ->{
                showFragment(ID_FRAGMENT_TRAIL_DETAIL)
                startLoadGpx()
            }
            ACTION_TRAIL_DRAW ->
                showFragment(ID_FRAGMENT_TRAIL_DRAW)
            ACTION_TRAIL_RECORD ->
                showFragment(ID_FRAGMENT_TRAIL_RECORD)
        }
    }

    fun updateTrailAndEditInfo(trail:Trail){
        updateTrail(trail)
        startTrailInfoEditActivity(TrailInfoEditActivity.ACTION_TRAIL_EDIT_INFO, null)
    }

    fun updateTrailAndEditPoiInfo(trail:Trail, poiPosition:Int){
        updateTrail(trail)
        startTrailInfoEditActivity(TrailInfoEditActivity.ACTION_TRAIL_EDIT_POI_INFO, poiPosition)
    }

    /***********************************Navigation***********************************************/

    /**
     * Starts loading a Gpx file
     */

    private fun startLoadGpx(){
        val loadGpxIntent=Intent(Intent.ACTION_OPEN_DOCUMENT)
        loadGpxIntent.addCategory(Intent.CATEGORY_OPENABLE)
        loadGpxIntent.type="*/*"
        startActivityForResult(loadGpxIntent, KEY_REQUEST_LOAD_GPX)
    }

    /**
     * Starts TrailInfoEditActivity
     * @param trailEditActionId : defines which action should be performed (among TrailInfoEditActivity.ACTION_TRAIL_xxx)
     * @param trailPointOfInterestPosition : if a trailPointOfInterest is edited, defines its position in the trailTrack
     */

    private fun startTrailInfoEditActivity(trailEditActionId:Int, trailPointOfInterestPosition:Int?){
        val trailInfoEditActivityIntent=Intent(this, TrailInfoEditActivity::class.java)
        trailInfoEditActivityIntent.putExtra(KEY_BUNDLE_TRAIL_EDIT_ACTION, trailEditActionId)
        trailInfoEditActivityIntent.putExtra(KEY_BUNDLE_TRAIL, this.trail)
        if(trailPointOfInterestPosition!=null) {
            trailInfoEditActivityIntent
                .putExtra(KEY_BUNDLE_TRAIL_POI_POSITION, trailPointOfInterestPosition)
        }
        startActivityForResult(trailInfoEditActivityIntent, KEY_REQUEST_EDIT_TRAIL_INFO)
    }

    /**Activity result**/

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(TAG_ACTIVITY, "Activity '$requestCode' resulted with $resultCode")
        when(requestCode) {
            KEY_REQUEST_LOAD_GPX -> handleLoadGpxResult(resultCode, data)
            KEY_REQUEST_EDIT_TRAIL_INFO -> handleTrailInfoEditResult(resultCode, data)
        }
    }

    /**Handles load gpx result**/

    private fun handleLoadGpxResult(resultCode: Int, data:Intent?){
        when (resultCode) {
            Activity.RESULT_OK -> {
                val uri = data?.data
                createTrailFromGpx(uri)
            }
            Activity.RESULT_CANCELED ->
                finish()
        }
    }

    /**Handles trail info edit result**/

    private fun handleTrailInfoEditResult(resultCode: Int, data: Intent?){
        if(resultCode== Activity.RESULT_OK){
            if(data!=null&&data.hasExtra(KEY_BUNDLE_TRAIL)) {
                val updatedTrail = data.getParcelableExtra<Trail>(KEY_BUNDLE_TRAIL)!!
                updateTrail(updatedTrail)
            }
        }
    }
}
