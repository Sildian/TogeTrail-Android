package com.sildian.apps.togetrail.hiker.profile

import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.common.baseControllers.BaseDataFlowActivity
import com.sildian.apps.togetrail.common.baseControllers.BaseDataFlowFragment
import com.sildian.apps.togetrail.common.utils.cloudHelpers.AuthFirebaseHelper
import com.sildian.apps.togetrail.event.detail.EventActivity
import com.sildian.apps.togetrail.hiker.model.core.Hiker
import com.sildian.apps.togetrail.hiker.profileEdit.ProfileEditActivity
import com.sildian.apps.togetrail.trail.map.TrailActivity
import kotlinx.android.synthetic.main.activity_profile.*

/*************************************************************************************************
 * Allows to see a hiker's profile or related information
 ************************************************************************************************/

class ProfileActivity : BaseDataFlowActivity() {

    /**********************************Static items**********************************************/

    companion object {

        /**Bundle keys for intents**/
        const val KEY_BUNDLE_HIKER_ID="KEY_BUNDLE_HIKER_ID"         //Hiker id -> Mandatory
    }

    /****************************************Data************************************************/

    private var hikerId: String?=null                               //The hiker id
    private var hiker: Hiker?=null                                  //The hiker

    /**********************************UI component**********************************************/

    private val progressbar by lazy {activity_profile_progressbar}
    private var fragment: BaseDataFlowFragment?=null

    /********************************Menu monitoring*********************************************/

    /**Generates the menu within the toolbar**/

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return if(this.hikerId==AuthFirebaseHelper.getCurrentUser()?.uid){
            menuInflater.inflate(R.menu.menu_edit, menu)
            true
        } else {
            true
        }
    }

    /**Click on menu item from toolbar**/

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.groupId==R.id.menu_edit){
            if(item.itemId==R.id.menu_edit_edit){
                startProfileEditActivity()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /********************************Navigation control******************************************/

    override fun onBackPressed() {
        finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    /******************************Data monitoring************************************************/

    /**Loads data**/

    override fun loadData() {
        readDataFromIntent()
    }

    /**Reads data from intent**/

    private fun readDataFromIntent(){
        if(intent!=null){
            if(intent.hasExtra(KEY_BUNDLE_HIKER_ID)){
                this.hikerId = intent.getStringExtra(KEY_BUNDLE_HIKER_ID)
                this.hikerId?.let{ hikerId ->
                    loadHikerFromDatabase(hikerId)
                }
            }
        }
    }

    /**
     * Loads a hiker from the database
     * @param hikerId : the hiker's id
     */

    private fun loadHikerFromDatabase(hikerId:String){
        getHikerRealTime(hikerId, this::handleHikerResult)
    }

    /**
     * Handles the hiker result when loaded from the database
     * @param hiker : the resulted hiker
     */

    private fun handleHikerResult(hiker:Hiker?){
        this.progressbar.visibility=View.GONE
        this.hiker=hiker
        if(this.fragment==null){
            showFragment()
        }else if(this.fragment?.isVisible==true){
            this.fragment?.updateData(this.hiker)
        }
    }

    /******************************UI monitoring**************************************************/

    override fun getLayoutId(): Int = R.layout.activity_profile

    /******************************Trails monitoring*********************************************/

    fun seeTrail(trailId:String){
        startTrailActivity(trailId)
    }

    /******************************Events monitoring*********************************************/

    fun seeEvent(eventId:String){
        startEventActivity(eventId)
    }

    /******************************Fragments monitoring******************************************/

    /**
     * Shows the fragment
     */

    private fun showFragment(){
        this.fragment=ProfileFragment(this.hiker)
        this.fragment?.let { fragment ->
            supportFragmentManager.beginTransaction()
                .replace(R.id.activity_profile_fragment, fragment).commit()
        }
    }

    /***********************************Navigation***********************************************/

    /**Starts Profile edit activity**/

    private fun startProfileEditActivity(){
        val profileEditActivityIntent=Intent(this, ProfileEditActivity::class.java)
        profileEditActivityIntent.putExtra(ProfileEditActivity.KEY_BUNDLE_PROFILE_ACTION, ProfileEditActivity.ACTION_PROFILE_EDIT_INFO)
        profileEditActivityIntent.putExtra(ProfileEditActivity.KEY_BUNDLE_HIKER, this.hiker)
        startActivity(profileEditActivityIntent)
    }

    /**Starts Trail activity**/

    private fun startTrailActivity(trailId:String){
        val trailActivityIntent=Intent(this, TrailActivity::class.java)
        trailActivityIntent.putExtra(TrailActivity.KEY_BUNDLE_TRAIL_ACTION, TrailActivity.ACTION_TRAIL_SEE)
        trailActivityIntent.putExtra(TrailActivity.KEY_BUNDLE_TRAIL_ID, trailId)
        startActivity(trailActivityIntent)
    }

    /**Starts Event activity**/

    private fun startEventActivity(eventId:String){
        val eventActivityIntent=Intent(this, EventActivity::class.java)
        eventActivityIntent.putExtra(EventActivity.KEY_BUNDLE_EVENT_ID, eventId)
        startActivity(eventActivityIntent)
    }
}
