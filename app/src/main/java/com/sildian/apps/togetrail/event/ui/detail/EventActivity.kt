package com.sildian.apps.togetrail.event.ui.detail

import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import androidx.databinding.ViewDataBinding
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.common.baseControllers.BaseActivity
import com.sildian.apps.togetrail.common.baseControllers.BaseFragment
import com.sildian.apps.togetrail.databinding.ActivityEventBinding
import com.sildian.apps.togetrail.event.ui.edit.EventEditActivity
import com.sildian.apps.togetrail.hiker.ui.profile.ProfileActivity
import com.sildian.apps.togetrail.trail.ui.map.TrailActivity
import dagger.hilt.android.AndroidEntryPoint

/*************************************************************************************************
 * Displays an event's detail info and allows a user to register on this event
 ************************************************************************************************/

@AndroidEntryPoint
class EventActivity : BaseActivity<ActivityEventBinding>() {

    /**********************************Static items**********************************************/

    companion object {

        /**Bundle keys for intents**/
        const val KEY_BUNDLE_EVENT_ID="KEY_BUNDLE_EVENT_ID"     //Event's id -> Mandatory
    }

    /****************************************Menu************************************************/

    private var menu:Menu?=null                                 //The activity menu

    /****************************************Data************************************************/

    private var eventId:String?=null                            //The event's id

    /**********************************UI component**********************************************/

    private var fragment: BaseFragment<out ViewDataBinding>?=null

    /********************************Menu monitoring*********************************************/

    /**Generates the menu within the toolbar**/

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        this.menu=menu
        return true
    }

    /**Click on menu item from toolbar**/

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.groupId==R.id.menu_edit){
            if(item.itemId==R.id.menu_edit_edit){
                startEventEditActivity()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /**Defines the menu items**/

    fun allowEditMenu() {
        if (this.menu != null) {
            this.menu?.clear()
            menuInflater.inflate(R.menu.menu_edit, this.menu)
        }
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
            if(intent.hasExtra(KEY_BUNDLE_EVENT_ID)){
                this.eventId= intent.getStringExtra(KEY_BUNDLE_EVENT_ID)
            }
        }
    }

    /******************************UI monitoring**************************************************/

    override fun getLayoutId(): Int = R.layout.activity_event

    override fun initializeUI() {
        showFragment()
    }

    /******************************Fragments monitoring******************************************/

    private fun showFragment(){
        this.fragment= EventFragment(this.eventId)
        this.fragment?.let { fragment ->
            supportFragmentManager.beginTransaction()
                .replace(R.id.activity_event_fragment, fragment).commit()
        }
    }

    /*********************************Hikers monitoring******************************************/

    fun seeHiker(hikerId: String){
        startProfileActivity(hikerId)
    }

    /*********************************Trails monitoring******************************************/

    fun seeTrail(trailId:String){
        startTrailActivity(trailId)
    }

    /***********************************Navigation***********************************************/

    private fun startEventEditActivity(){
        val eventEditActivityIntent= Intent(this, EventEditActivity::class.java)
        eventEditActivityIntent.putExtra(EventEditActivity.KEY_BUNDLE_EVENT_ID,this.eventId)
        startActivity(eventEditActivityIntent)
    }

    private fun startProfileActivity(hikerId:String){
        val profileActivityIntent=Intent(this, ProfileActivity::class.java)
        profileActivityIntent.putExtra(ProfileActivity.KEY_BUNDLE_HIKER_ID, hikerId)
        startActivity(profileActivityIntent)
    }

    private fun startTrailActivity(trailId:String){
        val trailActivityIntent=Intent(this, TrailActivity::class.java)
        trailActivityIntent.putExtra(TrailActivity.KEY_BUNDLE_TRAIL_ACTION, TrailActivity.ACTION_TRAIL_SEE)
        trailActivityIntent.putExtra(TrailActivity.KEY_BUNDLE_TRAIL_ID, trailId)
        startActivity(trailActivityIntent)
    }
}
