package com.sildian.apps.togetrail.event.edit

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
import com.sildian.apps.togetrail.common.utils.cloudHelpers.UserFirebaseHelper
import com.sildian.apps.togetrail.common.utils.uiHelpers.DialogHelper
import com.sildian.apps.togetrail.event.model.core.Event
import com.sildian.apps.togetrail.event.model.support.EventFirebaseQueries
import com.sildian.apps.togetrail.event.model.support.EventHelper
import com.sildian.apps.togetrail.main.MainActivity
import kotlinx.android.synthetic.main.activity_event_edit.*

/*************************************************************************************************
 * Allows a user to create or edit an event
 ************************************************************************************************/

class EventEditActivity : AppCompatActivity() {

    /**********************************Static items**********************************************/

    companion object {

        /**Logs**/
        private const val TAG_ACTIVITY = "TAG_ACTIVITY"
        private const val TAG_MENU = "TAG_MENU"
        private const val TAG_STORAGE = "TAG_STORAGE"
    }

    /****************************************Data************************************************/

    private var event: Event?=null                          //The event

    /**********************************UI component**********************************************/

    private val toolbar by lazy {activity_event_edit_toolbar}
    private lateinit var fragment: Fragment
    private lateinit var progressDialog: AlertDialog

    /********************************Attached flows**********************************************/

    private lateinit var saveDataFlow: SaveDataFlow      //Flow used when the user clicks on save menu

    /************************************Life cycle**********************************************/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG_ACTIVITY, "Activity '${javaClass.simpleName}' created")
        setContentView(R.layout.activity_event_edit)
        readDataFromIntent(intent)
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
            if(intent.hasExtra(MainActivity.KEY_BUNDLE_EVENT)){
                this.event= intent.getParcelableExtra(MainActivity.KEY_BUNDLE_EVENT)
            }else{
                val name=resources.getString(R.string.message_event_name_unknown)
                this.event=EventHelper.buildFromNothing(name)
            }
        }
    }

    fun updateEvent(event:Event){
        this.event=event
    }

    fun updateEventAndSave(event: Event){
        updateEvent(event)
        this.progressDialog= DialogHelper.createProgressDialog(this)
        this.progressDialog.show()
        saveEvent()
    }

    private fun saveEvent(){

        /*If the event has no id, it means it was not created in the database yet. Then creates it.*/

        if(this.event?.id==null){
            EventFirebaseQueries.createEvent(this.event!!)
                .addOnSuccessListener { documentReference->

                    /*Once created, updates it with the created id*/

                    Log.d(TAG_STORAGE, "Event created in the database")
                    this.event?.id=documentReference.id
                    EventFirebaseQueries.updateEvent(this.event!!)
                        .addOnSuccessListener {
                            Log.d(TAG_STORAGE, "Event updated in the database")
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
            EventFirebaseQueries.updateEvent(this.event!!)
                .addOnSuccessListener {
                    Log.d(TAG_STORAGE, "Event updated in the database")
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

    /******************************UI monitoring**************************************************/

    private fun initializeToolbar(){
        setSupportActionBar(this.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle(R.string.toolbar_event)
    }

    /******************************Fragments monitoring******************************************/

    /**
     * Shows a fragment
     */

    private fun showFragment(){
        this.fragment= EventEditFragment(this.event)
        this.saveDataFlow=this.fragment as SaveDataFlow
        supportFragmentManager.beginTransaction()
            .replace(R.id.activity_event_edit_fragment, this.fragment).commit()
    }

    /*************************************Navigation*********************************************/

    private fun finishOk(){
        val resultIntent=Intent()
        resultIntent.putExtra(MainActivity.KEY_BUNDLE_EVENT, this.event)
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }

    private fun finishCancel(){
        setResult(Activity.RESULT_CANCELED)
        finish()
    }
}
