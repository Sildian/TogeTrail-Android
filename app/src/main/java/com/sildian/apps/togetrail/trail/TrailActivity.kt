package com.sildian.apps.togetrail.trail

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.main.MainActivity
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

        /**Request keys for intent**/
        const val KEY_REQUEST_LOAD_GPX=1001
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

    /***********************************Data monitoring******************************************/

    private fun readDataFromIntent(intent: Intent?){
        if(intent!=null){
            if(intent.hasExtra(MainActivity.KEY_BUNDLE_TRAIL_ACTION)){
                this.currentAction=intent.getIntExtra(MainActivity.KEY_BUNDLE_TRAIL_ACTION, ACTION_TRAIL_SEE)
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
                this.fragment.updateCurrentTrail(this.trail)
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

    /******************************Fragments monitoring******************************************/

    /**
     * Shows a fragment
     * @param fragmentId : defines which fragment to display (choice within ID_FRAGMENT_xxx)
     */

    private fun showFragment(fragmentId:Int){
        when(fragmentId){
            ID_FRAGMENT_TRAIL_DETAIL ->this.fragment=TrailDetailFragment()
            //TODO handle other fragments
        }
        supportFragmentManager.beginTransaction()
            .replace(R.id.activity_trail_fragment, this.fragment).commit()
    }

    /***********************************Trail actions********************************************/

    private fun startTrailAction(){
        when(this.currentAction){
            ACTION_TRAIL_SEE->
                showFragment(ID_FRAGMENT_TRAIL_DETAIL)
            ACTION_TRAIL_CREATE_FROM_GPX->{
                showFragment(ID_FRAGMENT_TRAIL_DETAIL)
                loadGpx()
            }
            //TODO handle other cases
        }
    }

    /***********************************Navigation***********************************************/

    private fun loadGpx(){
        val loadGpxIntent=Intent(Intent.ACTION_OPEN_DOCUMENT)
        loadGpxIntent.addCategory(Intent.CATEGORY_OPENABLE)
        loadGpxIntent.type="*/*"
        startActivityForResult(loadGpxIntent, KEY_REQUEST_LOAD_GPX)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode== Activity.RESULT_OK){
            if(requestCode== KEY_REQUEST_LOAD_GPX){
                val uri=data?.data
                createTrailFromGpx(uri)
            }
        }else{
            //TODO handle
        }
    }
}
