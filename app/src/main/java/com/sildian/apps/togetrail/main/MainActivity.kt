package com.sildian.apps.togetrail.main

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.view.menu.MenuPopupHelper
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.common.flows.BaseDataFlowFragment
import com.sildian.apps.togetrail.common.utils.cloudHelpers.UserFirebaseHelper
import com.sildian.apps.togetrail.event.detail.EventActivity
import com.sildian.apps.togetrail.event.edit.EventEditActivity
import com.sildian.apps.togetrail.event.list.EventsListFragment
import com.sildian.apps.togetrail.event.model.core.Event
import com.sildian.apps.togetrail.event.model.support.EventFirebaseQueries
import com.sildian.apps.togetrail.hiker.profileEdit.ProfileEditActivity
import com.sildian.apps.togetrail.hiker.model.core.Hiker
import com.sildian.apps.togetrail.hiker.model.core.HikerHistoryItem
import com.sildian.apps.togetrail.hiker.model.core.HikerHistoryType
import com.sildian.apps.togetrail.hiker.model.support.HikerHelper
import com.sildian.apps.togetrail.hiker.model.support.HikerFirebaseQueries
import com.sildian.apps.togetrail.hiker.profile.ProfileActivity
import com.sildian.apps.togetrail.location.model.core.Location
import com.sildian.apps.togetrail.location.search.LocationSearchActivity
import com.sildian.apps.togetrail.trail.list.TrailsListFragment
import com.sildian.apps.togetrail.trail.map.TrailActivity
import com.sildian.apps.togetrail.trail.map.TrailMapFragment
import com.sildian.apps.togetrail.trail.model.core.Trail
import com.sildian.apps.togetrail.trail.model.support.TrailFirebaseQueries
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.navigation_view_header.view.*
import net.danlew.android.joda.JodaTimeAndroid

/*************************************************************************************************
 * Lets the user navigate between the main screens
 ************************************************************************************************/

class MainActivity :
    AppCompatActivity(),
    NavigationView.OnNavigationItemSelectedListener,
    BottomNavigationView.OnNavigationItemSelectedListener
{

    /**********************************Static items**********************************************/

    companion object{

        /**Logs**/
        private const val TAG="MainActivity"

        /**Fragments Ids***/
        private const val ID_FRAGMENT_MAP=1
        private const val ID_FRAGMENT_TRAILS=2
        private const val ID_FRAGMENT_EVENTS=3

        /**Request keys for activities**/
        private const val KEY_REQUEST_LOGIN=1001
        private const val KEY_REQUEST_PROFILE=1002
        private const val KEY_REQUEST_LOCATION_SEARCH=1003

        /**Request keys for permissions**/
        private const val KEY_REQUEST_PERMISSION_LOCATION=2001

        /**Bundle keys for permissions**/
        private const val KEY_BUNDLE_PERMISSION_LOCATION=Manifest.permission.ACCESS_FINE_LOCATION
    }

    /****************************************Data************************************************/

    private var currentHiker: Hiker?=null               //The current hiker connected to the app
    private val trails= arrayListOf<Trail>()            //The list of trails to display
    private val events= arrayListOf<Event>()            //The list of events to display

    /*************************************Queries************************************************/

    private var trailsQuery=TrailFirebaseQueries.getLastTrails()    //The current query used to fetch trails
    private var eventsQuery=EventFirebaseQueries.getNextEvents()    //The current query used to fetch events

    /**********************************UI component**********************************************/

    private lateinit var fragment:BaseDataFlowFragment
    private val toolbar by lazy {activity_main_toolbar}
    private val searchTextField by lazy {activity_main_text_field_research}
    private val drawerLayout by lazy {activity_main_drawer_layout}
    private val navigationView by lazy {activity_main_navigation_view}
    private val navigationViewHeader by lazy {
        layoutInflater.inflate(R.layout.navigation_view_header, this.navigationView)
    }
    private val navigationHeaderUserImage by lazy {
        navigationViewHeader.navigation_view_header_user_image
    }
    private val navigationHeaderUserNameText by lazy {
        navigationViewHeader.navigation_view_header_text_user_name
    }
    private val bottomNavigationView by lazy {activity_main_bottom_navigation_view}
    private val addButton by lazy {activity_main_button_add}

    /************************************Life cycle**********************************************/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "Activity '${javaClass.simpleName}' created")
        setContentView(R.layout.activity_main)
        JodaTimeAndroid.init(this)
        Places.initialize(applicationContext, resources.getString(R.string.google_maps_key))
        initializeToolbar()
        initializeSearchTextField()
        initializeNavigationView()
        initializeBottomNavigationView()
        initializeAddButton()
        requestLocationPermission()
        updateAndSaveCurrentHiker()
    }

    /*******************************Menu monitoring**********************************************/

    /**Click on menu item from...**/

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        Log.d(TAG, "Menu '${item.title}' clicked")

        return when(item.groupId) {

            /*Bottom Navigation View*/

            R.id.menu_main -> {
                when (item.itemId) {
                    R.id.menu_main_map ->
                        showFragment(ID_FRAGMENT_MAP)
                    R.id.menu_main_trails ->
                        showFragment(ID_FRAGMENT_TRAILS)
                    R.id.menu_main_events ->
                        showFragment(ID_FRAGMENT_EVENTS)
                }
                true
            }

            /*Navigation View*/

            R.id.menu_user -> {
                //TODO handle clicks
                when (item.itemId) {
                    R.id.menu_user_profile -> {
                        if(UserFirebaseHelper.getCurrentUser()!=null){
                            startProfileActivity(ProfileActivity.ACTION_PROFILE_SEE_PROFILE)
                        }else{
                            //TODO handle user not connected
                        }
                    }
                    R.id.menu_user_settings ->
                        if(UserFirebaseHelper.getCurrentUser()!=null){
                            startProfileEditActivity(ProfileEditActivity.ACTION_PROFILE_EDIT_SETTINGS)
                        }else{
                            //TODO handle user not connected
                        }
                    R.id.menu_user_login ->
                        login()
                }
                true
            }
            else -> false
        }
    }

    /**Click on menu item from PopupMenu**/

    private fun onPopupMenuItemClick(item: MenuItem?): Boolean {

        Log.d(TAG, "Menu '${item?.title}' clicked")

        when(item?.groupId) {
            R.id.menu_add -> {
                when (item.itemId) {
                    R.id.menu_add_event ->
                        startEventEditActivity()
                }
            }
            R.id.menu_add_new_trail-> {
                when (item.itemId) {
                    R.id.menu_add_trail_load_gpx ->
                        startTrailActivity(TrailActivity.ACTION_TRAIL_CREATE_FROM_GPX)
                    R.id.menu_add_trail_draw ->
                        startTrailActivity(TrailActivity.ACTION_TRAIL_DRAW)
                    R.id.menu_add_trail_record ->
                        startTrailActivity(TrailActivity.ACTION_TRAIL_RECORD)
                }
            }
        }
        return true
    }

    /**Shows the add menu options within a PopupMenu**/

    @SuppressLint("RestrictedApi")
    private fun showAddMenuOptions(){

        /*Inflates the menu and sets the callback*/

        val menuBuilder=MenuBuilder(this)
        MenuInflater(this).inflate(R.menu.menu_add, menuBuilder)
        menuBuilder.setCallback(object:MenuBuilder.Callback{
            override fun onMenuModeChange(menu: MenuBuilder?) {
                //Nothing here
            }
            override fun onMenuItemSelected(menu: MenuBuilder?, item: MenuItem?): Boolean {
                onPopupMenuItemClick(item)
                return true
            }
        })

        /*Creates the popup and shows it*/

        val menuPopupHelper=MenuPopupHelper(this, menuBuilder, this.addButton)
        menuPopupHelper.setForceShowIcon(true)
        val menuPopupMargin=resources.getDimension(R.dimen.components_margin_medium).toInt()
        menuPopupHelper.show(menuPopupMargin, menuPopupMargin)
    }

    /****************************Data monitoring**************************************************/

    /**
     * Updates and saves the current hiker in the database
     */

    private fun updateAndSaveCurrentHiker(){

        /*Checks if the user is connected*/

        val user=UserFirebaseHelper.getCurrentUser()
        if(user!=null) {

            /*If the user is connected, gets its related hiker profile from the database*/

            HikerFirebaseQueries.getHiker(user.uid)
                .addOnSuccessListener { documentSnapshot ->

                    Log.d(TAG, "Hiker '${user.uid}' loaded from the database")
                    val hiker=documentSnapshot.toObject(Hiker::class.java)

                    /*If the profile exists, then updates the currentHiker*/

                    if(hiker!=null) {
                        this.currentHiker = hiker
                    }

                    /*Else, creates a hiker profile in the database*/

                    else{
                        this.currentHiker=HikerHelper.buildFromFirebaseUser(user)
                        HikerFirebaseQueries.createOrUpdateHiker(this.currentHiker!!)
                            .addOnSuccessListener {
                                //TODO handle
                                Log.d(TAG, "Hiker '${user.uid}' registered in the database")

                                /*And updates the hiker's history*/

                                val historyItem= HikerHistoryItem(
                                    HikerHistoryType.HIKER_REGISTERED,
                                    this.currentHiker?.registrationDate!!
                                )
                                HikerFirebaseQueries.addHistoryItem(this.currentHiker?.id!!, historyItem)
                            }
                            .addOnFailureListener { e ->
                                //TODO handle
                                Log.w(TAG, e.message.toString())
                            }
                    }
                }
                .addOnFailureListener { e ->
                    //TODO handle
                    Log.w(TAG, e.message.toString())
                }
        }
    }

    /**
     * Loads the trails from the database
     * @param callback : the callback to handle the result
     */

    fun loadTrails(callback:(List<Trail>)->Unit){

        //TODO add a progress bar
        
        this.trailsQuery
            .addSnapshotListener { querySnapshot, e ->

                /*If the query is a success, displays the results*/

                if(querySnapshot!=null){
                    Log.d(TAG, "Query finished with ${querySnapshot.size()} trails")
                    this.trails.clear()
                    querySnapshot.forEach { documentSnapshot ->
                        val trail=documentSnapshot.toObject(Trail::class.java)
                        trail.id=documentSnapshot.id
                        this.trails.add(trail)
                        callback.invoke(this.trails)
                    }
                }

                /*Else*/

                else if(e!=null){
                    //TODO handle
                    Log.w(TAG, e.message.toString())
                }
            }
    }

    /**
     * Loads the events from the database
     * @param callback : the callback to handle the result
     */

    fun loadEvents(callback:(List<Event>)->Unit){

        //TODO add a progress bar

        this.eventsQuery
            .addSnapshotListener { querySnapshot, e ->

                /*If the query is a success, displays the results*/

                if(querySnapshot!=null){
                    Log.d(TAG, "Query finished with ${querySnapshot.size()} events")
                    this.events.clear()
                    querySnapshot.forEach { documentSnapshot ->
                        val event=documentSnapshot.toObject(Event::class.java)
                        event.id=documentSnapshot.id
                        this.events.add(event)
                        callback.invoke(this.events)
                    }
                }

                /*Else*/

                else if(e!=null){
                    //TODO handle
                    Log.w(TAG, e.message.toString())
                }
            }
    }

    /**
     * Sets the queries to search results around the given point
     * @param point : the origin point
     */

    fun setQueriesToSearchAroundPoint(point: LatLng){
        this.trailsQuery=TrailFirebaseQueries.getTrailsAroundPoint(point)
        this.eventsQuery=EventFirebaseQueries.getEventsAroundPoint(point)
    }

    /**
     * Sets the queries to search results around the given location
     * @param location : the location
     */

    fun setQueriesToSearchAroundLocation(location:Location){
        if(location.country!=null) {
            this.trailsQuery = TrailFirebaseQueries.getTrailsNearbyLocation(location)!!
            this.eventsQuery = EventFirebaseQueries.getEventsNearbyLocation(location)!!
            this.fragment.updateData()
        }
    }

    /***********************************Trails monitoring****************************************/

    fun seeTrail(trail:Trail?){
        startTrailActivity(TrailActivity.ACTION_TRAIL_SEE, trail)
    }

    /***********************************Events monitoring****************************************/

    fun seeEvent(event:Event?){
        startEventActivity(event)
    }

    /******************************UI monitoring**************************************************/

    private fun initializeToolbar(){
        setSupportActionBar(this.toolbar)
        supportActionBar?.title=""
    }

    private fun initializeSearchTextField(){
        this.searchTextField.setOnClickListener {
            startLocationSearchActivity()
        }
    }

    private fun initializeNavigationView(){
        val toggle = ActionBarDrawerToggle(
            this, this.drawerLayout, this.toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        this.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        this.navigationView.setNavigationItemSelectedListener(this)
        updateNavigationViewUserItems()
    }

    private fun initializeBottomNavigationView(){
        this.bottomNavigationView.setOnNavigationItemSelectedListener(this)
    }

    private fun initializeAddButton(){
        this.addButton.setOnClickListener {
            showAddMenuOptions()
        }
    }

    private fun updateNavigationViewUserItems(){

        /*If the user is null, then shows default info*/

        val user=UserFirebaseHelper.getCurrentUser()
        if(user==null){
            this.navigationHeaderUserImage.setImageResource(R.drawable.ic_person_black)
            this.navigationHeaderUserNameText.setText(R.string.message_user_unknown)
        }

        /*Else shows the user's info*/

        else{
            Glide.with(this)
                .load(user.photoUrl)
                .apply(RequestOptions.circleCropTransform())
                .placeholder(R.drawable.ic_person_black)
                .into(this.navigationHeaderUserImage)
            this.navigationHeaderUserNameText.text=user.displayName
        }
    }

    /******************************Login / Logout************************************************/

    private fun login(){
        //TODO add a progress bar
        val user=UserFirebaseHelper.getCurrentUser()
        if(user==null){
            startLoginActivity()
        }else{
            this.currentHiker=null
            UserFirebaseHelper.signUserOut()
            updateNavigationViewUserItems()
        }
        this.drawerLayout.closeDrawers()
    }

    /******************************Fragments monitoring******************************************/

    /**
     * Shows a fragment
     * @param fragmentId : defines which fragment to display (choice within ID_FRAGMENT_xxx)
     */

    private fun showFragment(fragmentId:Int){
        when(fragmentId){
            ID_FRAGMENT_MAP->
                this.fragment= TrailMapFragment(this.trails)
            ID_FRAGMENT_TRAILS->
                this.fragment=TrailsListFragment(this.currentHiker)
            ID_FRAGMENT_EVENTS->
                this.fragment= EventsListFragment(this.currentHiker)
        }
        supportFragmentManager.beginTransaction()
            .replace(R.id.activity_main_fragment, this.fragment).commitAllowingStateLoss()
    }

    /***********************************Permissions**********************************************/

    private fun requestLocationPermission(){
        if(Build.VERSION.SDK_INT>=23
            &&checkSelfPermission(KEY_BUNDLE_PERMISSION_LOCATION)!= PackageManager.PERMISSION_GRANTED){

            /*If permission not already granted, requests it*/

            if(shouldShowRequestPermissionRationale(KEY_BUNDLE_PERMISSION_LOCATION)){
                //TODO handle
            }else{
                requestPermissions(
                    arrayOf(KEY_BUNDLE_PERMISSION_LOCATION), KEY_REQUEST_PERMISSION_LOCATION)
            }
        }else{

            /*If SDK <23 or permission already granted, directly shows the map fragment*/

            showFragment(ID_FRAGMENT_MAP)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            KEY_REQUEST_PERMISSION_LOCATION->if(grantResults.isNotEmpty()){
                when(grantResults[0]){
                    PackageManager.PERMISSION_GRANTED -> {
                        Log.d(TAG, "Permission '$KEY_BUNDLE_PERMISSION_LOCATION' granted")
                        showFragment(ID_FRAGMENT_MAP)
                    }
                    PackageManager.PERMISSION_DENIED -> {
                        //TODO handle
                        Log.d(TAG, "Permission '$KEY_BUNDLE_PERMISSION_LOCATION' denied")
                    }
                }
            }
        }
    }

    /***********************************Navigation***********************************************/

    /**
     * Starts login on the server
     */

    private fun startLoginActivity(){
        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setTheme(R.style.LoginTheme)
                .setAvailableProviders(listOf(AuthUI.IdpConfig.EmailBuilder().build()))
                .setIsSmartLockEnabled(false, true)
                .build(),
            KEY_REQUEST_LOGIN
        )
    }

    /**
     * Starts profile activity
     * @param profileActionId : defines which action should be performed (choice within ProfileActivity.ACTION_PROFILE_xxx
     */

    private fun startProfileActivity(profileActionId:Int){
        val profileActivityIntent=Intent(this, ProfileActivity::class.java)
        profileActivityIntent.putExtra(ProfileActivity.KEY_BUNDLE_PROFILE_ACTION, profileActionId)
        profileActivityIntent.putExtra(ProfileActivity.KEY_BUNDLE_HIKER, this.currentHiker)
        startActivityForResult(profileActivityIntent, KEY_REQUEST_PROFILE)
    }

    /**
     * Starts profile Edit activity
     * @param profileActionId : defines which action should be performed (choice within ProfileEditActivity.ACTION_PROFILE_xxx
     */

    private fun startProfileEditActivity(profileActionId:Int){
        val profileEditActivityIntent=Intent(this, ProfileEditActivity::class.java)
        profileEditActivityIntent.putExtra(ProfileEditActivity.KEY_BUNDLE_PROFILE_ACTION, profileActionId)
        profileEditActivityIntent.putExtra(ProfileEditActivity.KEY_BUNDLE_HIKER, this.currentHiker)
        startActivityForResult(profileEditActivityIntent, KEY_REQUEST_PROFILE)
    }

    /**
     * Starts the TrailActivity
     * @param trailActionId : defines which action should be performed (choice within TrailActivity.ACTION_TRAIL_xxx)
     * @param trail (optionnal) : the trail to see
     */

    private fun startTrailActivity(trailActionId: Int, trail:Trail?=null){
        val trailActivityIntent= Intent(this, TrailActivity::class.java)
        trailActivityIntent.putExtra(TrailActivity.KEY_BUNDLE_TRAIL_ACTION, trailActionId)
        if(trailActionId==TrailActivity.ACTION_TRAIL_SEE){
            trailActivityIntent.putExtra(TrailActivity.KEY_BUNDLE_TRAIL, trail)
        }
        trailActivityIntent.putExtra(TrailActivity.KEY_BUNDLE_HIKER, this.currentHiker)
        startActivity(trailActivityIntent)
    }

    /**
     * Starts the EventActivity
     */

    private fun startEventActivity(event: Event?){
        val eventActivityIntent=Intent(this, EventActivity::class.java)
        eventActivityIntent.putExtra(EventActivity.KEY_BUNDLE_EVENT, event)
        if(this.currentHiker!=null){
            eventActivityIntent.putExtra(EventActivity.KEY_BUNDLE_HIKER, this.currentHiker)
        }
        startActivity(eventActivityIntent)
    }

    /**
     * Starts the EventEditActivity
     */

    private fun startEventEditActivity(){
        val eventEditActivityIntent= Intent(this, EventEditActivity::class.java)
        eventEditActivityIntent.putExtra(EventEditActivity.KEY_BUNDLE_HIKER, this.currentHiker)
        startActivity(eventEditActivityIntent)
    }

    /**
     * Starts the LocationSearchActivity
     */

    private fun startLocationSearchActivity(){
        val locationSearchActivity=Intent(this, LocationSearchActivity::class.java)
        startActivityForResult(locationSearchActivity, KEY_REQUEST_LOCATION_SEARCH)
    }

    /**Activity result**/

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            KEY_REQUEST_LOGIN -> handleLoginResult(resultCode, data)
            KEY_REQUEST_PROFILE -> handleProfileResult(resultCode, data)
            KEY_REQUEST_LOCATION_SEARCH -> handleLocationSearchResult(resultCode, data)
        }
    }

    /**Handles login result**/

    private fun handleLoginResult(resultCode: Int, data: Intent?){
        val idpResponse=IdpResponse.fromResultIntent(data)
        when{
            resultCode== Activity.RESULT_OK -> {
                Log.d(TAG, "Login successful")
                updateNavigationViewUserItems()
                updateAndSaveCurrentHiker()
            }
            resultCode==Activity.RESULT_CANCELED -> {
                //TODO handle
                Log.d(TAG, "Login canceled")
            }
            idpResponse?.error!=null -> {
                //TODO handle
                Log.w(TAG, "Login failed : ${idpResponse.error?.message}")
            }
            else -> {
                //TODO handle
                Log.w(TAG, "Login failed : unknown error")
            }
        }
    }

    /**Handles profile result**/

    private fun handleProfileResult(resultCode: Int, data: Intent?){
        if(resultCode== Activity.RESULT_OK){
            if(data!=null && data.hasExtra(ProfileActivity.KEY_BUNDLE_HIKER)){
                this.currentHiker=data.getParcelableExtra(ProfileActivity.KEY_BUNDLE_HIKER)
                updateNavigationViewUserItems()
            }
        }
    }

    /**Handles location search result**/

    private fun handleLocationSearchResult(resultCode: Int, data: Intent?){
        if(resultCode== Activity.RESULT_OK){
            if(data!=null && data.hasExtra(LocationSearchActivity.KEY_BUNDLE_LOCATION)){
                val location=data.getParcelableExtra<Location>(LocationSearchActivity.KEY_BUNDLE_LOCATION)
                if(location != null) {
                    setQueriesToSearchAroundLocation(location)
                }
            }
        }
    }
}
