package com.sildian.apps.togetrail.hiker.profileEdit

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.common.flows.SaveDataFlow
import com.sildian.apps.togetrail.common.utils.cloudHelpers.ImageStorageFirebaseHelper
import com.sildian.apps.togetrail.common.utils.cloudHelpers.UserFirebaseHelper
import com.sildian.apps.togetrail.common.utils.uiHelpers.DialogHelper
import com.sildian.apps.togetrail.hiker.model.core.Hiker
import com.sildian.apps.togetrail.hiker.model.support.HikerFirebaseQueries
import com.sildian.apps.togetrail.main.MainActivity
import kotlinx.android.synthetic.main.activity_profile_edit.*

/*************************************************************************************************
 * Lets a user see and edit his own profile
 ************************************************************************************************/

class ProfileEditActivity : AppCompatActivity() {

    /**********************************Static items**********************************************/

    companion object {

        /**Logs**/
        private const val TAG_ACTIVITY = "TAG_ACTIVITY"
        private const val TAG_MENU="TAG_MENU"
        private const val TAG_USER="TAG_USER"
        private const val TAG_STORAGE="TAG_STORAGE"

        /**Fragments ids**/
        private const val ID_FRAGMENT_INFO=1
        private const val ID_FRAGMENT_SETTINGS=2

        /**Actions to perform**/
        const val ACTION_PROFILE_EDIT_INFO=1
        const val ACTION_PROFILE_EDIT_SETTINGS=2
    }

    /****************************************Data************************************************/

    private var currentAction= ACTION_PROFILE_EDIT_INFO         //Action defining what the user is performing
    private var hiker: Hiker?=null                              //The hiker

    /**********************************UI component**********************************************/

    private val toolbar by lazy {activity_profile_edit_toolbar}
    private lateinit var fragment: Fragment
    private lateinit var progressDialog: AlertDialog

    /********************************Attached flows**********************************************/

    private lateinit var saveDataFlow:SaveDataFlow      //Flow used when the user clicks on save menu

    /**********************************Pictures support******************************************/

    private var imagePathToUploadIntoDatabase:String?=null      //Path of image to upload into the database
    private var imagePathToDeleteFromDatabase:String?=null      //Path of image to delete from the database

    /************************************Life cycle**********************************************/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG_ACTIVITY, "Activity '${javaClass.simpleName}' created")
        setContentView(R.layout.activity_profile_edit)
        readDataFromIntent(intent)
        initializeToolbar()
        startProfileAction()
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
        menuInflater.inflate(R.menu.menu_edit, menu)
        return true
    }

    /**Click on menu item from toolbar**/

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.d(TAG_MENU, "Menu '${item.title}' clicked")
        if(item.groupId==R.id.menu_edit){
            if(item.itemId==R.id.menu_edit_save){
                this.saveDataFlow.saveData()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /******************************Data monitoring************************************************/

    private fun readDataFromIntent(intent: Intent?){
        if(intent!=null){
            if(intent.hasExtra(MainActivity.KEY_BUNDLE_PROFILE_ACTION)){
                this.currentAction=intent.getIntExtra(MainActivity.KEY_BUNDLE_PROFILE_ACTION, ACTION_PROFILE_EDIT_INFO)
            }
            if(intent.hasExtra(MainActivity.KEY_BUNDLE_HIKER)){
                this.hiker= intent.getParcelableExtra(MainActivity.KEY_BUNDLE_HIKER)
            }
        }
    }

    private fun updateHiker(hiker:Hiker){
        this.hiker=hiker
    }

    fun updateHikerAndSave(hiker: Hiker){
        updateHiker(hiker)
        this.progressDialog= DialogHelper.createProgressDialog(this)
        this.progressDialog.show()
        if(this.imagePathToUploadIntoDatabase!=null) {
            saveImage()
        }
        else{
            saveHiker()
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
                        Log.d(TAG_STORAGE, "Image uploaded with url '$url'")

                        /*Then updates the hiker with this url*/

                        this.hiker?.photoUrl=url.toString()
                        saveHiker()
                    }
                    ?.addOnFailureListener { e ->
                        //TODO handle
                        Log.w(TAG_STORAGE, e.message.toString())
                        saveHiker()
                    }
            }
            .addOnFailureListener { e ->
                //TODO handle
                Log.w(TAG_STORAGE, e.message.toString())
                saveHiker()
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

    private fun saveHiker(){
        HikerFirebaseQueries.createOrUpdateHiker(this.hiker!!)
            .addOnSuccessListener {
                Log.d(TAG_STORAGE, "Hiker updated in the database")
                this.progressDialog.dismiss()
                //TODO show a snackbar when finished
                updateUserProfile()
            }
            .addOnFailureListener { e ->
                Log.w(TAG_STORAGE, e.message.toString())
                this.progressDialog.dismiss()
                //TODO handle
                finishCancel()
            }
    }

    fun updateImagePathToUploadIntoDatabase(imagePath:String){
        if(this.hiker?.photoUrl!=null && this.hiker?.photoUrl!!.startsWith("https://")){
            this.imagePathToDeleteFromDatabase=this.hiker?.photoUrl
        }
        this.imagePathToUploadIntoDatabase=imagePath
    }

    fun updateImagePathToDeleteFromDatabase(imagePath:String){
        this.imagePathToUploadIntoDatabase=null
        if(imagePath.startsWith("https://")){
            this.imagePathToDeleteFromDatabase=imagePath
        }
    }

    /******************************User monitoring************************************************/

    /**Updates the user's profile in the backend**/

    fun updateUserProfile(){
        UserFirebaseHelper.updateUserProfile(this.hiker?.name!!, this.hiker?.photoUrl)
            ?.addOnSuccessListener {
                Log.d(TAG_USER, "User profile updated in the database")
                this.progressDialog.dismiss()
                //TODO show a snackbar when finished
                finishOk()
            }
            ?.addOnFailureListener { e ->
                Log.w(TAG_USER, e.message.toString())
                this.progressDialog.dismiss()
                //TODO handle
                finishCancel()
            }
    }

    /**Resets the user's password in the backend**/

    fun resetUserPassword(){
        this.progressDialog= DialogHelper.createProgressDialog(this)
        UserFirebaseHelper.resetUserPassword()
            ?.addOnSuccessListener {
                Log.d(TAG_USER, "Email sent to the user to let him reset his password")
                this.progressDialog.dismiss()
                //TODO show a snackbar when finished
            }
            ?.addOnFailureListener { e ->
                Log.w(TAG_USER, e.message.toString())
                this.progressDialog.dismiss()
                //TODO handle
            }
    }

    /**Definitely deletes the user's account from the backend**/

    fun deleteUserAccount(){

        this.progressDialog= DialogHelper.createProgressDialog(this)

        /*Deletes the hiker's data related to the user*/

        HikerFirebaseQueries.deleteHiker(this.hiker!!)
            .addOnSuccessListener {
                Log.d(TAG_USER, "Hiker deleted from the database")

                /*Delete the user's account*/

                UserFirebaseHelper.deleteUserAccount()
                    ?.addOnSuccessListener {
                        Log.d(TAG_USER, "User deleted from the database")
                        this.progressDialog.dismiss()

                        /*Deletes the user's photo*/

                        if(this.hiker?.photoUrl!=null) {
                            ImageStorageFirebaseHelper.deleteImage(this.hiker?.photoUrl.toString())
                        }
                        //TODO show a snackbar when finished
                    }
                    ?.addOnFailureListener { e ->
                        Log.w(TAG_USER, e.message.toString())
                        this.progressDialog.dismiss()
                        //TODO handle
                    }
            }
            .addOnFailureListener { e ->
                Log.w(TAG_USER, e.message.toString())
                this.progressDialog.dismiss()
                //TODO handle
            }
    }

    /******************************UI monitoring**************************************************/

    private fun initializeToolbar(){
        setSupportActionBar(this.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        when(this.currentAction){
            ACTION_PROFILE_EDIT_INFO ->
                supportActionBar?.setTitle(R.string.toolbar_hiker_profile)
            ACTION_PROFILE_EDIT_SETTINGS ->
                supportActionBar?.setTitle(R.string.toolbar_hikder_settings)
        }
    }

    /*********************************Starts profile action**************************************/

    private fun startProfileAction(){
        when(this.currentAction){
            ACTION_PROFILE_EDIT_INFO -> showFragment(ID_FRAGMENT_INFO)
            ACTION_PROFILE_EDIT_SETTINGS -> showFragment(ID_FRAGMENT_SETTINGS)
        }
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
        this.saveDataFlow=this.fragment as SaveDataFlow
        supportFragmentManager.beginTransaction()
            .replace(R.id.activity_profile_edit_fragment, this.fragment).commit()
    }

    /*************************************Navigation*********************************************/

    private fun finishOk(){
        val resultIntent=Intent()
        resultIntent.putExtra(MainActivity.KEY_BUNDLE_HIKER, this.hiker)
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }

    private fun finishCancel(){
        setResult(Activity.RESULT_CANCELED)
        finish()
    }
}
