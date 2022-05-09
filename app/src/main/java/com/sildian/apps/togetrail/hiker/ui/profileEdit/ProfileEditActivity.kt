package com.sildian.apps.togetrail.hiker.ui.profileEdit

import android.app.Activity
import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import androidx.databinding.ViewDataBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.common.baseControllers.BaseActivity
import com.sildian.apps.togetrail.common.baseControllers.BaseFragment
import com.sildian.apps.togetrail.common.baseControllers.BaseImagePickerFragment
import com.sildian.apps.togetrail.databinding.ActivityProfileEditBinding
import com.sildian.apps.togetrail.location.ui.search.LocationSearchActivity
import com.sildian.apps.togetrail.location.data.models.Location
import dagger.hilt.android.AndroidEntryPoint

/*************************************************************************************************
 * Lets a user see and edit his own profile
 ************************************************************************************************/

@AndroidEntryPoint
class ProfileEditActivity : BaseActivity<ActivityProfileEditBinding>() {

    /**********************************Static items**********************************************/

    companion object {

        /**Fragments ids**/
        private const val ID_FRAGMENT_INFO=1
        private const val ID_FRAGMENT_SETTINGS=2

        /**Actions to perform**/
        const val ACTION_PROFILE_EDIT_INFO=1
        const val ACTION_PROFILE_EDIT_SETTINGS=2

        /**Bundle keys for intents**/
        const val KEY_BUNDLE_PROFILE_ACTION="KEY_BUNDLE_PROFILE_ACTION"     //Which action to perform (see above) -> Mandatory
        const val KEY_BUNDLE_HIKER_ID="KEY_BUNDLE_HIKER_ID"                 //The hiker id -> Mandatory

        /**Request keys for intents**/
        private const val KEY_REQUEST_LOCATION_SEARCH=1001
    }

    /****************************************Data************************************************/

    private var currentAction= ACTION_PROFILE_EDIT_INFO         //Action defining what the user is performing
    private var hikerId:String?=null                            //The hiker's id

    /**********************************UI component**********************************************/

    private var fragment: BaseFragment<out ViewDataBinding>?=null

    /********************************Navigation control******************************************/

    override fun onBackPressed() {
        if(this.currentAction== ACTION_PROFILE_EDIT_INFO
            &&(this.fragment as BaseImagePickerFragment).getAddPhotoBottomSheetState()!=BottomSheetBehavior.STATE_HIDDEN){
                (this.fragment as BaseImagePickerFragment).hideAddPhotoBottomSheet()
            }
        else {
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

    /******************************Data monitoring************************************************/

    override fun initializeData() {
        readDataFromIntent()
    }

    override fun saveData() {
        this.fragment?.saveData()
    }

    private fun readDataFromIntent(){
        if(intent!=null){
            if(intent.hasExtra(KEY_BUNDLE_PROFILE_ACTION)){
                this.currentAction=intent.getIntExtra(KEY_BUNDLE_PROFILE_ACTION, ACTION_PROFILE_EDIT_INFO)
            }
            if(intent.hasExtra(KEY_BUNDLE_HIKER_ID)){
                this.hikerId= intent.getStringExtra(KEY_BUNDLE_HIKER_ID)
            }
        }
    }

    /******************************UI monitoring**************************************************/

    override fun getLayoutId(): Int = R.layout.activity_profile_edit

    override fun initializeUI() {
        initializeToolbar()
        startProfileAction()
    }

    private fun initializeToolbar(){
        setSupportActionBar(this.binding.activityProfileEditToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        when(this.currentAction){
            ACTION_PROFILE_EDIT_INFO ->
                supportActionBar?.setTitle(R.string.toolbar_hiker_my_profile)
            ACTION_PROFILE_EDIT_SETTINGS ->
                supportActionBar?.setTitle(R.string.toolbar_hiker_my_settings)
        }
    }

    /*********************************Starts profile action**************************************/

    private fun startProfileAction(){
        when(this.currentAction){
            ACTION_PROFILE_EDIT_INFO -> showFragment(ID_FRAGMENT_INFO)
            ACTION_PROFILE_EDIT_SETTINGS -> showFragment(ID_FRAGMENT_SETTINGS)
        }
    }

    /*******************************Location monitoring******************************************/

    fun searchLocation(){
        startLocationSearchActivity()
    }

    private fun updateLiveLocation(location: Location){
        this.fragment?.updateData(location)
    }

    /******************************Fragments monitoring******************************************/

    private fun showFragment(fragmentId:Int){
        when(fragmentId){
            ID_FRAGMENT_INFO ->
                this.fragment= ProfileInfoEditFragment(this.hikerId)
            ID_FRAGMENT_SETTINGS ->
                this.fragment=ProfileSettingsEditFragment(this.hikerId)
        }
        this.fragment?.let { fragment ->
            supportFragmentManager.beginTransaction()
                .replace(R.id.activity_profile_edit_fragment, fragment).commit()
        }
    }

    /*************************************Navigation*********************************************/

    private fun startLocationSearchActivity(){
        val locationSearchActivityIntent=Intent(this, LocationSearchActivity::class.java)
        startActivityForResult(locationSearchActivityIntent, KEY_REQUEST_LOCATION_SEARCH)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode) {
            KEY_REQUEST_LOCATION_SEARCH -> handleLocationSearchActivityResult(resultCode, data)
        }
    }

    private fun handleLocationSearchActivityResult(resultCode: Int, data: Intent?){
        if(resultCode== Activity.RESULT_OK) {
            if (data != null && data.hasExtra(LocationSearchActivity.KEY_BUNDLE_LOCATION)) {
                val location =
                    data.getParcelableExtra<Location>(LocationSearchActivity.KEY_BUNDLE_LOCATION)
                location?.let { loc -> updateLiveLocation(loc) }
            }
        }
    }
}
