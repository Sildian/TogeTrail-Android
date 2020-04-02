package com.sildian.apps.togetrail.trail.map

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.common.flows.BaseDataFlowActivity
import com.sildian.apps.togetrail.common.utils.cloudHelpers.UserFirebaseHelper
import com.sildian.apps.togetrail.common.utils.uiHelpers.DialogHelper
import com.sildian.apps.togetrail.hiker.model.core.Hiker
import com.sildian.apps.togetrail.hiker.model.core.HikerHistoryItem
import com.sildian.apps.togetrail.hiker.model.core.HikerHistoryType
import com.sildian.apps.togetrail.hiker.model.support.HikerFirebaseQueries
import com.sildian.apps.togetrail.trail.infoEdit.TrailInfoEditActivity
import com.sildian.apps.togetrail.trail.model.core.Trail
import com.sildian.apps.togetrail.trail.model.support.TrailBuilder
import com.sildian.apps.togetrail.trail.model.support.TrailFirebaseQueries
import io.ticofab.androidgpxparser.parser.GPXParser
import kotlinx.android.synthetic.main.activity_trail.*
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.util.*

/*************************************************************************************************
 * This activity monitors the trails and lets the user see or edit a trail
 ************************************************************************************************/

class TrailActivity : BaseDataFlowActivity() {

    /**********************************Static items**********************************************/

    companion object{

        /**Logs**/
        private const val TAG="TrailActivity"

        /**Fragments Ids***/
        private const val ID_FRAGMENT_TRAIL_DETAIL=1
        private const val ID_FRAGMENT_TRAIL_DRAW=2
        private const val ID_FRAGMENT_TRAIL_RECORD=3

        /**Trail actions defining what the user is performing**/
        const val ACTION_TRAIL_SEE=1
        const val ACTION_TRAIL_CREATE_FROM_GPX=2
        const val ACTION_TRAIL_DRAW=3
        const val ACTION_TRAIL_RECORD=4

        /**Request keys for intent**/
        private const val KEY_REQUEST_LOAD_GPX=1001
        private const val KEY_REQUEST_EDIT_TRAIL_INFO=1002

        /**Bundle keys for intent**/
        const val KEY_BUNDLE_TRAIL_ACTION="KEY_BUNDLE_TRAIL_ACTION"
        const val KEY_BUNDLE_TRAIL="KEY_BUNDLE_TRAIL"
        const val KEY_BUNDLE_HIKER="KEY_BUNDLE_HIKER"
    }

    /**************************************Data**************************************************/

    private var currentAction= ACTION_TRAIL_SEE                 //Action defining what the user is performing
    private var trail: Trail?=null                              //Current trail shown
    private var hiker: Hiker?=null                              //The current hiker

    /**********************************UI component**********************************************/

    private val toolbar by lazy {activity_trail_toolbar}
    private lateinit var fragment: BaseTrailMapFragment
    private lateinit var progressDialog:AlertDialog

    /************************************Life cycle**********************************************/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "Activity '${javaClass.simpleName}' created")
        setContentView(R.layout.activity_trail)
        loadData()
        initializeToolbar()
        startTrailAction()
    }

    /********************************Navigation control******************************************/

    override fun onBackPressed() {
        when{
            this.fragment.getInfoBottomSheetState()!=BottomSheetBehavior.STATE_HIDDEN->
                this.fragment.hideInfoBottomSheet()
            else->
                finishCancel() //TODO ask the user if he wants to save
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        //TODO ask the user if he wants to save
        finishCancel()
        return true
    }

    /********************************Menu monitoring*********************************************/

    /**Generates the menu within the toolbar**/

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_save, menu)
        return true
    }

    /**Click on menu item from toolbar**/

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.d(TAG, "Menu '${item.title}' clicked")
        if(item.groupId==R.id.menu_save){
            if(item.itemId==R.id.menu_save_save){
                saveData()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /***********************************Data monitoring******************************************/

    override fun loadData() {
        readDataFromIntent()
    }

    override fun saveData() {
        this.fragment.saveData()
    }

    private fun readDataFromIntent(){
        if(intent!=null){
            if(intent.hasExtra(KEY_BUNDLE_TRAIL_ACTION)){
                this.currentAction=
                    intent.getIntExtra(KEY_BUNDLE_TRAIL_ACTION, ACTION_TRAIL_SEE)
            }
            if(intent.hasExtra(KEY_BUNDLE_TRAIL)){
                this.trail=
                    intent.getParcelableExtra(KEY_BUNDLE_TRAIL)
            }
            if(intent.hasExtra(KEY_BUNDLE_HIKER)){
                this.hiker=intent.getParcelableExtra(KEY_BUNDLE_HIKER)
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
                this.trail=
                    TrailBuilder
                        .withGpx(gpx)
                        .build()
                this.fragment.updateTrailAndShowTrackAndInfo(this.trail)
            }

            /*Handles exceptions*/

            catch(e:IOException){
                e.printStackTrace()
                //TODO handle
            }
            catch(e:XmlPullParserException){
                e.printStackTrace()
                //TODO handle
            }
            catch(e: TrailBuilder.TrailBuildNoTrackException){
                e.printStackTrace()
                //TODO handle
            }
            catch(e: TrailBuilder.TrailBuildTooManyTracksException){
                e.printStackTrace()
                //TODO handle
            }
        }
        else{
            //TODO handle
        }
    }

    private fun updateTrail(trail: Trail?){
        this.trail=trail
        this.fragment.updateTrailAndShowTrack(this.trail)
    }

    fun saveTrail(trail: Trail?){

        /*Updates the trail*/

        updateTrail(trail)
        this.trail?.lastUpdate= Date()

        /*Shows a progress dialog*/

        this.progressDialog=DialogHelper.createProgressDialog(this)
        this.progressDialog.show()

        /*If the trail has no id, it means it was not created in the database yet. Then creates it.*/

        if(this.trail?.id==null){

            this.trail?.authorId=UserFirebaseHelper.getCurrentUser()?.uid

            TrailFirebaseQueries.createTrail(this.trail!!)
                .addOnSuccessListener { documentReference->

                    /*Once created, updates it with the created id*/

                    this.trail?.id=documentReference.id
                    Log.d(TAG, "Trail '${this.trail?.id}' created in the database")

                    TrailFirebaseQueries.updateTrail(this.trail!!)
                        .addOnSuccessListener {
                            progressDialog.dismiss()
                            //TODO show a snackbar when finished

                            /*Also updates the hiker and the history*/

                            this.hiker!!.nbTrailsCreated++
                            val historyItem= HikerHistoryItem(
                                HikerHistoryType.TRAIL_CREATED,
                                this.trail?.creationDate!!,
                                this.trail?.id!!,
                                this.trail?.name!!,
                                this.trail?.location?.toString(),
                                this.trail?.getFirstPhotoUrl()
                            )
                            HikerFirebaseQueries.createOrUpdateHiker(this.hiker!!)
                            HikerFirebaseQueries.addHistoryItem(this.hiker!!.id, historyItem)

                            finishOk()
                        }
                        .addOnFailureListener { e ->
                            Log.w(TAG, e.message.toString())
                            progressDialog.dismiss()
                            //TODO handle
                            finishOk()
                        }
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, e.message.toString())
                    progressDialog.dismiss()
                    //TODO handle
                    finishCancel()
                }

            /*Else updates it*/

        }else{
            TrailFirebaseQueries.updateTrail(this.trail!!)
                .addOnSuccessListener {
                    Log.d(TAG, "Trail '${this.trail?.id}' updated in the database")
                    progressDialog.dismiss()
                    //TODO show a snackbar when finished
                    finishOk()
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, e.message.toString())
                    progressDialog.dismiss()
                    //TODO handle
                    finishCancel()
                }
        }
    }

    /*************************************UI monitoring******************************************/

    private fun initializeToolbar(){
        setSupportActionBar(this.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        when(this.currentAction){
            ACTION_TRAIL_SEE -> supportActionBar?.setTitle(R.string.toolbar_trail)
            ACTION_TRAIL_CREATE_FROM_GPX -> supportActionBar?.setTitle(R.string.toolbar_trail)
            ACTION_TRAIL_DRAW -> supportActionBar?.setTitle(R.string.toolbar_trail_draw)
            ACTION_TRAIL_RECORD -> supportActionBar?.setTitle(R.string.toolbar_trail_record)
        }
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
            ACTION_TRAIL_SEE -> {
                showFragment(ID_FRAGMENT_TRAIL_DETAIL)
                this.fragment.updateTrail(this.trail)
            }
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

    fun updateTrailAndEditInfo(trail: Trail){
        updateTrail(trail)
        startTrailInfoEditActivity(TrailInfoEditActivity.ACTION_TRAIL_EDIT_INFO, null)
    }

    fun updateTrailAndEditPoiInfo(trail: Trail, poiPosition:Int){
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
        trailInfoEditActivityIntent.putExtra(TrailInfoEditActivity.KEY_BUNDLE_TRAIL_ACTION, trailEditActionId)
        trailInfoEditActivityIntent.putExtra(TrailInfoEditActivity.KEY_BUNDLE_TRAIL, this.trail)
        if(trailPointOfInterestPosition!=null) {
            trailInfoEditActivityIntent
                .putExtra(TrailInfoEditActivity.KEY_BUNDLE_TRAIL_POI_POSITION, trailPointOfInterestPosition)
        }
        trailInfoEditActivityIntent.putExtra(TrailInfoEditActivity.KEY_BUNDLE_HIKER, this.hiker)
        startActivityForResult(trailInfoEditActivityIntent, KEY_REQUEST_EDIT_TRAIL_INFO)
    }

    /**Activity result**/

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
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
            if(data!=null){
                if(data.hasExtra(TrailInfoEditActivity.KEY_BUNDLE_TRAIL)) {
                    val updatedTrail = data.getParcelableExtra<Trail>(TrailInfoEditActivity.KEY_BUNDLE_TRAIL)!!
                    updateTrail(updatedTrail)
                }
                if(data.hasExtra(TrailInfoEditActivity.KEY_BUNDLE_HIKER)) {
                    this.hiker=data.getParcelableExtra(TrailInfoEditActivity.KEY_BUNDLE_HIKER)
                }
            }
        }
    }

    /**Finish with ok result (the trail is saved)**/

    private fun finishOk(){
        val resultIntent=Intent()
        resultIntent.putExtra(KEY_BUNDLE_HIKER, this.hiker)
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }

    /**Finish with cancel result (the trail is not saved)**/

    private fun finishCancel(){
        setResult(Activity.RESULT_CANCELED)
        finish()
    }
}
