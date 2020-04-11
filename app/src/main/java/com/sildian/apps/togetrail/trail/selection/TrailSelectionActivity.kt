package com.sildian.apps.togetrail.trail.selection

import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.firestore.Query
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.common.flows.BaseDataFlowActivity
import com.sildian.apps.togetrail.common.utils.cloudHelpers.AuthFirebaseHelper
import com.sildian.apps.togetrail.hiker.model.core.Hiker
import com.sildian.apps.togetrail.trail.map.TrailActivity
import com.sildian.apps.togetrail.trail.model.core.Trail
import com.sildian.apps.togetrail.trail.model.support.TrailFirebaseQueries
import kotlinx.android.synthetic.main.activity_trail_selection.*

/*************************************************************************************************
 * This activity displays different queries to select trails to attach to an event
 ************************************************************************************************/

class TrailSelectionActivity : BaseDataFlowActivity() {

    /**********************************Static items**********************************************/

    companion object{

        /**Bundle keys for intents**/
        const val KEY_BUNDLE_SELECTED_TRAILS="KEY_BUNDLE_SELECTED_TRAILS"

        /**Queries keys**/
        private const val KEY_QUERY_LAST_TRAILS="KEY_QUERY_LAST_TRAILS"
        private const val KEY_QUERY_MY_TRAILS="KEY_QUERY_MY_TRAILS"
        private const val KEY_QUERY_TRAILS_NEARBY_HOME="KEY_QUERY_TRAILS_NEARBY_HOME"
    }

    /**************************************Data**************************************************/

    private var currentUser: Hiker?=null                                //The current user connected to the app
    private val trailsQueries: HashMap <String, Query> = hashMapOf()    //A hashMap with the trails queries to show (key=id (see above), entry=query)
    private var selectedTrails: ArrayList<Trail> = arrayListOf()        //The list of selected trails

    /**********************************UI component**********************************************/

    private val toolbar by lazy {activity_trail_selection_toolbar}
    private val progressbar by lazy {activity_trail_selection_progressbar}
    private val viewpagerLayout by lazy {activity_trail_selection_layout_viewpager}
    private val tabs by lazy {activity_trail_selection_tabs}
    private val pager by lazy {activity_trail_selection_pager}
    private val validateButton by lazy {activity_trail_selection_button_validate}

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

    /***********************************Data monitoring******************************************/

    override fun loadData() {
        readDataFromIntent()
        loadCurrentUserFromDatabase()
    }

    private fun readDataFromIntent(){
        if(intent!=null){
            if(intent.hasExtra(KEY_BUNDLE_SELECTED_TRAILS)){
                this.selectedTrails=intent.getParcelableArrayListExtra(KEY_BUNDLE_SELECTED_TRAILS)
            }
        }
    }

    private fun loadCurrentUserFromDatabase(){
        val user=AuthFirebaseHelper.getCurrentUser()
        user?.uid?.let { userId ->
            getHiker(userId, this::handleHikerResult)
        }
    }

    private fun handleHikerResult(hiker:Hiker?){
        this.progressbar.visibility=View.GONE
        this.currentUser=hiker
        defineTrailsQueries()
        showViewPager()
        activateValidateButton()
    }

    private fun defineTrailsQueries(){

        /*Adds the last added trails query*/

        this.trailsQueries[KEY_QUERY_LAST_TRAILS]=
            TrailFirebaseQueries.getLastTrails()

        this.currentUser?.let { user ->

            /*If the user created trails, adds the my trails query*/

            if (user.nbTrailsCreated > 0) {
                this.trailsQueries[KEY_QUERY_MY_TRAILS] =
                    TrailFirebaseQueries.getMyTrails(user.id)
            }

            /*If the user indicated a live location, adds the nearby home query*/

            if (user.liveLocation.country != null) {
                this.trailsQueries[KEY_QUERY_TRAILS_NEARBY_HOME] =
                    TrailFirebaseQueries.getTrailsNearbyLocation(user.liveLocation)!!
            }
        }
    }

    /*************************************UI monitoring******************************************/

    override fun getLayoutId(): Int = R.layout.activity_trail_selection

    override fun initializeUI() {
        initializeToolbar()
    }

    private fun initializeToolbar(){
        setSupportActionBar(this.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle(R.string.toolbar_event_select_trails)
    }

    private fun activateValidateButton(){
        this.validateButton.setOnClickListener {
            finishOk()
        }
    }

    /**********************************Trails monitoring******************************************/

    fun addSelectedTrail(trail:Trail){
        if(this.selectedTrails.firstOrNull { it.id==trail.id } ==null){
            this.selectedTrails.add(trail)
        }
    }

    fun removeSelectedTrail(trail:Trail){
        val trailToRemove= this.selectedTrails.firstOrNull { it.id==trail.id }
        if(trailToRemove !=null) {
            this.selectedTrails.remove(trailToRemove)
        }
    }

    fun seeTrail(trail:Trail){
        startTrailActivity(trail)
    }

    /**********************************View pager monitoring**************************************/

    private fun showViewPager(){
        this.viewpagerLayout.visibility= View.VISIBLE
        this.pager.adapter=TrailSelectionAdapter(this as FragmentActivity)
        TabLayoutMediator(this.tabs, this.pager) { tab, position ->
            when(this.trailsQueries.keys.toList()[position]){
                KEY_QUERY_LAST_TRAILS -> tab.setText(R.string.label_trail_list_last_added)
                KEY_QUERY_MY_TRAILS -> tab.setText(R.string.label_trail_list_my_trails)
                KEY_QUERY_TRAILS_NEARBY_HOME -> tab.setText(R.string.label_trail_list_nearby_trails)
            }
        }.attach()
    }

    /*******************************Adapter for the ViewPager************************************/

    inner class TrailSelectionAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

        override fun getItemCount(): Int = trailsQueries.size

        override fun createFragment(position: Int): Fragment {
            return TrailSelectionFragment(trailsQueries.values.toList()[position], selectedTrails)
        }
    }

    /*************************************Navigation*********************************************/

    private fun startTrailActivity(trail:Trail){
        val trailActivityIntent= Intent(this, TrailActivity::class.java)
        trailActivityIntent.putExtra(TrailActivity.KEY_BUNDLE_TRAIL_ACTION, TrailActivity.ACTION_TRAIL_SEE)
        trailActivityIntent.putExtra(TrailActivity.KEY_BUNDLE_TRAIL, trail)
        startActivity(trailActivityIntent)
    }

    private fun finishOk(){
        val resultIntent=Intent()
        resultIntent.putExtra(KEY_BUNDLE_SELECTED_TRAILS, this.selectedTrails)
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }

    private fun finishCancel(){
        setResult(Activity.RESULT_CANCELED)
        finish()
    }
}
