package com.sildian.apps.togetrail.event.edit

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
import com.sildian.apps.togetrail.common.utils.cloudHelpers.UserFirebaseHelper
import com.sildian.apps.togetrail.common.utils.uiHelpers.DialogHelper
import com.sildian.apps.togetrail.event.model.core.Event
import com.sildian.apps.togetrail.event.model.support.EventFirebaseQueries
import com.sildian.apps.togetrail.hiker.model.core.Hiker
import com.sildian.apps.togetrail.hiker.model.core.HikerHistoryItem
import com.sildian.apps.togetrail.hiker.model.core.HikerHistoryType
import com.sildian.apps.togetrail.hiker.model.support.HikerFirebaseQueries
import com.sildian.apps.togetrail.location.model.core.Location
import com.sildian.apps.togetrail.location.search.LocationSearchActivity
import com.sildian.apps.togetrail.trail.model.core.Trail
import kotlinx.android.synthetic.main.activity_event_edit.*

/*************************************************************************************************
 * Allows a user to create or edit an event
 ************************************************************************************************/

class EventEditActivity : BaseDataFlowActivity() {

    /**********************************Static items**********************************************/

    companion object {

        /**Logs**/
        private const val TAG="EventEditActivity"

        /**Bundle keys for intents**/
        const val KEY_BUNDLE_EVENT="KEY_BUNDLE_EVENT"
        const val KEY_BUNDLE_HIKER="KEY_BUNDLE_HIKER"

        /**Request keys for intents**/
        private const val KEY_REQUEST_LOCATION_SEARCH=1001
    }

    /****************************************Data************************************************/

    private var event: Event?=null                          //The event
    private var hiker: Hiker?=null                          //The current hiker
    private val attachedTrails= arrayListOf<Trail>()        //The list of attached trails (useful only when the event has no id yet)

    /**********************************UI component**********************************************/

    private val toolbar by lazy {activity_event_edit_toolbar}
    private lateinit var fragment: BaseDataFlowFragment
    private lateinit var progressDialog: AlertDialog

    /************************************Life cycle**********************************************/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "Activity '${javaClass.simpleName}' created")
        setContentView(R.layout.activity_event_edit)
        loadData()
        initializeToolbar()
        showFragment()
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
        Log.d(TAG, "Menu '${item.title}' clicked")
        if(item.groupId==R.id.menu_save){
            if(item.itemId==R.id.menu_save_save){
                saveData()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /******************************Data monitoring************************************************/

    override fun loadData() {
        readDataFromIntent()
    }

    override fun saveData() {
        this.fragment.saveData()
    }

    private fun readDataFromIntent(){
        if(intent!=null){
            if(intent.hasExtra(KEY_BUNDLE_EVENT)){
                this.event= intent.getParcelableExtra(KEY_BUNDLE_EVENT)
            }else{
                this.event=Event()
            }
            if(intent.hasExtra(KEY_BUNDLE_HIKER)){
                this.hiker=intent.getParcelableExtra(KEY_BUNDLE_HIKER)
            }
        }
    }

    fun updateAttachedTrailsToUpdate(attachedTrails:List<Trail>){
        this.attachedTrails.clear()
        this.attachedTrails.addAll(attachedTrails)
    }

    fun saveEvent(){

        /*Shows a progress dialog*/

        this.progressDialog= DialogHelper.createProgressDialog(this)
        this.progressDialog.show()

        /*If the event has no id, it means it was not created in the database yet. Then creates it.*/

        if(this.event?.id==null){

            this.event?.authorId=UserFirebaseHelper.getCurrentUser()?.uid

            EventFirebaseQueries.createEvent(this.event!!)
                .addOnSuccessListener { documentReference->

                    /*Once created...*/

                    this.event?.id=documentReference.id
                    Log.d(TAG, "Event '${this.event?.id}' created in the database")

                    /*Updates the attached trails*/

                    this.attachedTrails.forEach { trail ->
                        updateAttachedTrail(trail)
                    }

                    /*And updates the event with the created id*/

                    EventFirebaseQueries.updateEvent(this.event!!)
                        .addOnSuccessListener {
                            Log.d(TAG, "Event '${this.event?.id}' updated in the database")
                            progressDialog.dismiss()
                            //TODO show a snackbar when finished

                            /*Also updates the hiker and the history*/

                            this.hiker!!.nbEventsCreated++
                            val historyItem=HikerHistoryItem(
                                HikerHistoryType.EVENT_CREATED,
                                this.event?.creationDate!!,
                                this.event?.id!!,
                                this.event?.name!!,
                                this.event?.meetingPoint?.toString()
                            )
                            HikerFirebaseQueries.createOrUpdateHiker(this.hiker!!)
                            HikerFirebaseQueries.addHistoryItem(this.hiker!!.id, historyItem)

                            finishOk()
                        }
                        .addOnFailureListener { e ->
                            Log.w(TAG, e.message.toString())
                            progressDialog.dismiss()
                            //TODO handle
                            finishOk()
                        }
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, e.message.toString())
                    this.progressDialog.dismiss()
                    //TODO handle
                    finishCancel()
                }

            /*Else updates it*/

        }else{
            EventFirebaseQueries.updateEvent(this.event!!)
                .addOnSuccessListener {
                    Log.d(TAG, "Event '${this.event?.id}' updated in the database")
                    this.progressDialog.dismiss()
                    //TODO show a snackbar when finished
                    finishOk()
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, e.message.toString())
                    this.progressDialog.dismiss()
                    //TODO handle
                    finishCancel()
                }
        }
    }

    fun updateAttachedTrail(trail: Trail){
        EventFirebaseQueries.updateAttachedTrail(this.event?.id!!, trail)
            .addOnSuccessListener {
                Log.d(TAG, "Attached trail '${trail.id}' updated in the database")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, e.message.toString())
            }
    }

    fun deleteAttachedTrail(trail: Trail){
        EventFirebaseQueries.deleteAttachedTrail(this.event?.id!!, trail.id!!)
            .addOnSuccessListener {
                Log.d(TAG, "Attached trail '${trail.id}' deleted from the database")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, e.message.toString())
            }
    }

    /******************************UI monitoring**************************************************/

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
        this.fragment.updateData()
    }

    /******************************Fragments monitoring******************************************/

    /**
     * Shows a fragment
     */

    private fun showFragment(){
        this.fragment= EventEditFragment(this.event)
        supportFragmentManager.beginTransaction()
            .replace(R.id.activity_event_edit_fragment, this.fragment).commit()
    }

    /*************************************Navigation*********************************************/

    private fun startLocationSearchActivity(){
        val locationSearchActivityIntent=Intent(this, LocationSearchActivity::class.java)
        locationSearchActivityIntent.putExtra(LocationSearchActivity.KEY_BUNDLE_FINE_RESEARCH, true)
        startActivityForResult(locationSearchActivityIntent, KEY_REQUEST_LOCATION_SEARCH)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode== KEY_REQUEST_LOCATION_SEARCH){
            if(data!=null&&data.hasExtra(LocationSearchActivity.KEY_BUNDLE_LOCATION)){
                val location=data.getParcelableExtra<Location>(LocationSearchActivity.KEY_BUNDLE_LOCATION)
                updateMeetingPoint(location)
            }
        }
    }

    private fun finishOk(){
        val resultIntent=Intent()
        resultIntent.putExtra(KEY_BUNDLE_EVENT, this.event)
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }

    private fun finishCancel(){
        setResult(Activity.RESULT_CANCELED)
        finish()
    }
}
