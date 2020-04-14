package com.sildian.apps.togetrail.event.edit

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.common.baseControllers.BaseDataFlowActivity
import com.sildian.apps.togetrail.common.baseControllers.BaseDataFlowFragment
import com.sildian.apps.togetrail.common.utils.cloudHelpers.AuthFirebaseHelper
import com.sildian.apps.togetrail.common.utils.uiHelpers.DialogHelper
import com.sildian.apps.togetrail.event.model.core.Event
import com.sildian.apps.togetrail.hiker.model.core.Hiker
import com.sildian.apps.togetrail.hiker.model.core.HikerHistoryItem
import com.sildian.apps.togetrail.hiker.model.core.HikerHistoryType
import com.sildian.apps.togetrail.location.model.core.Location
import com.sildian.apps.togetrail.location.search.LocationSearchActivity
import com.sildian.apps.togetrail.trail.map.TrailActivity
import com.sildian.apps.togetrail.trail.model.core.Trail
import com.sildian.apps.togetrail.trail.selection.TrailSelectionActivity
import kotlinx.android.synthetic.main.activity_event_edit.*

/*************************************************************************************************
 * Allows a user to create or edit an event
 ************************************************************************************************/

class EventEditActivity : BaseDataFlowActivity() {

    /**********************************Static items**********************************************/

    companion object {

        /**Bundle keys for intents**/
        const val KEY_BUNDLE_EVENT="KEY_BUNDLE_EVENT"       //Event -> Optional, if not provided a new event is created

        /**Request keys for intents**/
        private const val KEY_REQUEST_LOCATION_SEARCH=1001
        private const val KEY_REQUEST_TRAIL_SELECTION=1002
    }

    /****************************************Data************************************************/

    private var event: Event?=null                          //The event
    private val attachedTrails= arrayListOf<Trail>()        //The list of attached trails (useful only when the event has no id yet)

    /**********************************UI component**********************************************/

    private val toolbar by lazy {activity_event_edit_toolbar}
    private var fragment: BaseDataFlowFragment?=null
    private var progressDialog: AlertDialog?=null

    /************************************Life cycle**********************************************/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showFragment()
    }

    /********************************Navigation control******************************************/

    override fun onBackPressed() {
        finish()
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
            if(intent.hasExtra(KEY_BUNDLE_EVENT)){
                this.event= intent.getParcelableExtra(KEY_BUNDLE_EVENT)
            }else{
                this.event=Event()
            }
        }
    }

    /**Updates the attached trails to update**/

    fun updateAttachedTrailsToUpdate(attachedTrails:List<Trail>){
        this.attachedTrails.clear()
        this.attachedTrails.addAll(attachedTrails)
    }

    /**Saves the event within the database**/

    fun saveEventInDatabase(){

        if(this.event!=null) {

            /*Shows a progress dialog*/

            this.progressDialog = DialogHelper.createProgressDialog(this)
            this.progressDialog?.show()

            /*If the event has no id, it means it was not created in the database yet. Then creates it.*/

            if (this.event?.id == null) {
                this.event?.authorId = AuthFirebaseHelper.getCurrentUser()?.uid
                createEventInDatabase()

                /*Else updates it*/

            } else {
                updateEventInDatabase()
            }
        }
    }

    /**Creates an event in the database**/

    private fun createEventInDatabase(){
        this.event?.let { event ->
            addEvent(event, this::handleCreatedEvent)
        }
    }

    /**
     * Handles the created event in the database
     * @param eventId : the created event's id
     */

    private fun handleCreatedEvent(eventId: String){
        this.event?.id=eventId
        updateEventInDatabase()
        updateCurrentUserInDatabase()
        this.attachedTrails.forEach { trail ->
            updateAttachedTrail(trail)
        }
    }

    /**Updates the event in the database**/

    private fun updateEventInDatabase(){
        this.event?.let { event ->
            updateEvent(event, this::handleUpdatedEvent)
        }
    }

    /**Handles the updated event in the database**/

    private fun handleUpdatedEvent(){
        this.progressDialog?.dismiss()
        finish()
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
            updateHiker(hikerToUpdate)
            val historyItem = HikerHistoryItem(
                HikerHistoryType.EVENT_CREATED,
                this.event?.creationDate!!,
                this.event?.id!!,
                this.event?.name!!,
                this.event?.meetingPoint?.toString(),
                this.event?.mainPhotoUrl
            )
            addHikerHistoryItem(hikerToUpdate.id, historyItem)
            this.progressDialog?.dismiss()
            finish()
        }
    }

    /**
     * Attaches a trail to the event
     * @param trail : the trail to attach
     */

    fun updateAttachedTrail(trail: Trail){
        this.event?.id?.let { eventId ->
            updateEventAttachedTrail(eventId, trail)
        }
    }

    /**
     * Detaches a trail from the event
     * @param trail : the trail to detach
     */

    fun deleteAttachedTrail(trail: Trail){
        this.event?.id?.let { eventId ->
            deleteEventAttachedTrail(eventId, trail.id.toString())
        }
    }

    /******************************UI monitoring**************************************************/

    override fun getLayoutId(): Int = R.layout.activity_event_edit

    override fun initializeUI() {
        initializeToolbar()
    }

    private fun initializeToolbar(){
        setSupportActionBar(this.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle(R.string.toolbar_event)
    }

    /*******************************Location monitoring******************************************/

    fun searchLocation(){
        startLocationSearchActivity()
    }

    private fun updateMeetingPoint(location: Location){
        this.event?.meetingPoint=location
        this.fragment?.updateData(location)
    }

    /*********************************Trails monitoring******************************************/

    fun selectTrail(selectedTrails:ArrayList<Trail>){
        startTrailSelectionActivity(selectedTrails)
    }

    fun seeTrail(trailId:String){
        startTrailActivity(trailId)
    }

    /******************************Fragments monitoring******************************************/

    /**Shows the fragment**/

    private fun showFragment(){
        this.fragment= EventEditFragment(this.event)
        this.fragment?.let { fragment ->
            supportFragmentManager.beginTransaction()
                .replace(R.id.activity_event_edit_fragment, fragment).commit()
        }
    }

    /*************************************Navigation*********************************************/

    /**Starts Location search activity**/

    private fun startLocationSearchActivity(){
        val locationSearchActivityIntent=Intent(this, LocationSearchActivity::class.java)
        locationSearchActivityIntent.putExtra(LocationSearchActivity.KEY_BUNDLE_FINE_RESEARCH, true)
        startActivityForResult(locationSearchActivityIntent, KEY_REQUEST_LOCATION_SEARCH)
    }

    /**Starts Trail selection Activity**/

    private fun startTrailSelectionActivity(selectedTrails:ArrayList<Trail>){
        val trailSelectionActivityIntent=Intent(this, TrailSelectionActivity::class.java)
        trailSelectionActivityIntent.putParcelableArrayListExtra(TrailSelectionActivity.KEY_BUNDLE_SELECTED_TRAILS, selectedTrails)
        startActivityForResult(trailSelectionActivityIntent, KEY_REQUEST_TRAIL_SELECTION)
    }

    /**Starts Trail activity**/

    private fun startTrailActivity(trailId:String){
        val trailActivityIntent=Intent(this, TrailActivity::class.java)
        trailActivityIntent.putExtra(TrailActivity.KEY_BUNDLE_TRAIL_ACTION, TrailActivity.ACTION_TRAIL_SEE)
        trailActivityIntent.putExtra(TrailActivity.KEY_BUNDLE_TRAIL_ID, trailId)
        startActivity(trailActivityIntent)
    }

    /**Gets Activity result**/

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            KEY_REQUEST_LOCATION_SEARCH -> handleLocationSearchActivityResult(resultCode, data)
            KEY_REQUEST_TRAIL_SELECTION -> handleTrailSelectionActivityResult(resultCode, data)
        }
    }

    /**Handles Location search activity result**/

    private fun handleLocationSearchActivityResult(resultCode: Int, data: Intent?){
        if(resultCode== Activity.RESULT_OK) {
            if (data != null && data.hasExtra(LocationSearchActivity.KEY_BUNDLE_LOCATION)) {
                val location =
                    data.getParcelableExtra<Location>(LocationSearchActivity.KEY_BUNDLE_LOCATION)
                location?.let { loc ->
                    updateMeetingPoint(loc)
                }
            }
        }
    }

    /**Handles Trail selection activity result**/

    private fun handleTrailSelectionActivityResult(resultCode: Int, data: Intent?){
        if(resultCode==Activity.RESULT_OK){
            if(data!=null && data.hasExtra(TrailSelectionActivity.KEY_BUNDLE_SELECTED_TRAILS)){
                val selectedTrails=data.getParcelableArrayListExtra<Trail>(TrailSelectionActivity.KEY_BUNDLE_SELECTED_TRAILS)
                selectedTrails?.let { trails ->
                    this.fragment?.updateData(trails)
                }
            }
        }
    }
}
