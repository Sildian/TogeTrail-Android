package com.sildian.apps.togetrail.trail.map

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.view.Menu
import android.view.MenuItem
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.common.baseControllers.BaseActivity
import com.sildian.apps.togetrail.common.baseViewModels.ViewModelFactory
import com.sildian.apps.togetrail.common.utils.cloudHelpers.AuthFirebaseHelper
import com.sildian.apps.togetrail.common.utils.uiHelpers.DialogHelper
import com.sildian.apps.togetrail.hiker.profile.ProfileActivity
import com.sildian.apps.togetrail.trail.infoEdit.TrailInfoEditActivity
import com.sildian.apps.togetrail.trail.model.core.Trail
import com.sildian.apps.togetrail.trail.model.support.TrailBuildException
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
    private var isEditable = false
    private var isTrailLoaded = false
    private lateinit var trailViewModel: TrailViewModel

    /**********************************UI component**********************************************/

    private val toolbar by lazy {activity_trail_toolbar}
    private var fragment: BaseTrailMapFragment<out ViewDataBinding>?=null

    /********************************Navigation control******************************************/

    override fun onBackPressed() {
        when{
            this.fragment?.getInfoBottomSheetState()!=BottomSheetBehavior.STATE_HIDDEN->
                this.fragment?.hideInfoBottomSheet()
            else->
                finishCancel()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finishCancel()
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

    override fun loadData() {
        initializeData()
        observeTrail()
        observeRequestFailure()
        readDataFromIntent()
    }

    private fun initializeData() {
        this.trailViewModel= ViewModelProviders
            .of(this, ViewModelFactory)
            .get(TrailViewModel::class.java)
    }

    private fun observeTrail() {
        this.trailViewModel.trail.observe(this) { trail ->
            if (!isTrailLoaded) {
                isTrailLoaded = true
                if (currentAction == ACTION_TRAIL_SEE && trail != null) {
                    startTrailAction()
                }
            }
        }
    }

    private fun observeRequestFailure() {
        this.trailViewModel.requestFailure.observe(this) { e ->
            if (currentAction == ACTION_TRAIL_SEE && e != null) {
                onQueryError(e)
            }
        }
    }

    private fun readDataFromIntent(){
        if(intent!=null){
            if(intent.hasExtra(KEY_BUNDLE_TRAIL_ACTION)){
                this.currentAction=
                    intent.getIntExtra(KEY_BUNDLE_TRAIL_ACTION, ACTION_TRAIL_SEE)
            }
            if(intent.hasExtra(KEY_BUNDLE_TRAIL_ID)) {
                val trailId = intent.getStringExtra(KEY_BUNDLE_TRAIL_ID)
                trailId?.let { id ->
                    this.trailViewModel.loadTrailFromDatabaseRealTime(id)
                }
            }
            else {
                startTrailAction()
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
            catch(e: TrailBuildException) {
                e.printStackTrace()
                when (e.errorCode) {
                    TrailBuildException.ErrorCode.NO_TRACK ->
                        DialogHelper.createInfoDialog(
                            this,
                            R.string.message_file_failure,
                            R.string.message_file_failure_gpx_no_track
                        ).show()
                    TrailBuildException.ErrorCode.TOO_MANY_TRACKS ->
                        DialogHelper.createInfoDialog(
                            this,
                            R.string.message_file_failure,
                            R.string.message_file_failure_gpx_too_many_tracks
                        ).show()
                }
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

    override fun saveData() {
        this.fragment?.saveData()
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

    private fun showFragment(fragmentId:Int){
        when(fragmentId){
            ID_FRAGMENT_TRAIL_DETAIL ->
                this.fragment= TrailMapDetailFragment(this.trailViewModel, this.isEditable)
            ID_FRAGMENT_TRAIL_DRAW ->
                this.fragment = TrailMapDrawFragment(this.trailViewModel)
            ID_FRAGMENT_TRAIL_RECORD ->
                this.fragment = TrailMapRecordFragment(this.trailViewModel)
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
                this.isEditable=AuthFirebaseHelper.getCurrentUser()?.uid==this.trailViewModel.trail.value?.authorId
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

    fun seeHiker(hikerId: String) {
        startProfileActivity(hikerId)
    }

    /***********************************Navigation***********************************************/

    private fun startLoadGpx(){
        val loadGpxIntent=Intent(Intent.ACTION_OPEN_DOCUMENT)
        loadGpxIntent.addCategory(Intent.CATEGORY_OPENABLE)
        loadGpxIntent.type="*/*"
        startActivityForResult(loadGpxIntent, KEY_REQUEST_LOAD_GPX)
    }

    private fun startProfileActivity(hikerId: String) {
        val profileActivityIntent = Intent(this, ProfileActivity::class.java)
        profileActivityIntent.putExtra(ProfileActivity.KEY_BUNDLE_HIKER_ID, hikerId)
        startActivity(profileActivityIntent)
    }

    //TODO it the trail is too big, it may fail to pass to the intent. An other way needs to be found to edit a trail.

    private fun startTrailInfoEditActivity(trailEditActionId:Int, trailPointOfInterestPosition:Int?){
        val trailInfoEditActivityIntent=Intent(this, TrailInfoEditActivity::class.java)
        trailInfoEditActivityIntent.putExtra(TrailInfoEditActivity.KEY_BUNDLE_TRAIL_ACTION, trailEditActionId)
        trailInfoEditActivityIntent.putExtra(TrailInfoEditActivity.KEY_BUNDLE_TRAIL, this.trailViewModel.trail.value)
        if(trailPointOfInterestPosition!=null) {
            trailInfoEditActivityIntent
                .putExtra(TrailInfoEditActivity.KEY_BUNDLE_TRAIL_POI_POSITION, trailPointOfInterestPosition)
        }
        startActivityForResult(trailInfoEditActivityIntent, KEY_REQUEST_EDIT_TRAIL_INFO)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode) {
            KEY_REQUEST_LOAD_GPX -> handleLoadGpxResult(resultCode, data)
            KEY_REQUEST_EDIT_TRAIL_INFO -> handleTrailInfoEditResult(resultCode, data)
        }
    }

    private fun handleLoadGpxResult(resultCode: Int, data:Intent?){
        when (resultCode) {
            Activity.RESULT_OK -> {
                val uri = data?.data
                createTrailFromGpx(uri)
            }
            Activity.RESULT_CANCELED ->
                finishCancel()
        }
    }

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
