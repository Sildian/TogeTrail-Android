package com.sildian.apps.togetrail.hiker.profile

import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.common.baseControllers.BaseActivity
import com.sildian.apps.togetrail.common.baseControllers.BaseFragment
import com.sildian.apps.togetrail.common.utils.cloudHelpers.AuthFirebaseHelper
import com.sildian.apps.togetrail.event.detail.EventActivity
import com.sildian.apps.togetrail.hiker.profileEdit.ProfileEditActivity
import com.sildian.apps.togetrail.trail.map.TrailActivity

/*************************************************************************************************
 * Allows to see a hiker's profile or related information
 ************************************************************************************************/

class ProfileActivity : BaseActivity() {

    /**********************************Static items**********************************************/

    companion object {

        /**Bundle keys for intents**/
        const val KEY_BUNDLE_HIKER_ID="KEY_BUNDLE_HIKER_ID"         //Hiker id -> Mandatory
    }

    /****************************************Data************************************************/

    private var hikerId: String?=null

    /**********************************UI component**********************************************/

    private var fragment: BaseFragment?=null

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
        finishCancel()
    }

    override fun onSupportNavigateUp(): Boolean {
        finishCancel()
        return true
    }

    /******************************Data monitoring************************************************/

    override fun loadData() {
        readDataFromIntent()
    }

    private fun readDataFromIntent(){
        if(intent!=null){
            if(intent.hasExtra(KEY_BUNDLE_HIKER_ID)){
                this.hikerId = intent.getStringExtra(KEY_BUNDLE_HIKER_ID)
            }
        }
    }

    /******************************UI monitoring**************************************************/

    override fun getLayoutId(): Int = R.layout.activity_profile

    override fun initializeUI() {
        showFragment()
    }

    /******************************Trails monitoring*********************************************/

    fun seeTrail(trailId:String){
        startTrailActivity(trailId)
    }

    /******************************Events monitoring*********************************************/

    fun seeEvent(eventId:String){
        startEventActivity(eventId)
    }

    /******************************Fragments monitoring******************************************/

    private fun showFragment(){
        this.fragment=ProfileFragment(this.hikerId)
        this.fragment?.let { fragment ->
            supportFragmentManager.beginTransaction()
                .replace(R.id.activity_profile_fragment, fragment).commit()
        }
    }

    /***********************************Navigation***********************************************/

    private fun startProfileEditActivity(){
        val profileEditActivityIntent=Intent(this, ProfileEditActivity::class.java)
        profileEditActivityIntent.putExtra(ProfileEditActivity.KEY_BUNDLE_PROFILE_ACTION, ProfileEditActivity.ACTION_PROFILE_EDIT_INFO)
        profileEditActivityIntent.putExtra(ProfileEditActivity.KEY_BUNDLE_HIKER_ID, this.hikerId)
        startActivity(profileEditActivityIntent)
    }

    private fun startTrailActivity(trailId:String){
        val trailActivityIntent=Intent(this, TrailActivity::class.java)
        trailActivityIntent.putExtra(TrailActivity.KEY_BUNDLE_TRAIL_ACTION, TrailActivity.ACTION_TRAIL_SEE)
        trailActivityIntent.putExtra(TrailActivity.KEY_BUNDLE_TRAIL_ID, trailId)
        startActivity(trailActivityIntent)
    }

    private fun startEventActivity(eventId:String){
        val eventActivityIntent=Intent(this, EventActivity::class.java)
        eventActivityIntent.putExtra(EventActivity.KEY_BUNDLE_EVENT_ID, eventId)
        startActivity(eventActivityIntent)
    }
}
