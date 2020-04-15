package com.sildian.apps.togetrail.trail.infoEdit

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
import com.sildian.apps.togetrail.common.baseControllers.BaseImagePickerFragment
import com.sildian.apps.togetrail.common.utils.cloudHelpers.StorageFirebaseHelper
import com.sildian.apps.togetrail.common.utils.cloudHelpers.AuthFirebaseHelper
import com.sildian.apps.togetrail.common.utils.uiHelpers.DialogHelper
import com.sildian.apps.togetrail.hiker.model.core.Hiker
import com.sildian.apps.togetrail.hiker.model.core.HikerHistoryItem
import com.sildian.apps.togetrail.hiker.model.core.HikerHistoryType
import com.sildian.apps.togetrail.location.model.core.Location
import com.sildian.apps.togetrail.location.search.LocationSearchActivity
import com.sildian.apps.togetrail.trail.model.core.Trail
import com.sildian.apps.togetrail.trail.model.core.TrailPointOfInterest
import kotlinx.android.synthetic.main.activity_trail_info_edit.*
import java.util.*

/*************************************************************************************************
 * This activity allows the user to edit information about a trail or a trailPointOfInterest
 ************************************************************************************************/

class TrailInfoEditActivity : BaseDataFlowActivity() {

    /**********************************Static items**********************************************/

    companion object{

        /**Logs**/
        private const val TAG="TrailInfoEditActivity"

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
    private var trail: Trail?=null                                  //Current trail to be edited
    private var trailPointOfInterest: TrailPointOfInterest?=null    //Current trailPointOfInterest to be edited
    private var trailPointOfInterestPosition:Int?=null              //The trailPoi's position within the trailTrack

    /**********************************UI component**********************************************/

    private val toolbar by lazy {activity_trail_info_edit_toolbar}
    private var fragment: BaseImagePickerFragment?=null
    private var progressDialog:AlertDialog?=null

    /**********************************Pictures support******************************************/

    private var imagePathToUploadIntoDatabase:String?=null      //Path of image to upload into the database
    private var imagePathToDeleteFromDatabase:String?=null      //Path of image to delete from the database

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

    /**Loads data**/

    override fun loadData() {
        readDataFromIntent()
    }

    /**Updates data**/

    override fun updateData(data: Any?) {
        if(data is TrailPointOfInterest) {
            if (this.trail != null) {
                this.trailPointOfInterestPosition?.let { index ->
                    this.trailPointOfInterest = data
                    this.trail!!.trailTrack.trailPointsOfInterest[index] = data
                }
            }
        }
    }

    /**Saves data**/

    override fun saveData() {
        this.fragment?.saveData()
    }

    /**Read data from intent**/

    private fun readDataFromIntent(){
        if(intent!=null){
            if(intent.hasExtra(KEY_BUNDLE_TRAIL_ACTION)){
                this.currentAction=intent.getIntExtra(
                    KEY_BUNDLE_TRAIL_ACTION, ACTION_TRAIL_EDIT_INFO)
            }
            if(intent.hasExtra(KEY_BUNDLE_TRAIL)){
                this.trail=intent.getParcelableExtra(KEY_BUNDLE_TRAIL)
            }
            if(intent.hasExtra(KEY_BUNDLE_TRAIL_POI_POSITION)){
                val position=intent.getIntExtra(KEY_BUNDLE_TRAIL_POI_POSITION, 0)
                this.trailPointOfInterestPosition=position
                this.trail?.let { trail ->
                    this.trailPointOfInterest = trail.trailTrack.trailPointsOfInterest[position]
                }
            }
        }
    }

    /**Saves the trail**/

    fun saveTrail(){

        /*Shows a progress dialog*/

        this.progressDialog=DialogHelper.createProgressDialog(this)
        this.progressDialog?.show()

        /*Manages images to store within the cloud before saving the trail*/

        if(this.imagePathToUploadIntoDatabase!=null) {
            saveImage()
        }
        else{
            saveTrailInDatabase()
        }
        if(this.imagePathToDeleteFromDatabase!=null) {
            deleteImage()
        }
    }

    /**Saves a trail point of interest**/

    fun saveTrailPoi(trailPointOfInterest: TrailPointOfInterest){

        /*Updates the poi*/

        updateData(trailPointOfInterest)

        /*Shows a progress dialog*/

        this.progressDialog=DialogHelper.createProgressDialog(this)
        this.progressDialog?.show()

        /*Manages images to store within the cloud before saving the trail*/

        if(this.imagePathToUploadIntoDatabase!=null) {
            saveImage()
        }
        else{
            saveTrailInDatabase()
        }
        if(this.imagePathToDeleteFromDatabase!=null) {
            deleteImage()
        }
    }

    private fun saveImage(){

        /*Uploads the image matching the path indicated within the image path to upload*/

        StorageFirebaseHelper.uploadImage(this.imagePathToUploadIntoDatabase.toString())
            .addOnSuccessListener { uploadTask ->

                /*When success, fetches the url of the created image in the database*/

                uploadTask?.storage?.downloadUrl
                    ?.addOnSuccessListener { url ->
                        Log.d(TAG, "Image successfully uploaded with url '${url}'")

                        /*Then updates the trail or the trailPOI with this url*/

                        when(this.currentAction){
                            ACTION_TRAIL_EDIT_INFO -> {
                                this.trail?.mainPhotoUrl=url.toString()
                            }
                            ACTION_TRAIL_EDIT_POI_INFO -> {
                                if(this.trailPointOfInterest!=null) {
                                    this.trailPointOfInterest?.photoUrl = url.toString()
                                    updateData(this.trailPointOfInterest)
                                }
                            }
                        }

                        /*And saves the trail*/

                        saveTrailInDatabase()
                    }
                    ?.addOnFailureListener { e ->
                        Log.w(TAG, e.message.toString())
                        saveTrailInDatabase()
                    }
            }
            .addOnFailureListener { e ->
                Log.w(TAG, e.message.toString())
                saveTrailInDatabase()
            }
    }

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

    /**Saves the trail in the database**/

    private fun saveTrailInDatabase(){

        /*Updates the trail*/

        this.trail?.lastUpdate= Date()

        if(this.trail!=null) {

            /*Shows a progress dialog*/

            this.progressDialog = DialogHelper.createProgressDialog(this)
            this.progressDialog?.show()

            /*If the trail has no id, it means it was not created in the database yet. Then creates it.*/

            if (this.trail?.id == null) {
                this.trail?.authorId = AuthFirebaseHelper.getCurrentUser()?.uid
                createTrailInDatabase()

                /*Else updates it*/

            } else {
                updateTrailInDatabase()
            }
        }
    }

    /**Creates a trail in the database**/

    private fun createTrailInDatabase(){
        this.trail?.let { trail ->
            addTrail(trail, this::handleCreatedTrail)
        }
    }

    /**
     * Handles the created trail in the database
     * @param trailId : the created trail's id
     */

    private fun handleCreatedTrail(trailId: String){
        this.trail?.id=trailId
        updateTrailInDatabase()
        updateCurrentUserInDatabase()
    }

    /**Updates the trail in the database**/

    private fun updateTrailInDatabase(){
        this.trail?.let { trail ->
            updateTrail(trail, this::handleUpdatedTrail)
        }
    }

    /**Handles the updated trail in the database**/

    private fun handleUpdatedTrail(){
        this.progressDialog?.dismiss()
        finishOk()
    }

    /**Starts update the current user in the database**/

    private fun updateCurrentUserInDatabase(){

        /*First, gets current user with updates info*/

        val user=AuthFirebaseHelper.getCurrentUser()
        user?.uid?.let { hikerId ->
            getHiker(hikerId, this::handleHikerToUpdate)
        }
    }

    /**
     * Handles the hiker to update when loaded from the database
     * @param hiker : the hiker to update
     */

    private fun handleHikerToUpdate(hiker:Hiker?){
        hiker?.let { hikerToUpdate ->
            hiker.nbTrailsCreated++
            updateHiker(hikerToUpdate)
            val historyItem = HikerHistoryItem(
                HikerHistoryType.TRAIL_CREATED,
                this.trail?.creationDate!!,
                this.trail?.id!!,
                this.trail?.name!!,
                this.trail?.location?.toString(),
                this.trail?.getFirstPhotoUrl()
            )
            addHikerHistoryItem(hikerToUpdate.id, historyItem)
            this.progressDialog?.dismiss()
            finishOk()
        }
    }

    /**
     * Gives an image to be stored on the cloud
     * @param imagePath : the temporary image's uri
     */

    fun updateImagePathToUploadIntoDatabase(imagePath:String){
        if(this.trailPointOfInterest?.photoUrl!=null && this.trailPointOfInterest?.photoUrl!!.startsWith("https://")){
            this.imagePathToDeleteFromDatabase=this.trailPointOfInterest?.photoUrl
        }
        this.imagePathToUploadIntoDatabase=imagePath
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

    /*************************************UI monitoring******************************************/

    override fun getLayoutId(): Int = R.layout.activity_trail_info_edit

    override fun initializeUI() {
        initializeToolbar()
    }

    override fun refreshUI() {
        //Nothing
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
        this.trail?.location=location
        this.fragment?.updateData(location)
    }

    /******************************Fragments monitoring******************************************/

    /**
     * Shows a fragment
     * @param fragmentId : defines which fragment to display (choice within ID_FRAGMENT_xxx)
     */

    private fun showFragment(fragmentId:Int){
        when(fragmentId){
            ID_FRAGMENT_TRAIL_INFO_EDIT ->
                this.fragment=
                    TrailInfoEditFragment(
                        this.trail
                    )
            ID_FRAGMENT_TRAIL_POI_INFO_EDIT ->
                this.fragment =
                    TrailPOIInfoEditFragment(
                        this.trailPointOfInterest
                    )
        }
        this.fragment?.let { fragment ->
            supportFragmentManager.beginTransaction()
                .replace(R.id.activity_trail_info_edit_fragment, fragment).commit()
        }
    }

    /*************************************Navigation*********************************************/

    /**Starts Location search activity**/

    private fun startLocationSearchActivity(){
        val locationSearchActivityIntent=Intent(this, LocationSearchActivity::class.java)
        startActivityForResult(locationSearchActivityIntent, KEY_REQUEST_LOCATION_SEARCH)
    }

    /**Gets Activity result**/

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode== KEY_REQUEST_LOCATION_SEARCH){
            if(data!=null&&data.hasExtra(LocationSearchActivity.KEY_BUNDLE_LOCATION)){
                val location=data.getParcelableExtra<Location>(LocationSearchActivity.KEY_BUNDLE_LOCATION)
                location?.let { loc -> updateLocation(loc) }
            }
        }
    }

    /**Finishes with Ok status**/

    private fun finishOk(){
        val resultIntent=Intent()
        resultIntent.putExtra(KEY_BUNDLE_TRAIL, this.trail)
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }

    /**Finishes with Cancel status**/

    private fun finishCancel(){
        setResult(Activity.RESULT_CANCELED)
        finish()
    }
}
