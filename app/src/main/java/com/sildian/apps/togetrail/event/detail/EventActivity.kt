package com.sildian.apps.togetrail.event.detail

import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.common.baseControllers.BaseDataFlowActivity
import com.sildian.apps.togetrail.common.baseControllers.BaseDataFlowFragment
import com.sildian.apps.togetrail.common.utils.cloudHelpers.AuthFirebaseHelper
import com.sildian.apps.togetrail.event.edit.EventEditActivity
import com.sildian.apps.togetrail.event.model.core.Event
import com.sildian.apps.togetrail.hiker.model.core.Hiker
import com.sildian.apps.togetrail.hiker.model.core.HikerHistoryItem
import com.sildian.apps.togetrail.hiker.model.core.HikerHistoryType
import com.sildian.apps.togetrail.hiker.profile.ProfileActivity
import com.sildian.apps.togetrail.trail.map.TrailActivity
import kotlinx.android.synthetic.main.activity_event.*
import java.util.*

/*************************************************************************************************
 * Displays an event's detail info and allows a user to register on this event
 ************************************************************************************************/

class EventActivity : BaseDataFlowActivity() {

    /**********************************Static items**********************************************/

    companion object {

        /**Logs**/
        private const val TAG="EventActivity"

        /**Bundle keys for intents**/
        const val KEY_BUNDLE_EVENT_ID="KEY_BUNDLE_EVENT_ID"     //Event's id -> Mandatory
    }

    /****************************************Data************************************************/

    private var event: Event?=null                              //The event

    /**********************************UI component**********************************************/

    private val progressbar by lazy {activity_event_progressbar}
    private var fragment: BaseDataFlowFragment?=null
    private var progressDialog: AlertDialog?=null

    /************************************Life cycle**********************************************/

    override fun onDestroy() {
        this.eventQueryRegistration?.remove()
        super.onDestroy()
    }

    /********************************Menu monitoring*********************************************/

    /**Generates the menu within the toolbar**/

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_edit, menu)
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
            if(intent.hasExtra(KEY_BUNDLE_EVENT_ID)){
                val eventId= intent.getStringExtra(KEY_BUNDLE_EVENT_ID)
                eventId?.let { id -> loadEventFromDatabase(id) }
            }
        }
    }

    /**
     * Loads an event from the database
     * @param eventId : the event's id
     */

    private fun loadEventFromDatabase(eventId:String){
        getEventRealTime(eventId, this::handleEventResult)
    }

    /**
     * Handles the event result when loaded from the database
     * @param event : the event loaded from the database
     */

    private fun handleEventResult(event:Event?){
        this.progressbar.visibility= View.GONE
        this.event=event
        if(this.fragment==null){
            showFragment()
        }else if(this.fragment?.isVisible==true){
            this.fragment?.updateData(this.event)
        }
    }

    /**Starts register the current user to the event**/

    fun registerUserToEvent(){

        /*First, gets the current user with updated info*/

        val user=AuthFirebaseHelper.getCurrentUser()
        user?.uid?.let { hikerId ->
            getHiker(hikerId, this::handleHikerToRegister)
        }
    }

    /**
     * Handles the hiker to register to the event when loaded from the database
     * @param hiker : the hiker to register
     */

    private fun handleHikerToRegister(hiker:Hiker?){

        if(hiker!=null && this.event!=null) {

            /*Increases the number of hikers registered to the event*/

            hiker.nbEventsAttended++
            this.event!!.nbHikersRegistered++

            this.event?.let { event ->

                /*Updates both the hiker and the event*/

                updateHiker(hiker)
                updateEvent(event)
                updateHikerAttendedEvent(hiker.id, event)
                updateEventRegisteredHiker(event.id.toString(), hiker)

                /*Adds an history item to the hiker*/

                val historyItem = HikerHistoryItem(
                    HikerHistoryType.EVENT_ATTENDED,
                    Date(),
                    event.id,
                    event.name,
                    event.meetingPoint.toString(),
                    event.mainPhotoUrl
                )
                addHikerHistoryItem(hiker.id, historyItem)
            }
        }
    }

    /**Starts unregister the current user from the event**/

    fun unregisterUserFromEvent(){

        /*First, gets the current user with updated info*/

        val user=AuthFirebaseHelper.getCurrentUser()
        user?.uid?.let { hikerId ->
            getHiker(hikerId, this::handleHikerToUnregister)
        }
    }

    /**
     * Handles the hiker to unregister from the event when loaded from the database
     * @param hiker : the hiker to register
     */

    private fun handleHikerToUnregister(hiker:Hiker?){

        if(hiker!=null && this.event!=null) {

            /*Increases the number of hikers registered to the event*/

            hiker.nbEventsAttended--
            this.event!!.nbHikersRegistered--

            this.event?.let { event ->

                /*Updates both the hiker and the event*/

                updateHiker(hiker)
                updateEvent(event)
                deleteHikerAttendedEvent(hiker.id, event.id.toString())
                deleteEventRegisteredHiker(event.id.toString(), hiker.id)

                //TODO should be nice to remove the related history item
            }
        }
    }

    /******************************UI monitoring**************************************************/

    override fun getLayoutId(): Int = R.layout.activity_event

    override fun initializeUI() {
        //Nothing
    }

    override fun refreshUI() {
        //Nothing
    }

    /******************************Fragments monitoring******************************************/

    private fun showFragment(){
        this.fragment= EventFragment(this.event)
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

    /**Starts Event edit activity**/

    private fun startEventEditActivity(){
        val eventEditActivityIntent= Intent(this, EventEditActivity::class.java)
        eventEditActivityIntent.putExtra(EventEditActivity.KEY_BUNDLE_EVENT,this.event)
        startActivity(eventEditActivityIntent)
    }

    /**Starts Profile activity**/

    private fun startProfileActivity(hikerId:String){
        val profileActivityIntent=Intent(this, ProfileActivity::class.java)
        profileActivityIntent.putExtra(ProfileActivity.KEY_BUNDLE_HIKER_ID, hikerId)
        startActivity(profileActivityIntent)
    }

    /**Starts Trail activity**/

    private fun startTrailActivity(trailId:String){
        val trailActivityIntent=Intent(this, TrailActivity::class.java)
        trailActivityIntent.putExtra(TrailActivity.KEY_BUNDLE_TRAIL_ACTION, TrailActivity.ACTION_TRAIL_SEE)
        trailActivityIntent.putExtra(TrailActivity.KEY_BUNDLE_TRAIL_ID, trailId)
        startActivity(trailActivityIntent)
    }
}
