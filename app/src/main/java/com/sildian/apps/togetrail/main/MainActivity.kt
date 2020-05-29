package com.sildian.apps.togetrail.main

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.view.menu.MenuPopupHelper
import androidx.core.view.GravityCompat
import androidx.databinding.Observable
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.common.baseControllers.BaseDataFlowActivity
import com.sildian.apps.togetrail.common.baseControllers.BaseDataFlowFragment
import com.sildian.apps.togetrail.common.baseViewModels.ViewModelFactory
import com.sildian.apps.togetrail.common.utils.NumberUtilities
import com.sildian.apps.togetrail.common.utils.cloudHelpers.AuthFirebaseHelper
import com.sildian.apps.togetrail.common.utils.uiHelpers.DialogHelper
import com.sildian.apps.togetrail.event.detail.EventActivity
import com.sildian.apps.togetrail.event.edit.EventEditActivity
import com.sildian.apps.togetrail.event.list.EventsListFragment
import com.sildian.apps.togetrail.event.model.core.Event
import com.sildian.apps.togetrail.event.model.support.EventFirebaseQueries
import com.sildian.apps.togetrail.hiker.profileEdit.ProfileEditActivity
import com.sildian.apps.togetrail.hiker.model.support.HikerViewModel
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
    BaseDataFlowActivity(),
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
        private const val KEY_REQUEST_LOCATION_SEARCH=1002

        /**Request keys for permissions**/
        private const val KEY_REQUEST_PERMISSION_LOCATION=2001

        /**Bundle keys for permissions**/
        private const val KEY_BUNDLE_PERMISSION_LOCATION=Manifest.permission.ACCESS_FINE_LOCATION
    }

    /****************************************Data************************************************/

    private lateinit var hikerViewModel:HikerViewModel      //The current user connected to the app

    /*************************************Queries************************************************/

    var trailsQuery=TrailFirebaseQueries.getLastTrails();private set    //The current query used to fetch trails
    var eventsQuery=EventFirebaseQueries.getNextEvents();private set    //The current query used to fetch events

    /**********************************UI component**********************************************/

    private var fragment:BaseDataFlowFragment?=null
    private val toolbar by lazy {activity_main_toolbar}
    private val searchTextField by lazy {activity_main_text_field_research}
    private val clearResearchButton by lazy {activity_main_button_research_clear}
    private val progressbar by lazy {activity_main_progressbar}
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
    private val messageView by lazy {activity_main_view_message}

    /************************************Life cycle**********************************************/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        JodaTimeAndroid.init(this)
        Places.initialize(applicationContext, resources.getString(R.string.google_maps_key))
        requestLocationPermission()
    }

    /************************************Navigation control**************************************/

    override fun onBackPressed() {
        if(this.drawerLayout.isDrawerOpen(GravityCompat.START)){
            this.drawerLayout.closeDrawers()
        }else {
            super.onBackPressed()
        }
    }

    /*******************************Menu monitoring**********************************************/

    /**Click on menu item from...**/

    @Suppress("UNUSED_ANONYMOUS_PARAMETER")
    override fun onNavigationItemSelected(item: MenuItem): Boolean {

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
                when (item.itemId) {
                    R.id.menu_user_profile -> {
                        if(AuthFirebaseHelper.getCurrentUser()!=null){
                            startProfileActivity()
                        }else{
                            DialogHelper.createYesNoDialog(
                                this,
                                R.string.message_user_not_connected_title,
                                R.string.message_user_not_connected_message,
                                DialogInterface.OnClickListener { dialog, which ->
                                    if(which==DialogInterface.BUTTON_POSITIVE){
                                        startLogin()
                                    }
                                }
                            ).show()
                        }
                    }
                    R.id.menu_user_settings ->
                        if(AuthFirebaseHelper.getCurrentUser()!=null){
                            startProfileEditActivity()
                        }else{
                            DialogHelper.createYesNoDialog(
                                this,
                                R.string.message_user_not_connected_title,
                                R.string.message_user_not_connected_message,
                                DialogInterface.OnClickListener { dialog, which ->
                                    if(which==DialogInterface.BUTTON_POSITIVE){
                                        startLogin()
                                    }
                                }
                            ).show()
                        }
                    R.id.menu_user_trails -> {
                        this.drawerLayout.closeDrawer(GravityCompat.START)
                        this.bottomNavigationView.selectedItemId = R.id.menu_main_trails
                    }
                    R.id.menu_user_events -> {
                        this.drawerLayout.closeDrawer(GravityCompat.START)
                        this.bottomNavigationView.selectedItemId = R.id.menu_main_events
                    }
                    R.id.menu_user_login ->
                        startLogin()
                }
                true
            }
            else -> false
        }
    }

    /**Click on menu item from PopupMenu**/

    @Suppress("UNUSED_ANONYMOUS_PARAMETER")
    private fun onPopupMenuItemClick(item: MenuItem?): Boolean {

        /*Allows the user to use these features only if he is connected*/

        if(AuthFirebaseHelper.getCurrentUser()!=null) {

            when (item?.groupId) {
                R.id.menu_add -> {
                    when (item.itemId) {
                        R.id.menu_add_event ->
                            startEventEditActivity()
                    }
                }
                R.id.menu_add_new_trail -> {
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

            /*Else, asks if he wants to log in*/

        }else{
            DialogHelper.createYesNoDialog(
                this,
                R.string.message_user_not_connected_title,
                R.string.message_user_not_connected_message,
                DialogInterface.OnClickListener { dialog, which ->
                    if (which==DialogInterface.BUTTON_POSITIVE){
                        startLogin()
                    }
                }
            ).show()
            return false
        }
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
        menuPopupHelper.show()
    }

    /****************************Data monitoring**************************************************/

    /**Loads data**/

    override fun loadData() {
        this.hikerViewModel=ViewModelProviders
            .of(this, ViewModelFactory)
            .get(HikerViewModel::class.java)
        loginCurrentUser()
    }

    /**Logs the current user in the database**/

    private fun loginCurrentUser(){
        if(AuthFirebaseHelper.getCurrentUser()!=null) {
            this.progressbar.visibility = View.VISIBLE
            this.hikerViewModel.loginUser(this::handleLoginCurrentUser, this::handleQueryError)
        }
    }

    /**Handles current user login**/

    private fun handleLoginCurrentUser(){
        this.progressbar.visibility= View.GONE
        this.hikerViewModel.hiker?.id?.let { hikerId ->
            this.hikerViewModel.loadHikerFromDatabaseRealTime(hikerId)
        }
    }

    //TODO Move this?

    fun showEmptyMessage(){
        Snackbar.make(this.messageView, R.string.message_query_result_empty, Snackbar.LENGTH_LONG)
            .setAnchorView(this.addButton)
            .show()
    }

    /**
     * Sets the queries to search results around the given point
     * @param point : the origin point
     */

    fun setQueriesToSearchAroundPoint(point: LatLng){
        val latToDisplay=NumberUtilities.displayNumber(point.latitude, 4)
        val lngToDisplay=NumberUtilities.displayNumber(point.longitude, 4)
        val pointToDisplay="$latToDisplay ; $lngToDisplay"
        this.searchTextField.setText(pointToDisplay)
        this.trailsQueryRegistration?.remove()
        this.eventsQueryRegistration?.remove()
        this.trailsQuery=TrailFirebaseQueries.getTrailsAroundPoint(point)
        this.eventsQuery=EventFirebaseQueries.getEventsAroundPoint(point)
        when (this.fragment) {
            is TrailMapFragment -> this.fragment?.updateData(null)
            is TrailsListFragment -> this.fragment?.updateData(this.trailsQuery)
            is EventsListFragment -> this.fragment?.updateData(this.eventsQuery)
        }
    }

    /**
     * Sets the queries to search results around the given location
     * @param location : the location
     */

    @Suppress("MemberVisibilityCanBePrivate")
    fun setQueriesToSearchAroundLocation(location:Location){
        if(location.country!=null) {
            this.searchTextField.setText(location.toString())
            this.trailsQueryRegistration?.remove()
            this.eventsQueryRegistration?.remove()
            this.trailsQuery = TrailFirebaseQueries.getTrailsNearbyLocation(location)!!
            this.eventsQuery = EventFirebaseQueries.getEventsNearbyLocation(location)!!
            when (this.fragment) {
                is TrailMapFragment -> this.fragment?.updateData(null)
                is TrailsListFragment -> this.fragment?.updateData(this.trailsQuery)
                is EventsListFragment -> this.fragment?.updateData(this.eventsQuery)
            }
        }
    }

    /**Clears the current research and resets the queries by default**/

    fun resetQueries(){
        this.searchTextField.text=null
        this.trailsQueryRegistration?.remove()
        this.eventsQueryRegistration?.remove()
        this.trailsQuery=TrailFirebaseQueries.getLastTrails()
        this.eventsQuery=EventFirebaseQueries.getNextEvents()
        when (this.fragment) {
            is TrailMapFragment -> this.fragment?.updateData(null)
            is TrailsListFragment -> this.fragment?.updateData(this.trailsQuery)
            is EventsListFragment -> this.fragment?.updateData(this.eventsQuery)
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

    override fun getLayoutId(): Int = R.layout.activity_main

    override fun initializeUI() {
        initializeToolbar()
        initializeSearchTextField()
        initializeClearResearchButton()
        initializeNavigationView()
        initializeBottomNavigationView()
        initializeAddButton()
    }

    private fun initializeToolbar(){
        setSupportActionBar(this.toolbar)
        supportActionBar?.title=""
    }

    private fun initializeSearchTextField(){
        this.searchTextField.setOnClickListener {
            startLocationSearchActivity()
        }
    }

    private fun initializeClearResearchButton(){
        this.clearResearchButton.setOnClickListener {
            resetQueries()
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
        this.hikerViewModel.addOnPropertyChangedCallback(object:Observable.OnPropertyChangedCallback(){
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                updateNavigationViewUserItems()
            }
        })
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

        if(this.hikerViewModel.hiker==null){
            this.navigationHeaderUserImage.setImageResource(R.drawable.ic_person_white)
            this.navigationHeaderUserNameText.setText(R.string.message_user_unknown)
        }

        /*Else shows the user's info*/

        else{
            Glide.with(this)
                .load(this.hikerViewModel.hiker?.photoUrl)
                .apply(RequestOptions.circleCropTransform())
                .placeholder(R.drawable.ic_person_white)
                .into(this.navigationHeaderUserImage)
            this.navigationHeaderUserNameText.text=this.hikerViewModel.hiker?.name
        }
    }

    /******************************Login / Logout************************************************/

    private fun startLogin(){
        val user=AuthFirebaseHelper.getCurrentUser()
        if(user==null){
            startLoginActivity()
        }else{
            this.hikerViewModel.logoutUser()
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
                this.fragment= TrailMapFragment()
            ID_FRAGMENT_TRAILS->
                this.fragment=TrailsListFragment(this.hikerViewModel)
            ID_FRAGMENT_EVENTS->
                this.fragment= EventsListFragment(this.hikerViewModel)
        }
        this.fragment?.let { fragment ->
            supportFragmentManager.beginTransaction()
                .replace(R.id.activity_main_fragment, fragment).commitAllowingStateLoss()
        }
    }

    /***********************************Permissions**********************************************/

    /**Requests location permission**/

    private fun requestLocationPermission(){
        if(Build.VERSION.SDK_INT>=23
            &&checkSelfPermission(KEY_BUNDLE_PERMISSION_LOCATION)!= PackageManager.PERMISSION_GRANTED){

            /*If permission not already granted, requests it*/

            if(shouldShowRequestPermissionRationale(KEY_BUNDLE_PERMISSION_LOCATION)){
                DialogHelper.createInfoDialog(
                    this,
                    R.string.message_permission_requested_title,
                    R.string.message_permission_requested_message_location
                ).show()
            }else{
                requestPermissions(
                    arrayOf(KEY_BUNDLE_PERMISSION_LOCATION), KEY_REQUEST_PERMISSION_LOCATION)
            }
        }else{

            /*If SDK <23 or permission already granted, directly shows the map fragment*/

            showFragment(ID_FRAGMENT_MAP)
        }
    }

    /**Handles location permission result**/

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            KEY_REQUEST_PERMISSION_LOCATION->if(grantResults.isNotEmpty()){
                when(grantResults[0]){
                    PackageManager.PERMISSION_GRANTED -> {
                        Log.d(TAG, "Permission '$KEY_BUNDLE_PERMISSION_LOCATION' granted")
                        showFragment(ID_FRAGMENT_MAP)
                    }
                    PackageManager.PERMISSION_DENIED -> {
                        DialogHelper.createInfoDialog(
                            this,
                            R.string.message_permission_requested_title,
                            R.string.message_permission_requested_message_location
                        ).show()
                        Log.d(TAG, "Permission '$KEY_BUNDLE_PERMISSION_LOCATION' denied")
                    }
                }
            }
        }
    }

    /***********************************Navigation***********************************************/

    /**Starts login on the server**/

    private fun startLoginActivity(){
        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setTheme(R.style.LoginTheme)
                .setLogo(R.drawable.ic_togetrail_logo_with_name)
                .setAvailableProviders(listOf(
                    AuthUI.IdpConfig.EmailBuilder().build(),
                    AuthUI.IdpConfig.GoogleBuilder().build()
                ))
                .setIsSmartLockEnabled(false, true)
                .build(),
            KEY_REQUEST_LOGIN
        )
    }

    /**Starts profile activity**/

    private fun startProfileActivity(){
        val profileActivityIntent=Intent(this, ProfileActivity::class.java)
        profileActivityIntent.putExtra(ProfileActivity.KEY_BUNDLE_HIKER_ID, this.hikerViewModel.hiker?.id)
        startActivity(profileActivityIntent)
    }

    /**Starts profile Edit activity**/

    private fun startProfileEditActivity(){
        val profileEditActivityIntent=Intent(this, ProfileEditActivity::class.java)
        profileEditActivityIntent.putExtra(ProfileEditActivity.KEY_BUNDLE_PROFILE_ACTION, ProfileEditActivity.ACTION_PROFILE_EDIT_SETTINGS)
        profileEditActivityIntent.putExtra(ProfileEditActivity.KEY_BUNDLE_HIKER, this.hikerViewModel.hiker)
        startActivity(profileEditActivityIntent)
    }

    /**
     * Starts the TrailActivity
     * @param trailActionId : defines which action should be performed (choice within TrailActivity.ACTION_TRAIL_xxx)
     * @param trail (optional) : the trail to show
     */

    private fun startTrailActivity(trailActionId: Int, trail:Trail?=null){
        val trailActivityIntent= Intent(this, TrailActivity::class.java)
        trailActivityIntent.putExtra(TrailActivity.KEY_BUNDLE_TRAIL_ACTION, trailActionId)
        if(trailActionId==TrailActivity.ACTION_TRAIL_SEE){
            trailActivityIntent.putExtra(TrailActivity.KEY_BUNDLE_TRAIL, trail)
        }
        startActivity(trailActivityIntent)
    }

    /**
     * Starts the EventActivity
     * @param event : the event to show
     */

    private fun startEventActivity(event: Event?){
        val eventActivityIntent=Intent(this, EventActivity::class.java)
        eventActivityIntent.putExtra(EventActivity.KEY_BUNDLE_EVENT_ID, event?.id)
        startActivity(eventActivityIntent)
    }

    /**Starts the EventEditActivity**/

    private fun startEventEditActivity(){
        val eventEditActivityIntent= Intent(this, EventEditActivity::class.java)
        startActivity(eventEditActivityIntent)
    }

    /**Starts the LocationSearchActivity**/

    private fun startLocationSearchActivity(){
        val locationSearchActivity=Intent(this, LocationSearchActivity::class.java)
        startActivityForResult(locationSearchActivity, KEY_REQUEST_LOCATION_SEARCH)
    }

    /**Activity result**/

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            KEY_REQUEST_LOGIN -> handleLoginResult(resultCode, data)
            KEY_REQUEST_LOCATION_SEARCH -> handleLocationSearchResult(resultCode, data)
        }
    }

    /**Handles login result**/

    private fun handleLoginResult(resultCode: Int, data: Intent?){
        val idpResponse=IdpResponse.fromResultIntent(data)
        when{
            resultCode== Activity.RESULT_OK -> {
                Log.d(TAG, "Login successful")
                loginCurrentUser()
            }
            resultCode==Activity.RESULT_CANCELED -> {
                Log.d(TAG, "Login canceled")
            }
            idpResponse?.error!=null -> {
                Log.w(TAG, "Login failed : ${idpResponse.error?.message}")
                DialogHelper.createInfoDialog(
                    this,
                    R.string.message_log_failure_title,
                    R.string.message_log_failure_message
                ).show()
            }
            else -> {
                Log.w(TAG, "Login failed : unknown error")
                DialogHelper.createInfoDialog(
                    this,
                    R.string.message_log_failure_title,
                    R.string.message_log_failure_message
                ).show()
            }
        }
    }

    /**Handles location search result**/

    private fun handleLocationSearchResult(resultCode: Int, data: Intent?){
        if(resultCode== Activity.RESULT_OK){
            if(data!=null && data.hasExtra(LocationSearchActivity.KEY_BUNDLE_LOCATION)){
                val location=data.getParcelableExtra<Location>(LocationSearchActivity.KEY_BUNDLE_LOCATION)
                location?.let { loc ->
                    setQueriesToSearchAroundLocation(loc)
                }
            }
        }
    }
}
