package com.sildian.apps.togetrail.trail.infoEdit

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.common.flows.BaseDataFlowActivity
import com.sildian.apps.togetrail.common.flows.BaseDataFlowFragment
import com.sildian.apps.togetrail.common.utils.cloudHelpers.ImageStorageFirebaseHelper
import com.sildian.apps.togetrail.common.utils.cloudHelpers.UserFirebaseHelper
import com.sildian.apps.togetrail.common.utils.uiHelpers.DialogHelper
import com.sildian.apps.togetrail.trail.model.core.Trail
import com.sildian.apps.togetrail.trail.model.core.TrailPointOfInterest
import com.sildian.apps.togetrail.trail.model.support.TrailFirebaseQueries
import kotlinx.android.synthetic.main.activity_trail_info_edit.*

/*************************************************************************************************
 * This activity allows the user to edit information about a trail or a trailPointOfInterest
 ************************************************************************************************/

class TrailInfoEditActivity : BaseDataFlowActivity() {

    /**********************************Static items**********************************************/

    companion object{

        /**Logs**/
        private const val TAG_ACTIVITY="TAG_ACTIVITY"
        private const val TAG_MENU="TAG_MENU"
        private const val TAG_STORAGE="TAG_STORAGE"

        /**Fragments Ids***/
        private const val ID_FRAGMENT_TRAIL_INFO_EDIT=1
        private const val ID_FRAGMENT_TRAIL_POI_INFO_EDIT=2

        /**Trail actions defining what the user is performing**/
        const val ACTION_TRAIL_EDIT_INFO=1
        const val ACTION_TRAIL_EDIT_POI_INFO=2

        /**Bundle keys for intent**/
        const val KEY_BUNDLE_TRAIL_ACTION="KEY_BUNDLE_TRAIL_ACTION"
        const val KEY_BUNDLE_TRAIL="KEY_BUNDLE_TRAIL"
        const val KEY_BUNDLE_TRAIL_POI_POSITION="KEY_BUNDLE_TRAIL_POI_POSITION"
    }

    /**************************************Data**************************************************/

    private var currentAction= ACTION_TRAIL_EDIT_INFO               //Action defining what the user is performing
    private var trail: Trail?=null                                  //Current trail to be edited
    private var trailPointOfInterest: TrailPointOfInterest?=null    //Current trailPointOfInterest to be edited
    private var trailPointOfInterestPosition:Int?=null              //The trailPoi's position within the trailTrack

    /**********************************UI component**********************************************/

    private val toolbar by lazy {activity_trail_info_edit_toolbar}
    private lateinit var fragment: BaseDataFlowFragment
    private lateinit var progressDialog:AlertDialog

    /**********************************Pictures support******************************************/

    private var imagePathToUploadIntoDatabase:String?=null      //Path of image to upload into the database
    private var imagePathToDeleteFromDatabase:String?=null      //Path of image to delete from the database

    /************************************Life cycle**********************************************/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG_ACTIVITY, "Activity '${javaClass.simpleName}' created")
        setContentView(R.layout.activity_trail_info_edit)
        loadData()
        initializeToolbar()
        startTrailEditAction()
    }

    /********************************Navigation control******************************************/

    override fun onBackPressed() {
        //TODO ask the user if he wants to save
        finishCancel()
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
        Log.d(TAG_MENU, "Menu '${item.title}' clicked")
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
                this.currentAction=intent.getIntExtra(
                    KEY_BUNDLE_TRAIL_ACTION, ACTION_TRAIL_EDIT_INFO)
            }
            if(intent.hasExtra(KEY_BUNDLE_TRAIL)){
                this.trail=intent.getParcelableExtra(KEY_BUNDLE_TRAIL)
            }
            if(intent.hasExtra(KEY_BUNDLE_TRAIL_POI_POSITION)){
                val position=intent.getIntExtra(KEY_BUNDLE_TRAIL_POI_POSITION, 0)
                this.trailPointOfInterestPosition=position
                this.trailPointOfInterest=this.trail!!.trailTrack.trailPointsOfInterest[position]
            }
        }
    }

    private fun updateTrailPointOfInterest(trailPointOfInterest: TrailPointOfInterest){
        this.trailPointOfInterest=trailPointOfInterest
        this.trail!!.trailTrack.trailPointsOfInterest[this.trailPointOfInterestPosition!!]=trailPointOfInterest
    }

    fun saveTrail(){
        this.progressDialog=DialogHelper.createProgressDialog(this)
        this.progressDialog.show()
        saveTrailOnline()
    }

    fun saveTrailPoi(trailPointOfInterest: TrailPointOfInterest){
        updateTrailPointOfInterest(trailPointOfInterest)
        this.progressDialog=DialogHelper.createProgressDialog(this)
        this.progressDialog.show()
        if(this.imagePathToUploadIntoDatabase!=null) {
            saveImage()
        }
        else{
            saveTrailOnline()
        }
        if(this.imagePathToDeleteFromDatabase!=null) {
            deleteImage()
        }
    }

    private fun saveImage(){

        /*Uploads the image matching the path indicated within the image path to upload*/

        ImageStorageFirebaseHelper.uploadImage(this.imagePathToUploadIntoDatabase.toString())
            .addOnSuccessListener { uploadTask ->
                Log.d(TAG_STORAGE, "Uploaded image to database with success")

                /*When success, fetches the url of the created image in the database*/

                uploadTask?.storage?.downloadUrl
                    ?.addOnSuccessListener { url ->
                        Log.d(TAG_STORAGE, "Image uploaded with url '${url}'")

                        /*Then updates the trailPOI with this url*/

                        this.trailPointOfInterest?.photoUrl=url.toString()
                        updateTrailPointOfInterest(this.trailPointOfInterest!!)
                        saveTrailOnline()
                    }
                    ?.addOnFailureListener { e ->
                        //TODO handle
                        Log.w(TAG_STORAGE, e.message.toString())
                        saveTrailOnline()
                    }
            }
            .addOnFailureListener { e ->
                //TODO handle
                Log.w(TAG_STORAGE, e.message.toString())
                saveTrailOnline()
            }
    }

    private fun deleteImage(){

        /*Deletes the image matching the url indicated within the image path to delete*/

        ImageStorageFirebaseHelper.deleteImage(this.imagePathToDeleteFromDatabase.toString())
            .addOnSuccessListener {
                Log.d(TAG_STORAGE, "Deleted image from database with success")
            }
            .addOnFailureListener { e ->
                //TODO handle
                Log.w(TAG_STORAGE, e.message.toString())
            }
    }

    private fun saveTrailOnline(){

        /*If the trail has no id, it means it was not created in the database yet. Then creates it.*/

        if(this.trail?.id==null){

            this.trail?.authorId=UserFirebaseHelper.getCurrentUser()?.uid

            TrailFirebaseQueries.createTrail(this.trail!!)
                .addOnSuccessListener { documentReference->

                    /*Once created, updates it with the created id*/

                    Log.d(TAG_STORAGE, "Trail created in the database")
                    this.trail?.id=documentReference.id
                    TrailFirebaseQueries.updateTrail(this.trail!!)
                        .addOnSuccessListener {
                            Log.d(TAG_STORAGE, "Trail updated in the database")
                            progressDialog.dismiss()
                            //TODO show a snackbar when finished
                            finishOk()
                        }
                        .addOnFailureListener { e ->
                            Log.w(TAG_STORAGE, e.message.toString())
                            progressDialog.dismiss()
                            //TODO handle
                            finishOk()
                        }
                }
                .addOnFailureListener { e ->
                    Log.w(TAG_STORAGE, e.message.toString())
                    this.progressDialog.dismiss()
                    //TODO handle
                    finishCancel()
                }

            /*Else updates it*/

        }else{
            TrailFirebaseQueries.updateTrail(this.trail!!)
                .addOnSuccessListener {
                    Log.d(TAG_STORAGE, "Trail updated in the database")
                    this.progressDialog.dismiss()
                    //TODO show a snackbar when finished
                    finishOk()
                }
                .addOnFailureListener { e ->
                    Log.w(TAG_STORAGE, e.message.toString())
                    this.progressDialog.dismiss()
                    //TODO handle
                    finishCancel()
                }
        }
    }

    fun updateImagePathToUploadIntoDatabase(imagePath:String){
        if(this.trailPointOfInterest?.photoUrl!=null && this.trailPointOfInterest?.photoUrl!!.startsWith("https://")){
            this.imagePathToDeleteFromDatabase=this.trailPointOfInterest?.photoUrl
        }
        this.imagePathToUploadIntoDatabase=imagePath
    }

    fun updateImagePathToDeleteFromDatabase(imagePath:String){
        this.imagePathToUploadIntoDatabase=null
        if(imagePath.startsWith("https://")){
            this.imagePathToDeleteFromDatabase=imagePath
        }
    }

    /*************************************UI monitoring******************************************/

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
        supportFragmentManager.beginTransaction()
            .replace(R.id.activity_trail_info_edit_fragment, this.fragment).commit()
    }

    /*************************************Navigation*********************************************/

    private fun finishOk(){
        val resultIntent=Intent()
        resultIntent.putExtra(KEY_BUNDLE_TRAIL, this.trail)
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }

    private fun finishCancel(){
        setResult(Activity.RESULT_CANCELED)
        finish()
    }
}
