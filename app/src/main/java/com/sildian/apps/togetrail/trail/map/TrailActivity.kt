package com.sildian.apps.togetrail.trail.map

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.common.baseControllers.BaseActivity
import com.sildian.apps.togetrail.common.baseViewModels.ViewModelFactory
import com.sildian.apps.togetrail.common.utils.cloudHelpers.AuthFirebaseHelper
import com.sildian.apps.togetrail.common.utils.uiHelpers.DialogHelper
import com.sildian.apps.togetrail.trail.infoEdit.TrailInfoEditActivity
import com.sildian.apps.togetrail.trail.model.core.Trail
import com.sildian.apps.togetrail.trail.model.support.TrailBuilder
import com.sildian.apps.togetrail.trail.model.support.TrailViewModel
import io.ticofab.androidgpxparser.parser.GPXParser
import kotlinx.android.synthetic.main.activity_trail.*
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException

/*************************************************************************************************
 * This activity monitors the trails and lets the user see or edit a trail
 ************************************************************************************************/

class TrailActivity : BaseActivity() {

    /**********************************Static items**********************************************/

    companion object{

        /**Fragments Ids***/
        private const val ID_FRAGMENT_TRAIL_DETAIL=1
        private const val ID_FRAGMENT_TRAIL_DRAW=2
        private const val ID_FRAGMENT_TRAIL_RECORD=3

        /**Trail actions defining what the user is performing**/
        const val ACTION_TRAIL_SEE=1
        const val ACTION_TRAIL_CREATE_FROM_GPX=2
        const val ACTION_TRAIL_DRAW=3
        const val ACTION_TRAIL_RECORD=4

        /**Bundle keys for intent**/
        const val KEY_BUNDLE_TRAIL_ACTION="KEY_BUNDLE_TRAIL_ACTION"     //Action to perform (see above) -> Mandatory
        const val KEY_BUNDLE_TRAIL_ID="KEY_BUNDLE_TRAIL_ID"             //Trail id -> Optional, only if the trail already exists

        /**Request keys for intent**/
        private const val KEY_REQUEST_LOAD_GPX=1001
        private const val KEY_REQUEST_EDIT_TRAIL_INFO=1002
    }

    /**************************************Data**************************************************/

    private var currentAction= ACTION_TRAIL_SEE                 //Action defining what the user is performing
    private var isEditable = false                              //True if the trail is editable
    private lateinit var trailViewModel: TrailViewModel         //Trail data

    /**********************************UI component**********************************************/

    private val toolbar by lazy {activity_trail_toolbar}
    private var fragment: BaseTrailMapFragment?=null

    /********************************Navigation control******************************************/

    override fun onBackPressed() {
        when{
            this.fragment?.getInfoBottomSheetState()!=BottomSheetBehavior.STATE_HIDDEN->
                this.fragment?.hideInfoBottomSheet()
            else->
                finish()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    /********************************Menu monitoring*********************************************/

    /**Generates the menu within the toolbar**/

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return if(this.currentAction!= ACTION_TRAIL_SEE) {
            menuInflater.inflate(R.menu.menu_save, menu)
            true
        }
        else{
            true
        }
    }

    /**Click on menu item from toolbar**/

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.groupId==R.id.menu_save){
            if(item.itemId==R.id.menu_save_save){
                saveData()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /***********************************Data monitoring******************************************/

    /**Loads data**/

    override fun loadData() {
        this.trailViewModel= ViewModelProviders
            .of(this, ViewModelFactory)
            .get(TrailViewModel::class.java)
        readDataFromIntent()
    }

    /**Saves data**/

    override fun saveData() {
        this.fragment?.saveData()
    }

    /**Read data from intent**/

    private fun readDataFromIntent(){
        if(intent!=null){
            if(intent.hasExtra(KEY_BUNDLE_TRAIL_ACTION)){
                this.currentAction=
                    intent.getIntExtra(KEY_BUNDLE_TRAIL_ACTION, ACTION_TRAIL_SEE)
            }
            if(intent.hasExtra(KEY_BUNDLE_TRAIL_ID)) {
                val trailId = intent.getStringExtra(KEY_BUNDLE_TRAIL_ID)
                trailId?.let { id ->
                    this.trailViewModel.loadTrailFromDatabase(id, this::startTrailAction, this::handleQueryError)
                }
            }
            else {
                startTrailAction()
            }
        }
    }

    /**
     * Creates a Trail with a Gpx
     * @param uri : the gpx's uri
     */

    private fun createTrailFromGpx(uri:Uri?){

        if(uri!=null){

            /*Parses the gpx from the given uri, then updates the current trail*/

            val gpxParser= GPXParser()
            val inputStream = contentResolver.openInputStream(uri)
            try {
                val gpx = gpxParser.parse(inputStream)
                this.trailViewModel.initNewTrail(gpx)
            }

            /*Handles exceptions*/

            catch(e:IOException){
                e.printStackTrace()
                DialogHelper.createInfoDialog(
                    this,
                    R.string.message_file_failure,
                    R.string.message_file_failure_gpx_other_reason
                ).show()
            }
            catch(e:XmlPullParserException){
                e.printStackTrace()
                DialogHelper.createInfoDialog(
                    this,
                    R.string.message_file_failure,
                    R.string.message_file_failure_gpx_other_reason
                ).show()
            }
            catch(e: TrailBuilder.TrailBuildNoTrackException){
                e.printStackTrace()
                DialogHelper.createInfoDialog(
                    this,
                    R.string.message_file_failure,
                    R.string.message_file_failure_gpx_no_track
                ).show()
            }
            catch(e: TrailBuilder.TrailBuildTooManyTracksException){
                e.printStackTrace()
                DialogHelper.createInfoDialog(
                    this,
                    R.string.message_file_failure,
                    R.string.message_file_failure_gpx_too_many_tracks
                ).show()
            }
        }
        else{
            DialogHelper.createInfoDialog(
                this,
                R.string.message_file_failure,
                R.string.message_file_failure_gpx_no_track
            ).show()
        }
    }

    /*************************************UI monitoring******************************************/

    override fun getLayoutId(): Int = R.layout.activity_trail

    override fun initializeUI() {
        initializeToolbar()
    }

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
                this.fragment= TrailMapDetailFragment(this.trailViewModel, this.isEditable)
            ID_FRAGMENT_TRAIL_DRAW ->
                this.fragment = TrailMapDrawFragment()
            ID_FRAGMENT_TRAIL_RECORD ->
                this.fragment = TrailMapRecordFragment()
        }
        this.fragment?.let { fragment ->
            supportFragmentManager.beginTransaction()
                .replace(R.id.activity_trail_fragment, fragment).commit()
        }
    }

    /***********************************Trail actions********************************************/

    private fun startTrailAction(){
        when(this.currentAction){
            ACTION_TRAIL_SEE -> {
                this.isEditable=AuthFirebaseHelper.getCurrentUser()?.uid==this.trailViewModel.trail?.authorId
                showFragment(ID_FRAGMENT_TRAIL_DETAIL)
            }
            ACTION_TRAIL_CREATE_FROM_GPX ->{
                this.isEditable=true
                showFragment(ID_FRAGMENT_TRAIL_DETAIL)
                startLoadGpx()
            }
            ACTION_TRAIL_DRAW -> {
                this.isEditable=true
                this.trailViewModel.initNewTrail()
                showFragment(ID_FRAGMENT_TRAIL_DRAW)
            }
            ACTION_TRAIL_RECORD -> {
                this.isEditable=true
                this.trailViewModel.initNewTrail()
                showFragment(ID_FRAGMENT_TRAIL_RECORD)
            }
        }
    }

    fun updateTrailAndEditInfo(trail: Trail){
        updateData(trail)
        startTrailInfoEditActivity(TrailInfoEditActivity.ACTION_TRAIL_EDIT_INFO, null)
    }

    fun updateTrailAndEditPoiInfo(trail: Trail, poiPosition:Int){
        updateData(trail)
        startTrailInfoEditActivity(TrailInfoEditActivity.ACTION_TRAIL_EDIT_POI_INFO, poiPosition)
    }

    /***********************************Navigation***********************************************/

    /**Starts loading a Gpx file**/

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
        trailInfoEditActivityIntent.putExtra(TrailInfoEditActivity.KEY_BUNDLE_TRAIL, this.trailViewModel.trail)
        if(trailPointOfInterestPosition!=null) {
            trailInfoEditActivityIntent
                .putExtra(TrailInfoEditActivity.KEY_BUNDLE_TRAIL_POI_POSITION, trailPointOfInterestPosition)
        }
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
                if(data.hasExtra(TrailInfoEditActivity.KEY_BUNDLE_TRAIL)){
                    val trail=data.getParcelableExtra<Trail>(TrailInfoEditActivity.KEY_BUNDLE_TRAIL)
                    trail?.let { trailToUpdate ->
                        this.trailViewModel.initWithTrail(trailToUpdate)
                    }
                }
            }
        }
    }
}
