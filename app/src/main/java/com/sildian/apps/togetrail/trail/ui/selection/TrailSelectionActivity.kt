package com.sildian.apps.togetrail.trail.ui.selection

import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.firestore.Query
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.common.baseControllers.BaseActivity
import com.sildian.apps.togetrail.databinding.ActivityTrailSelectionBinding
import com.sildian.apps.togetrail.hiker.data.helpers.CurrentHikerInfo
import com.sildian.apps.togetrail.location.data.core.Location
import com.sildian.apps.togetrail.location.ui.search.LocationSearchActivity
import com.sildian.apps.togetrail.trail.ui.map.TrailActivity
import com.sildian.apps.togetrail.trail.data.core.Trail
import com.sildian.apps.togetrail.trail.data.source.TrailFirebaseQueries

/*************************************************************************************************
 * This activity displays different queries to select trails to attach to an event
 ************************************************************************************************/

class TrailSelectionActivity : BaseActivity<ActivityTrailSelectionBinding>() {

    /**********************************Static items**********************************************/

    companion object{

        /**Bundle keys for intents**/
        const val KEY_BUNDLE_SELECTED_TRAILS="KEY_BUNDLE_SELECTED_TRAILS"

        /**Queries keys for hashMap**/
        private const val KEY_QUERY_LAST_TRAILS="KEY_QUERY_LAST_TRAILS"
        private const val KEY_QUERY_MY_TRAILS="KEY_QUERY_MY_TRAILS"
        private const val KEY_QUERY_TRAILS_NEARBY_HOME="KEY_QUERY_TRAILS_NEARBY_HOME"
        private const val KEY_QUERY_CURRENT_RESEARCH="KEY_QUERY_CURRENT_RESEARCH"

        /**Request keys for activities**/
        private const val KEY_REQUEST_LOCATION_SEARCH=1001
    }

    /**************************************Data**************************************************/

    private val trailsQueries: HashMap <String, Query> = hashMapOf()    //A hashMap with the trails queries to show (key=id (see above), entry=query)
    private var selectedTrails: ArrayList<Trail> = arrayListOf()        //The list of selected trails

    /********************************Navigation control******************************************/

    override fun onBackPressed() {
        finishCancel()
    }

    override fun onSupportNavigateUp(): Boolean {
        finishCancel()
        return true
    }

    /***********************************Data monitoring******************************************/

    override fun initializeData() {
        readDataFromIntent()
        defineTrailsQueries()
    }

    private fun readDataFromIntent() {
        if (intent != null) {
            if (intent.hasExtra(KEY_BUNDLE_SELECTED_TRAILS)) {
                val trails=intent.getParcelableArrayListExtra<Trail>(KEY_BUNDLE_SELECTED_TRAILS)
                if (trails != null) {
                    this.selectedTrails = trails
                }
            }
        }
    }

    private fun defineTrailsQueries(){

        /*Adds the last trails query*/

        this.trailsQueries[KEY_QUERY_LAST_TRAILS]=
            TrailFirebaseQueries.getLastTrails()

        CurrentHikerInfo.currentHiker?.let { hiker ->

            /*If the hiker created trails, adds the my trails query*/

            if (hiker.nbTrailsCreated > 0) {
                this.trailsQueries[KEY_QUERY_MY_TRAILS] =
                    TrailFirebaseQueries.getMyTrails(hiker.id)
            }

            /*If the hiker indicated a live location, adds the nearby home query*/

            if (hiker.liveLocation.country != null) {
                this.trailsQueries[KEY_QUERY_TRAILS_NEARBY_HOME] =
                    TrailFirebaseQueries.getTrailsNearbyLocation(hiker.liveLocation)!!
            }
        }

        /*Adds the current research query (set to last trails by default)*/

        this.trailsQueries[KEY_QUERY_CURRENT_RESEARCH] =
            TrailFirebaseQueries.getLastTrails()
    }

    private fun setResearchQuery(location: Location){
        if(location.country!=null) {
            this.trailsQueries[KEY_QUERY_CURRENT_RESEARCH] =
                TrailFirebaseQueries.getTrailsNearbyLocation(location)!!
            this.binding.activityTrailSelectionPager.adapter?.notifyDataSetChanged()
            val index = this.trailsQueries.keys.toList().indexOf(KEY_QUERY_CURRENT_RESEARCH)
            this.binding.activityTrailSelectionPager.setCurrentItem(index, true)
            showViewPager()
        }
    }

    /*************************************UI monitoring******************************************/

    override fun getLayoutId(): Int = R.layout.activity_trail_selection

    override fun initializeUI() {
        initializeToolbar()
        initializeSearchTextField()
        initializeValidateButton()
        showViewPager()
    }

    private fun initializeToolbar(){
        setSupportActionBar(this.binding.activityTrailSelectionToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle(R.string.toolbar_event_select_trails)
    }

    private fun initializeSearchTextField(){
        this.binding.activityTrailSelectionTextFieldResearch.setOnClickListener {
            startLocationSearchActivity()
        }
    }

    private fun initializeValidateButton(){
        this.binding.activityTrailSelectionButtonValidate.setOnClickListener {
            finishOk()
        }
    }

    /**********************************Trails monitoring******************************************/

    fun addSelectedTrail(trail:Trail){
        if (this.selectedTrails.firstOrNull { it.id==trail.id } == null) {
            this.selectedTrails.add(trail)
        }
    }

    fun removeSelectedTrail(trail:Trail){
        val trailToRemove = this.selectedTrails.firstOrNull { it.id==trail.id }
        if (trailToRemove !=null) {
            this.selectedTrails.remove(trailToRemove)
        }
    }

    fun seeTrail(trail:Trail){
        startTrailActivity(trail)
    }

    /**********************************View pager monitoring**************************************/

    private fun showViewPager(){
        this.binding.activityTrailSelectionLayoutViewpager.visibility = View.VISIBLE
        this.binding.activityTrailSelectionPager.adapter = TrailSelectionAdapter(this as FragmentActivity)
        TabLayoutMediator(this.binding.activityTrailSelectionTabs, this.binding.activityTrailSelectionPager) { tab, position ->
            when (this.trailsQueries.keys.toList()[position]) {
                KEY_QUERY_LAST_TRAILS -> tab.setText(R.string.label_trail_list_last_added)
                KEY_QUERY_MY_TRAILS -> tab.setText(R.string.label_trail_list_my_trails)
                KEY_QUERY_TRAILS_NEARBY_HOME -> tab.setText(R.string.label_trail_list_nearby_home)
                KEY_QUERY_CURRENT_RESEARCH -> tab.setText(R.string.label_trail_list_current_research)
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
        trailActivityIntent.putExtra(TrailActivity.KEY_BUNDLE_TRAIL_ID, trail.id)
        startActivity(trailActivityIntent)
    }

    private fun startLocationSearchActivity(){
        val locationSearchActivity=Intent(this, LocationSearchActivity::class.java)
        startActivityForResult(locationSearchActivity, KEY_REQUEST_LOCATION_SEARCH)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            KEY_REQUEST_LOCATION_SEARCH -> handleLocationSearchResult(resultCode, data)
        }
    }

    private fun handleLocationSearchResult(resultCode: Int, data: Intent?){
        if(resultCode== Activity.RESULT_OK){
            if(data!=null && data.hasExtra(LocationSearchActivity.KEY_BUNDLE_LOCATION)){
                val location=data.getParcelableExtra<Location>(LocationSearchActivity.KEY_BUNDLE_LOCATION)
                location?.let { loc ->
                    this.binding.activityTrailSelectionTextFieldResearch.setText(loc.toString())
                    setResearchQuery(loc)
                }
            }
        }
    }

    override fun finishOk(){
        val resultIntent=Intent()
        resultIntent.putExtra(KEY_BUNDLE_SELECTED_TRAILS, this.selectedTrails)
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }
}
