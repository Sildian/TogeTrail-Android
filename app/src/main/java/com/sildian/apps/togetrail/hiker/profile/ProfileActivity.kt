package com.sildian.apps.togetrail.hiker.profile

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.common.utils.cloudHelpers.UserFirebaseHelper
import com.sildian.apps.togetrail.hiker.model.core.Hiker
import com.sildian.apps.togetrail.hiker.profileEdit.ProfileEditActivity
import com.sildian.apps.togetrail.main.MainActivity
import kotlinx.android.synthetic.main.activity_profile.*

/*************************************************************************************************
 * Allows to see a hiker's profile or related information
 ************************************************************************************************/

class ProfileActivity : AppCompatActivity() {

    /**********************************Static items**********************************************/

    companion object {

        /**Logs**/
        private const val TAG_ACTIVITY = "TAG_ACTIVITY"
        private const val TAG_STORAGE="TAG_STORAGE"

        /**Fragments ids**/
        private const val ID_FRAGMENT_PROFILE=1
        private const val ID_FRAGMENT_TRAILS=2
        private const val ID_FRAGMENT_EVENTS=3

        /**Actions to perform**/
        const val ACTION_PROFILE_SEE_PROFILE=1
        const val ACTION_PROFILE_SEE_TRAILS=2
        const val ACTION_PROFILE_SEE_EVENTS=3

        /**Request keys for activities**/
        private const val KEY_REQUEST_PROFILE_EDIT=1001

        /**Bundle keys for intents**/
        const val KEY_BUNDLE_PROFILE_ACTION="KEY_BUNDLE_PROFILE_ACTION"
        const val KEY_BUNDLE_HIKER="KEY_BUNDLE_HIKER"
    }

    /****************************************Data************************************************/

    private var currentAction= ACTION_PROFILE_SEE_PROFILE       //Action defining what the user is performing
    private var hiker: Hiker?=null                              //The hiker

    /**********************************UI component**********************************************/

    private val toolbar by lazy {activity_profile_toolbar}
    private lateinit var fragment: Fragment

    /************************************Life cycle**********************************************/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG_ACTIVITY, "Activity '${javaClass.simpleName}' created")
        setContentView(R.layout.activity_profile)
        readDataFromIntent(intent)
        initializeToolbar()
        startProfileAction()
    }

    /********************************Navigation control******************************************/

    override fun onBackPressed() {
        finishOk()
    }

    override fun onSupportNavigateUp(): Boolean {
        finishOk()
        return true
    }

    /******************************Data monitoring************************************************/

    private fun readDataFromIntent(intent: Intent?){
        if(intent!=null){
            if(intent.hasExtra(MainActivity.KEY_BUNDLE_PROFILE_ACTION)){
                this.currentAction=intent.getIntExtra(
                    MainActivity.KEY_BUNDLE_PROFILE_ACTION,
                    ACTION_PROFILE_SEE_EVENTS
                )
            }
            if(intent.hasExtra(MainActivity.KEY_BUNDLE_HIKER)){
                this.hiker= intent.getParcelableExtra(MainActivity.KEY_BUNDLE_HIKER)
            }
        }
    }

    /******************************UI monitoring**************************************************/

    private fun initializeToolbar(){
        setSupportActionBar(this.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        when(this.currentAction){
            ACTION_PROFILE_SEE_PROFILE -> {
                if(this.hiker?.id==UserFirebaseHelper.getCurrentUser()?.uid){
                    supportActionBar?.setTitle(R.string.toolbar_hiker_my_profile)
                }else{
                    supportActionBar?.setTitle(R.string.toolbar_hiker_profile)
                }
            }
            ACTION_PROFILE_SEE_TRAILS ->
                supportActionBar?.setTitle(R.string.toolbar_hiker_my_trails)
            ACTION_PROFILE_SEE_EVENTS ->
                supportActionBar?.setTitle(R.string.toolbar_hiker_my_events)
        }
    }

    /*************************************Profile action*****************************************/

    private fun startProfileAction(){
        when(this.currentAction){
            ACTION_PROFILE_SEE_PROFILE -> showFragment(ID_FRAGMENT_PROFILE)
            //TODO handle other fragments
        }
    }

    fun editProfile(){
        startProfileEditActivity(ProfileEditActivity.ACTION_PROFILE_EDIT_INFO)
    }

    /******************************Fragments monitoring******************************************/

    /**
     * Shows a fragment
     * @param fragmentId : defines which fragment to display (choice within ID_FRAGMENT_xxx)
     */

    private fun showFragment(fragmentId:Int){
        when(fragmentId){
            ID_FRAGMENT_PROFILE -> this.fragment=ProfileFragment(this.hiker)
            //TODO handle other fragments
        }
        supportFragmentManager.beginTransaction()
            .replace(R.id.activity_profile_fragment, this.fragment).commit()
    }

    /***********************************Navigation***********************************************/

    /**
     * Starts profile Edit activity
     * @param profileActionId : defines which action should be performed (choice within ProfileEditActivity.ACTION_PROFILE_xxx
     */

    private fun startProfileEditActivity(profileActionId:Int){
        val profileEditActivityIntent=Intent(this, ProfileEditActivity::class.java)
        profileEditActivityIntent.putExtra(KEY_BUNDLE_PROFILE_ACTION, profileActionId)
        profileEditActivityIntent.putExtra(KEY_BUNDLE_HIKER, this.hiker)
        startActivityForResult(profileEditActivityIntent, KEY_REQUEST_PROFILE_EDIT)
    }

    /**Activity result**/

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            KEY_REQUEST_PROFILE_EDIT -> handleProfileEditResult(resultCode, data)
        }
    }

    /**Handles profile edit result**/

    private fun handleProfileEditResult(resultCode: Int, data: Intent?){
        if(resultCode== Activity.RESULT_OK){
            if(data!=null && data.hasExtra(KEY_BUNDLE_HIKER)){
                this.hiker=data.getParcelableExtra(KEY_BUNDLE_HIKER)
                (this.fragment as ProfileFragment).updateHiker(this.hiker!!)
            }
        }
    }

    /**Finishes the activity and sends the hiker as a result**/

    private fun finishOk(){
        val resultIntent=Intent()
        resultIntent.putExtra(MainActivity.KEY_BUNDLE_HIKER, this.hiker)
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }
}