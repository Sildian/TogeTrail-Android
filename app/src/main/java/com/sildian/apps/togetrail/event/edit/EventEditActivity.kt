package com.sildian.apps.togetrail.event.edit

import android.app.Activity
import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.common.baseControllers.BaseActivity
import com.sildian.apps.togetrail.common.baseControllers.BaseFragment
import com.sildian.apps.togetrail.location.model.core.Location
import com.sildian.apps.togetrail.location.search.LocationSearchActivity
import com.sildian.apps.togetrail.trail.map.TrailActivity
import com.sildian.apps.togetrail.trail.model.core.Trail
import com.sildian.apps.togetrail.trail.selection.TrailSelectionActivity
import kotlinx.android.synthetic.main.activity_event_edit.*

/*************************************************************************************************
 * Allows a user to create or edit an event
 ************************************************************************************************/

class EventEditActivity : BaseActivity() {

    /**********************************Static items**********************************************/

    companion object {

        /**Bundle keys for intents**/
        const val KEY_BUNDLE_EVENT_ID="KEY_BUNDLE_EVENT_ID"     //Event's id -> Optional, if not provided a new event is created

        /**Request keys for intents**/
        private const val KEY_REQUEST_LOCATION_SEARCH=1001
        private const val KEY_REQUEST_TRAIL_SELECTION=1002
    }

    /****************************************Data************************************************/

    private var eventId: String?=null                           //The event's id

    /**********************************UI component**********************************************/

    private val toolbar by lazy {activity_event_edit_toolbar}
    private var fragment: BaseFragment?=null

    /********************************Navigation control******************************************/

    override fun onBackPressed() {
        finishCancel()
    }

    override fun onSupportNavigateUp(): Boolean {
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
        this.fragment?.saveData()
    }

    private fun readDataFromIntent(){
        if(intent!=null){
            if(intent.hasExtra(KEY_BUNDLE_EVENT_ID)){
                this.eventId= intent.getStringExtra(KEY_BUNDLE_EVENT_ID)
            }
        }
    }

    /******************************UI monitoring**************************************************/

    override fun getLayoutId(): Int = R.layout.activity_event_edit

    override fun initializeUI() {
        initializeToolbar()
        showFragment()
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

    private fun showFragment(){
        this.fragment= EventEditFragment(this.eventId)
        this.fragment?.let { fragment ->
            supportFragmentManager.beginTransaction()
                .replace(R.id.activity_event_edit_fragment, fragment).commit()
        }
    }

    /*************************************Navigation*********************************************/

    private fun startLocationSearchActivity(){
        val locationSearchActivityIntent=Intent(this, LocationSearchActivity::class.java)
        locationSearchActivityIntent.putExtra(LocationSearchActivity.KEY_BUNDLE_FINE_RESEARCH, true)
        startActivityForResult(locationSearchActivityIntent, KEY_REQUEST_LOCATION_SEARCH)
    }

    private fun startTrailSelectionActivity(selectedTrails:ArrayList<Trail>){
        val trailSelectionActivityIntent=Intent(this, TrailSelectionActivity::class.java)
        trailSelectionActivityIntent.putParcelableArrayListExtra(TrailSelectionActivity.KEY_BUNDLE_SELECTED_TRAILS, selectedTrails)
        startActivityForResult(trailSelectionActivityIntent, KEY_REQUEST_TRAIL_SELECTION)
    }

    private fun startTrailActivity(trailId:String){
        val trailActivityIntent=Intent(this, TrailActivity::class.java)
        trailActivityIntent.putExtra(TrailActivity.KEY_BUNDLE_TRAIL_ACTION, TrailActivity.ACTION_TRAIL_SEE)
        trailActivityIntent.putExtra(TrailActivity.KEY_BUNDLE_TRAIL_ID, trailId)
        startActivity(trailActivityIntent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            KEY_REQUEST_LOCATION_SEARCH -> handleLocationSearchActivityResult(resultCode, data)
            KEY_REQUEST_TRAIL_SELECTION -> handleTrailSelectionActivityResult(resultCode, data)
        }
    }

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
