package com.sildian.apps.togetrail.trail.infoEdit

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.common.baseControllers.BaseActivity
import com.sildian.apps.togetrail.common.baseControllers.BaseImagePickerFragment
import com.sildian.apps.togetrail.common.baseViewModels.ViewModelFactory
import com.sildian.apps.togetrail.location.model.core.Location
import com.sildian.apps.togetrail.location.search.LocationSearchActivity
import com.sildian.apps.togetrail.trail.model.core.Trail
import com.sildian.apps.togetrail.trail.model.support.TrailViewModel
import kotlinx.android.synthetic.main.activity_trail_info_edit.*

/*************************************************************************************************
 * This activity allows the user to edit information about a trail or a trailPointOfInterest
 ************************************************************************************************/

class TrailInfoEditActivity : BaseActivity() {

    /**********************************Static items**********************************************/

    companion object{

        /**Fragments Ids***/
        private const val ID_FRAGMENT_TRAIL_INFO_EDIT=1
        private const val ID_FRAGMENT_TRAIL_POI_INFO_EDIT=2

        /**Trail actions defining what the user is performing**/
        const val ACTION_TRAIL_EDIT_INFO=1
        const val ACTION_TRAIL_EDIT_POI_INFO=2

        /**Bundle keys for intent**/
        const val KEY_BUNDLE_TRAIL_ACTION="KEY_BUNDLE_TRAIL_ACTION"                 //Action to perform (see above) -> Mandatory
        const val KEY_BUNDLE_TRAIL="KEY_BUNDLE_TRAIL"                               //Trail -> Mandatory
        const val KEY_BUNDLE_TRAIL_POI_POSITION="KEY_BUNDLE_TRAIL_POI_POSITION"     //Trail poi position -> Optional (only if a poi is edited)

        /**Request keys for intent**/
        private const val KEY_REQUEST_LOCATION_SEARCH=1001
    }

    /**************************************Data**************************************************/

    private var currentAction= ACTION_TRAIL_EDIT_INFO               //Action defining what the user is performing
    private lateinit var trailViewModel: TrailViewModel             //Trail data
    private var trailPOIPosition: Int? = null                       //The trail POI position if needed

    /**********************************UI component**********************************************/

    private val toolbar by lazy {activity_trail_info_edit_toolbar}
    private var fragment: BaseImagePickerFragment?=null

    /************************************Life cycle**********************************************/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startTrailEditAction()
    }

    /********************************Navigation control******************************************/

    override fun onBackPressed() {
        if(this.fragment?.getAddPhotoBottomSheetState()!=BottomSheetBehavior.STATE_HIDDEN){
            this.fragment?.hideAddPhotoBottomSheet()
        }else {
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
        menuInflater.inflate(R.menu.menu_save, menu)
        return true
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
        this.trailViewModel= ViewModelProviders
            .of(this, ViewModelFactory)
            .get(TrailViewModel::class.java)
        readDataFromIntent()
    }

    override fun saveData() {
        this.fragment?.saveData()
    }

    private fun readDataFromIntent(){
        if(intent!=null){
            if(intent.hasExtra(KEY_BUNDLE_TRAIL_ACTION)){
                this.currentAction=intent.getIntExtra(
                    KEY_BUNDLE_TRAIL_ACTION, ACTION_TRAIL_EDIT_INFO)
            }
            if(intent.hasExtra(KEY_BUNDLE_TRAIL)){
                val trail:Trail?=intent.getParcelableExtra(KEY_BUNDLE_TRAIL)
                trail?.let { trailToInit ->
                    this.trailViewModel.initWithTrail(trailToInit)
                }
            }
            if(intent.hasExtra(KEY_BUNDLE_TRAIL_POI_POSITION)){
                this.trailPOIPosition = intent.getIntExtra(KEY_BUNDLE_TRAIL_POI_POSITION, 0)
            }
        }
    }

    /*************************************UI monitoring******************************************/

    override fun getLayoutId(): Int = R.layout.activity_trail_info_edit

    override fun initializeUI() {
        initializeToolbar()
    }

    private fun initializeToolbar(){
        setSupportActionBar(this.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        when(this.currentAction){
            ACTION_TRAIL_EDIT_INFO -> supportActionBar?.setTitle(R.string.toolbar_trail_info)
            ACTION_TRAIL_EDIT_POI_INFO -> supportActionBar?.setTitle(R.string.toolbar_trail_poi_info)
        }
    }

    /***********************************Trail edit actions***************************************/

    private fun startTrailEditAction(){
        when(this.currentAction){
            ACTION_TRAIL_EDIT_INFO -> showFragment(ID_FRAGMENT_TRAIL_INFO_EDIT)
            ACTION_TRAIL_EDIT_POI_INFO -> showFragment(ID_FRAGMENT_TRAIL_POI_INFO_EDIT)
        }
    }

    /*******************************Location monitoring******************************************/

    fun searchLocation(){
        startLocationSearchActivity()
    }

    private fun updateLocation(location: Location){
        this.fragment?.updateData(location)
    }

    /******************************Fragments monitoring******************************************/

    private fun showFragment(fragmentId:Int){
        when(fragmentId){
            ID_FRAGMENT_TRAIL_INFO_EDIT ->
                this.fragment=
                    TrailInfoEditFragment(
                        this.trailViewModel
                    )
            ID_FRAGMENT_TRAIL_POI_INFO_EDIT ->
                this.fragment =
                    TrailPOIInfoEditFragment(
                        this.trailViewModel, this.trailPOIPosition
                    )
        }
        this.fragment?.let { fragment ->
            supportFragmentManager.beginTransaction()
                .replace(R.id.activity_trail_info_edit_fragment, fragment).commit()
        }
    }

    /*************************************Navigation*********************************************/

    private fun startLocationSearchActivity(){
        val locationSearchActivityIntent=Intent(this, LocationSearchActivity::class.java)
        startActivityForResult(locationSearchActivityIntent, KEY_REQUEST_LOCATION_SEARCH)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode== KEY_REQUEST_LOCATION_SEARCH){
            if(data!=null&&data.hasExtra(LocationSearchActivity.KEY_BUNDLE_LOCATION)){
                val location=data.getParcelableExtra<Location>(LocationSearchActivity.KEY_BUNDLE_LOCATION)
                location?.let { loc -> updateLocation(loc) }
            }
        }
    }

    override fun finishOk(){
        val resultIntent=Intent()
        resultIntent.putExtra(KEY_BUNDLE_TRAIL, this.trailViewModel.trail.value)
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }
}
