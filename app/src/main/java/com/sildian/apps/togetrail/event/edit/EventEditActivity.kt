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
import com.sildian.apps.togetrail.event.model.core.Event
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
