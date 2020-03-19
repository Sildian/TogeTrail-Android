package com.sildian.apps.togetrail.event.detail

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
import com.sildian.apps.togetrail.event.edit.EventEditActivity
import com.sildian.apps.togetrail.event.model.core.Event
import com.sildian.apps.togetrail.event.model.support.EventFirebaseQueries
import com.sildian.apps.togetrail.event.model.support.EventHelper
import com.sildian.apps.togetrail.hiker.model.core.Hiker
import com.sildian.apps.togetrail.hiker.model.support.HikerFirebaseQueries
import kotlinx.android.synthetic.main.activity_event.*

/*************************************************************************************************
 * Displays an event's detail info and allows a user to register on this event
 ************************************************************************************************/

class EventActivity : BaseDataFlowActivity() {

    /**********************************Static items**********************************************/

    companion object {

        /**Logs**/
        private const val TAG="EventActivity"

        /**Request keys for activities**/
        private const val KEY_REQUEST_EVENT_EDIT=1001

        /**Bundle keys for intents**/
        const val KEY_BUNDLE_EVENT="KEY_BUNDLE_EVENT"
        const val KEY_BUNDLE_HIKER="KEY_BUNDLE_HIKER"
    }

    /****************************************Data************************************************/

    private var event: Event?=null                          //The event
    private var hiker: Hiker?=null                          //The current hiker

    /**********************************UI component**********************************************/

    private val toolbar by lazy {activity_event_toolbar}
    private lateinit var fragment: BaseDataFlowFragment
    private lateinit var progressDialog: AlertDialog

    /************************************Life cycle**********************************************/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "Activity '${javaClass.simpleName}' created")
        setContentView(R.layout.activity_event)
        loadData()
        initializeToolbar()
        showFragment()
    }

    /********************************Menu monitoring*********************************************/

    /**Generates the menu within the toolbar**/

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_edit, menu)
        return true
    }

    /**Click on menu item from toolbar**/

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.d(TAG, "Menu '${item.title}' clicked")
        if(item.groupId==R.id.menu_edit){
            if(item.itemId==R.id.menu_edit_edit){
                editEvent()
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

    override fun loadData() {
        readDataFromIntent()
    }

    private fun readDataFromIntent(){
        if(intent!=null){
            if(intent.hasExtra(KEY_BUNDLE_EVENT)){
                this.event= intent.getParcelableExtra(KEY_BUNDLE_EVENT)
            }else{
                this.event= EventHelper.buildFromNothing()
            }
            if(intent.hasExtra(KEY_BUNDLE_HIKER)){
                this.hiker=intent.getParcelableExtra(KEY_BUNDLE_HIKER)
            }
        }
    }

    fun registerUserToEvent(){
        if(this.hiker!=null){

            /*Increases the number of hikers registered to the event*/

            this.event!!.nbHikersRegistered++
            this.hiker!!.nbEventsAttended++

            /*Updates the hiker registered to the event*/

            EventFirebaseQueries.updateRegisteredHiker(this.event?.id!!, this.hiker!!)
                .addOnSuccessListener {

                    /*Then updates the event for which the hiker attended*/

                    HikerFirebaseQueries.updateAttendedEvent(this.hiker?.id!!, this.event!!)
                        .addOnSuccessListener {
                            Log.d(TAG, "Hiker registered successfully")
                        }
                        .addOnFailureListener { e ->
                            Log.w(TAG, e.message.toString())
                            //TODO handle
                        }
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, e.message.toString())
                    //TODO handle
                }

            /*Also updates the event within the original tree*/

            EventFirebaseQueries.updateEvent(this.event!!)
                .addOnFailureListener { e ->
                    Log.w(TAG, e.message.toString())
                }

            /*And updates the hiker within the original tree*/

            HikerFirebaseQueries.createOrUpdateHiker(this.hiker!!)
                .addOnFailureListener { e ->
                    Log.w(TAG, e.message.toString())
                }
        }
    }

    fun unregisterUserFromEvent(){
        if(this.hiker!=null){

            /*Decreases the number of hikers registered to the event*/

            this.event!!.nbHikersRegistered--
            this.hiker!!.nbEventsAttended--

            /*Deletes the hiker registered from the event*/

            EventFirebaseQueries.deleteRegisteredHiker(this.event?.id!!, this.hiker?.id!!)
                .addOnSuccessListener {

                    /*Then deletes the event for which the hiker attended*/

                    HikerFirebaseQueries.deleteAttendedEvent(this.hiker?.id!!, this.event?.id!!)
                        .addOnSuccessListener {
                            Log.d(TAG, "Hiker unregistered successfully")
                        }
                        .addOnFailureListener { e ->
                            Log.w(TAG, e.message.toString())
                            //TODO handle
                        }
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, e.message.toString())
                    //TODO handle
                }

            /*Also updates the event within the original tree*/

            EventFirebaseQueries.updateEvent(this.event!!)
                .addOnFailureListener { e ->
                    Log.w(TAG, e.message.toString())
                }

            /*And updates the hiker within the original tree*/

            HikerFirebaseQueries.createOrUpdateHiker(this.hiker!!)
                .addOnFailureListener { e ->
                    Log.w(TAG, e.message.toString())
                }
        }
    }

    /******************************UI monitoring**************************************************/

    private fun initializeToolbar(){
        setSupportActionBar(this.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle(R.string.toolbar_event)
    }

    /******************************Event monitoring**********************************************/

    fun editEvent(){
        startEventEditActivity()
    }

    /******************************Fragments monitoring******************************************/

    /**
     * Shows a fragment
     */

    private fun showFragment(){
        this.fragment= EventFragment(this.event, this.hiker)
        supportFragmentManager.beginTransaction()
            .replace(R.id.activity_event_fragment, this.fragment).commit()
    }

    /***********************************Navigation***********************************************/

    private fun startEventEditActivity(){
        val eventEditActivityIntent= Intent(this, EventEditActivity::class.java)
        eventEditActivityIntent.putExtra(EventEditActivity.KEY_BUNDLE_EVENT,this.event)
        startActivityForResult(eventEditActivityIntent, KEY_REQUEST_EVENT_EDIT)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            KEY_REQUEST_EVENT_EDIT -> handleEventEditActivityResult(resultCode, data)
        }
    }

    private fun handleEventEditActivityResult(resultCode: Int, data: Intent?){
        if(resultCode== Activity.RESULT_OK){
            if(data!=null && data.hasExtra(EventEditActivity.KEY_BUNDLE_EVENT)){
                this.event=data.getParcelableExtra(EventEditActivity.KEY_BUNDLE_EVENT)
                (this.fragment as EventFragment).updateEvent(this.event)
            }
        }
    }
}
