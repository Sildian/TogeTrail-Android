package com.sildian.apps.togetrail.hiker.profileEdit

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.common.baseControllers.BaseDataFlowActivity
import com.sildian.apps.togetrail.common.baseControllers.BaseDataFlowFragment
import com.sildian.apps.togetrail.common.baseControllers.BaseImagePickerFragment
import com.sildian.apps.togetrail.location.search.LocationSearchActivity
import com.sildian.apps.togetrail.common.utils.cloudHelpers.StorageFirebaseHelper
import com.sildian.apps.togetrail.common.utils.cloudHelpers.AuthFirebaseHelper
import com.sildian.apps.togetrail.common.utils.uiHelpers.DialogHelper
import com.sildian.apps.togetrail.hiker.model.core.Hiker
import com.sildian.apps.togetrail.hiker.model.support.HikerFirebaseQueries
import com.sildian.apps.togetrail.location.model.core.Location
import kotlinx.android.synthetic.main.activity_profile_edit.*

/*************************************************************************************************
 * Lets a user see and edit his own profile
 ************************************************************************************************/

class ProfileEditActivity : BaseDataFlowActivity() {

    /**********************************Static items**********************************************/

    companion object {

        /**Logs**/
        private const val TAG="ProfileEditFragment"

        /**Fragments ids**/
        private const val ID_FRAGMENT_INFO=1
        private const val ID_FRAGMENT_SETTINGS=2

        /**Actions to perform**/
        const val ACTION_PROFILE_EDIT_INFO=1
        const val ACTION_PROFILE_EDIT_SETTINGS=2

        /**Bundle keys for intents**/
        const val KEY_BUNDLE_PROFILE_ACTION="KEY_BUNDLE_PROFILE_ACTION"     //Which action to perform (see above) -> Mandatory
        const val KEY_BUNDLE_HIKER="KEY_BUNDLE_HIKER"                       //The hiker -> Mandatory

        /**Request keys for intents**/
        private const val KEY_REQUEST_LOCATION_SEARCH=1001
    }

    /****************************************Data************************************************/

    private var currentAction= ACTION_PROFILE_EDIT_INFO         //Action defining what the user is performing
    private var hiker: Hiker?=null                              //The hiker

    /**********************************UI component**********************************************/

    private val toolbar by lazy {activity_profile_edit_toolbar}
    private var fragment: BaseDataFlowFragment?=null
    private var progressDialog: AlertDialog?=null

    /**********************************Pictures support******************************************/

    private var imagePathToUploadIntoDatabase:String?=null      //Path of image to upload into the database
    private var imagePathToDeleteFromDatabase:String?=null      //Path of image to delete from the database

    /************************************Life cycle**********************************************/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startProfileAction()
    }

    /********************************Navigation control******************************************/

    override fun onBackPressed() {
        if(this.currentAction== ACTION_PROFILE_EDIT_INFO
            &&(this.fragment as BaseImagePickerFragment).getAddPhotoBottomSheetState()!=BottomSheetBehavior.STATE_HIDDEN){
                (this.fragment as BaseImagePickerFragment).hideAddPhotoBottomSheet()
            }
        else {
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

    /**Loads data**/

    override fun loadData() {
        readDataFromIntent()
    }

    /**Saves data**/

    override fun saveData() {
        this.fragment?.saveData()
    }

    /**Reads data from intent**/

    private fun readDataFromIntent(){
        if(intent!=null){
            if(intent.hasExtra(KEY_BUNDLE_PROFILE_ACTION)){
                this.currentAction=intent.getIntExtra(KEY_BUNDLE_PROFILE_ACTION, ACTION_PROFILE_EDIT_INFO)
            }
            if(intent.hasExtra(KEY_BUNDLE_HIKER)){
                this.hiker= intent.getParcelableExtra(KEY_BUNDLE_HIKER)
            }
        }
    }

    /**Starts saving the hiker within the database**/

    fun saveHiker(){

        /*Shows a progress dialog*/

        this.progressDialog= DialogHelper.createProgressDialog(this)
        this.progressDialog?.show()

        /*Manages images to store within the cloud before saving the hiker*/

        if(this.imagePathToUploadIntoDatabase!=null) {
            saveImage()
        }
        else{
            saveHikerInDatabase()
        }
        if(this.imagePathToDeleteFromDatabase!=null) {
            deleteImage()
        }
    }

    /**Saves an image within the cloud**/

    private fun saveImage(){

        /*Uploads the image matching the path indicated within the image path to upload*/

        StorageFirebaseHelper.uploadImage(this.imagePathToUploadIntoDatabase.toString())
            .addOnSuccessListener { uploadTask ->

                /*When success, fetches the url of the created image in the database*/

                uploadTask?.storage?.downloadUrl
                    ?.addOnSuccessListener { url ->
                        Log.d(TAG, "Image successfully uploaded with url '$url'")

                        /*Then updates the hiker with this url*/

                        this.hiker?.photoUrl=url.toString()
                        saveHikerInDatabase()
                    }
                    ?.addOnFailureListener { e ->
                        Log.w(TAG, e.message.toString())
                        saveHikerInDatabase()
                    }
            }
            .addOnFailureListener { e ->
                Log.w(TAG, e.message.toString())
                saveHikerInDatabase()
            }
    }

    /**Deletes an image from the cloud**/

    private fun deleteImage(){

        /*Deletes the image matching the url indicated within the image path to delete*/

        StorageFirebaseHelper.deleteImage(this.imagePathToDeleteFromDatabase.toString())
            .addOnSuccessListener {
                Log.d(TAG, "Deleted image from database with success")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, e.message.toString())
            }
    }

    /**Saves the hiker within the database**/

    private fun saveHikerInDatabase(){
        this.hiker?.let { hiker ->
            updateHiker(hiker, this::updateUserProfile)
        }
    }

    /**
     * Gives an image to be stored on the cloud
     * @param imagePath : the temporary image's uri
     */

    fun updateImagePathToUploadIntoDatabase(imagePath:String){
        this.hiker?.photoUrl?.let { photoUrl ->
            if (photoUrl.startsWith("https://")) {
                this.imagePathToDeleteFromDatabase = photoUrl
            }
            this.imagePathToUploadIntoDatabase = imagePath
        }
    }

    /**
     * Gives an image to be deleted from the cloud
     * @param imagePath : the image's url
     */

    fun updateImagePathToDeleteFromDatabase(imagePath:String){
        this.imagePathToUploadIntoDatabase=null
        if(imagePath.startsWith("https://")){
            this.imagePathToDeleteFromDatabase=imagePath
        }
    }

    /******************************User Auth monitoring******************************************/

    /**Updates the user's profile in the backend**/

    fun updateUserProfile(){
        this.hiker?.let { hiker ->
            AuthFirebaseHelper.updateUserProfile(hiker.name?:"", hiker.photoUrl)
                ?.addOnSuccessListener {
                    Log.d(TAG, "User profile updated in the database")
                    this.progressDialog?.dismiss()
                    finish()
                }
                ?.addOnFailureListener { e ->
                    Log.w(TAG, e.message.toString())
                    this.progressDialog?.dismiss()
                    DialogHelper.createInfoDialog(
                        this,
                        R.string.message_query_failure_title,
                        R.string.message_query_failure_message
                    ).show()
                }
        }
    }

    /**Resets the user's password in the backend**/

    fun resetUserPassword(){
        this.progressDialog= DialogHelper.createProgressDialog(this)
        AuthFirebaseHelper.resetUserPassword()
            ?.addOnSuccessListener {
                Log.d(TAG, "Email sent to the user to let him reset his password")
                this.progressDialog?.dismiss()
            }
            ?.addOnFailureListener { e ->
                Log.w(TAG, e.message.toString())
                this.progressDialog?.dismiss()
                DialogHelper.createInfoDialog(
                    this,
                    R.string.message_query_failure_title,
                    R.string.message_query_failure_message
                ).show()
            }
    }

    /**Definitely deletes the user's account from the backend**/

    fun deleteUserAccount(){

        this.progressDialog= DialogHelper.createProgressDialog(this)

        /*Deletes the hiker's data related to the user*/

        this.hiker?.let { hiker ->
            HikerFirebaseQueries.deleteHiker(hiker)
                .addOnSuccessListener {
                    Log.d(TAG, "Hiker deleted from the database")

                    /*Delete the user's account*/

                    AuthFirebaseHelper.deleteUserAccount()
                        ?.addOnSuccessListener {
                            Log.d(TAG, "User deleted from the database")
                            this.progressDialog?.dismiss()

                            /*Deletes the user's photo*/

                            if (hiker.photoUrl != null) {
                                StorageFirebaseHelper.deleteImage(hiker.photoUrl.toString())
                            }
                        }
                        ?.addOnFailureListener { e ->
                            Log.w(TAG, e.message.toString())
                            this.progressDialog?.dismiss()
                            DialogHelper.createInfoDialog(
                                this,
                                R.string.message_query_failure_title,
                                R.string.message_query_failure_message
                            ).show()
                        }
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, e.message.toString())
                    this.progressDialog?.dismiss()
                    DialogHelper.createInfoDialog(
                        this,
                        R.string.message_query_failure_title,
                        R.string.message_query_failure_message
                    ).show()
                }
        }
    }

    /******************************UI monitoring**************************************************/

    override fun getLayoutId(): Int = R.layout.activity_profile_edit

    override fun initializeUI() {
        initializeToolbar()
    }

    private fun initializeToolbar(){
        setSupportActionBar(this.toolbar)
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
        this.hiker?.liveLocation=location
        this.fragment?.updateData(location)
    }

    /******************************Fragments monitoring******************************************/

    /**
     * Shows a fragment
     * @param fragmentId : defines which fragment to display (choice within ID_FRAGMENT_xxx)
     */

    private fun showFragment(fragmentId:Int){
        when(fragmentId){
            ID_FRAGMENT_INFO ->
                this.fragment= ProfileInfoEditFragment(this.hiker)
            ID_FRAGMENT_SETTINGS ->
                this.fragment=ProfileSettingsEditFragment(this.hiker)
        }
        this.fragment?.let { fragment ->
            supportFragmentManager.beginTransaction()
                .replace(R.id.activity_profile_edit_fragment, fragment).commit()
        }
    }

    /*************************************Navigation*********************************************/

    /**Starts searching a location**/

    private fun startLocationSearchActivity(){
        val locationSearchActivityIntent=Intent(this, LocationSearchActivity::class.java)
        startActivityForResult(locationSearchActivityIntent, KEY_REQUEST_LOCATION_SEARCH)
    }

    /**Gets the activity result**/

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode) {
            KEY_REQUEST_LOCATION_SEARCH -> handleLocationSearchActivityResult(resultCode, data)
        }
    }

    /**Handles location search activity result**/

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
