package com.sildian.apps.togetrail.main

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.view.menu.MenuPopupHelper
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.badge.BadgeUtils
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.chat.chatRoom.ChatActivity
import com.sildian.apps.togetrail.common.baseControllers.BaseActivity
import com.sildian.apps.togetrail.common.baseControllers.BaseFragment
import com.sildian.apps.togetrail.common.baseViewModels.ViewModelFactory
import com.sildian.apps.togetrail.common.utils.NumberUtilities
import com.sildian.apps.togetrail.common.utils.cloudHelpers.AuthFirebaseHelper
import com.sildian.apps.togetrail.common.utils.permissionsHelpers.PermissionsCallback
import com.sildian.apps.togetrail.common.utils.uiHelpers.DialogHelper
import com.sildian.apps.togetrail.common.utils.uiHelpers.SnackbarHelper
import com.sildian.apps.togetrail.databinding.ActivityMainBinding
import com.sildian.apps.togetrail.event.detail.EventActivity
import com.sildian.apps.togetrail.event.edit.EventEditActivity
import com.sildian.apps.togetrail.event.list.EventsListFragment
import com.sildian.apps.togetrail.event.model.core.Event
import com.sildian.apps.togetrail.event.model.support.EventFirebaseQueries
import com.sildian.apps.togetrail.hiker.model.support.*
import com.sildian.apps.togetrail.hiker.profileEdit.ProfileEditActivity
import com.sildian.apps.togetrail.hiker.profile.ProfileActivity
import com.sildian.apps.togetrail.location.model.core.Location
import com.sildian.apps.togetrail.location.search.LocationSearchActivity
import com.sildian.apps.togetrail.trail.list.TrailsListFragment
import com.sildian.apps.togetrail.trail.map.TrailActivity
import com.sildian.apps.togetrail.trail.map.TrailMapFragment
import com.sildian.apps.togetrail.trail.model.core.Trail
import com.sildian.apps.togetrail.trail.model.support.HikerTrailsViewModel
import com.sildian.apps.togetrail.trail.model.support.TrailFirebaseQueries
import kotlinx.android.synthetic.main.navigation_view_header.view.*
import net.danlew.android.joda.JodaTimeAndroid

/*************************************************************************************************
 * Lets the user navigate between the main screens
 ************************************************************************************************/

class MainActivity :
    BaseActivity<ActivityMainBinding>(),
    PermissionsCallback,
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

    private lateinit var hikerViewModel: HikerViewModel
    private lateinit var hikerChatViewModel: HikerChatViewModel
    private lateinit var hikerTrailsViewModel: HikerTrailsViewModel
    private var isNbUnreadMessagesBadgeShown = false

    /*************************************Queries************************************************/

    var trailsQuery=TrailFirebaseQueries.getLastTrails();private set
    var eventsQuery=EventFirebaseQueries.getNextEvents();private set

    /**********************************UI component**********************************************/

    private var fragment:BaseFragment<out ViewDataBinding>?=null
    private val nbUnreadMessagesBadge by lazy { BadgeDrawable.create(this) }
    private val navigationViewHeader by lazy {
        layoutInflater.inflate(R.layout.navigation_view_header, this.binding.activityMainNavigationView)
    }
    private val navigationHeaderUserImage by lazy {
        navigationViewHeader.navigation_view_header_user_image
    }
    private val navigationHeaderUserNameText by lazy {
        navigationViewHeader.navigation_view_header_text_user_name
    }

    /************************************Life cycle**********************************************/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        JodaTimeAndroid.init(this)
        Places.initialize(applicationContext, resources.getString(R.string.google_maps_key))
        requestLocationPermission()
    }

    override fun onDestroy() {
        Places.deinitialize()
        super.onDestroy()
    }

    /************************************Navigation control**************************************/

    override fun onBackPressed() {
        if (this.binding.activityMainDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.binding.activityMainDrawerLayout.closeDrawers()
        } else {
            super.onBackPressed()
        }
    }

    /*******************************Menu monitoring**********************************************/

    /**Generates the menu within the toolbar**/

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_chat, menu)
        return true
    }

    /**Click on menu item from toolbar**/

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.groupId) {
            R.id.menu_chat -> {
                if (item.itemId == R.id.menu_chat_chat) {
                    if (CurrentHikerInfo.currentHiker != null) {
                        startChatActivity()
                    }
                    else {
                        showAccountNecessaryMessage()
                    }
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

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
                this.binding.activityMainDrawerLayout.closeDrawer(GravityCompat.START)
                when (item.itemId) {
                    R.id.menu_user_profile -> {
                        if(AuthFirebaseHelper.getCurrentUser()!=null){
                            startProfileActivity()
                        }else{
                            showAccountNecessaryMessage()
                        }
                    }
                    R.id.menu_user_settings ->
                        if(AuthFirebaseHelper.getCurrentUser()!=null){
                            startProfileEditActivity()
                        }else{
                            showAccountNecessaryMessage()
                        }
                    R.id.menu_user_trails ->
                        this.binding.activityMainBottomNavigationView.selectedItemId = R.id.menu_main_trails
                    R.id.menu_user_events ->
                        this.binding.activityMainBottomNavigationView.selectedItemId = R.id.menu_main_events
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

        when (item?.groupId) {
            R.id.menu_add -> {
                when (item.itemId) {
                    R.id.menu_add_event -> {
                        if (AuthFirebaseHelper.getCurrentUser() != null) {
                            startEventEditActivity()
                        } else {
                            showAccountNecessaryMessage()
                        }
                    }
                }
            }
            R.id.menu_add_new_trail -> {
                if (AuthFirebaseHelper.getCurrentUser() != null) {
                    when (item.itemId) {
                        R.id.menu_add_trail_load_gpx ->
                            startTrailActivity(TrailActivity.ACTION_TRAIL_CREATE_FROM_GPX)
                        R.id.menu_add_trail_draw ->
                            startTrailActivity(TrailActivity.ACTION_TRAIL_DRAW)
                        R.id.menu_add_trail_record ->
                            startTrailActivity(TrailActivity.ACTION_TRAIL_RECORD)
                    }
                } else {
                    showAccountNecessaryMessage()
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
            override fun onMenuModeChange(menu: MenuBuilder) {
                //Nothing here
            }
            override fun onMenuItemSelected(menu: MenuBuilder, item: MenuItem): Boolean {
                onPopupMenuItemClick(item)
                return true
            }
        })

        /*Creates the popup and shows it*/

        val menuPopupHelper=MenuPopupHelper(this, menuBuilder, this.binding.activityMainButtonAdd)
        menuPopupHelper.setForceShowIcon(true)
        menuPopupHelper.show()
    }

    /****************************Data monitoring**************************************************/

    override fun loadData() {
        initializeData()
        observeHiker()
        observeHikerChats()
        observeHikerLikedTrails()
        observeHikerMarkedTrails()
        observeRequestFailure()
        loginCurrentUser()
    }

    private fun initializeData() {
        this.hikerViewModel = ViewModelProviders
            .of(this, ViewModelFactory)
            .get(HikerViewModel::class.java)
        this.hikerChatViewModel = ViewModelProviders
            .of(this, ViewModelFactory)
            .get(HikerChatViewModel::class.java)
        this.hikerTrailsViewModel = ViewModelProviders
            .of(this, ViewModelFactory)
            .get(HikerTrailsViewModel::class.java)
    }

    private fun observeHiker() {
        this.hikerViewModel.hiker.observe(this) { hiker ->
            this.binding.activityMainProgressbar.visibility = View.GONE
            CurrentHikerInfo.currentHiker = hikerViewModel.hiker.value
            when {
                hiker == null -> {
                    this.hikerViewModel.clearQueryRegistration()
                    this.hikerChatViewModel.clearQueryRegistration()
                    this.hikerTrailsViewModel.clearQueryRegistration()
                    updateNavigationViewUserItems()
                }
                !this.hikerViewModel.isQueryRegistrationBusy() -> {
                    loadHiker()
                    loadHikerChats()
                    loadHikerLikedTrails()
                    loadHikerMarkedTrails()
                }
                else ->
                    updateNavigationViewUserItems()
            }
        }
    }

    private fun observeHikerChats() {
        this.hikerChatViewModel.chats.observe(this) { chats ->
            var nbUnreadMessages = 0
            chats.forEach { chat ->
                nbUnreadMessages+= chat.nbUnreadMessages
            }
            updateNbUnreadMessagesBadge(nbUnreadMessages)
        }
    }

    private fun observeHikerLikedTrails() {
        this.hikerTrailsViewModel.likedTrails.observe(this) { trails ->
            if (trails != null) {
                CurrentHikerInfo.currentHikerLikedTrail.clear()
                CurrentHikerInfo.currentHikerLikedTrail.addAll(trails)
            }
        }
    }

    private fun observeHikerMarkedTrails() {
        this.hikerTrailsViewModel.markedTrails.observe(this) { trails ->
            if (trails != null) {
                CurrentHikerInfo.currentHikerMarkedTrail.clear()
                CurrentHikerInfo.currentHikerMarkedTrail.addAll(trails)
            }
        }
    }

    private fun observeRequestFailure() {
        this.hikerViewModel.requestFailure.observe(this) { e ->
            if (e != null) {
                onQueryError(e)
            }
        }
        this.hikerChatViewModel.requestFailure.observe(this) { e ->
            if (e != null) {
                onQueryError(e)
            }
        }
        this.hikerTrailsViewModel.requestFailure.observe(this) { e ->
            if (e != null) {
                onQueryError(e)
            }
        }
    }

    private fun loginCurrentUser() {
        if (AuthFirebaseHelper.getCurrentUser() != null) {
            this.binding.activityMainProgressbar.visibility = View.VISIBLE
            this.hikerViewModel.loginUser()
        }
    }

    private fun loadHiker() {
        this.hikerViewModel.hiker.value?.id?.let { hikerId ->
            this.hikerViewModel.loadHikerFromDatabaseRealTime(hikerId)
        }
    }

    private fun loadHikerChats() {
        this.hikerViewModel.hiker.value?.id?.let { hikerId ->
            this.hikerChatViewModel.loadChatsFromDatabaseRealTime(hikerId)
        }
    }

    private fun loadHikerLikedTrails() {
        this.hikerViewModel.hiker.value?.id?.let { hikerId ->
            this.hikerTrailsViewModel.loadLikedTrailsFromDatabaseRealTime(hikerId)
        }
    }

    private fun loadHikerMarkedTrails() {
        this.hikerViewModel.hiker.value?.id?.let { hikerId ->
            this.hikerTrailsViewModel.loadMarkedTrailsFromDatabaseRealTime(hikerId)
        }
    }

    fun showQueryResultEmptyMessage(){
        SnackbarHelper
            .createSimpleSnackbar(this.binding.activityMainViewMessage, this.binding.activityMainButtonAdd, R.string.message_query_result_empty)
            .show()
    }

    fun showAccountNecessaryMessage() {
        SnackbarHelper
            .createSnackbarWithAction(this.binding.activityMainViewMessage, this.binding.activityMainButtonAdd, R.string.message_account_necessary,
                R.string.button_common_sign_in, this::startLogin)
            .show()
    }

    fun setQueriesToSearchAroundPoint(point: LatLng){
        val latToDisplay = NumberUtilities.displayNumber(point.latitude, 4)
        val lngToDisplay = NumberUtilities.displayNumber(point.longitude, 4)
        val pointToDisplay = "$latToDisplay ; $lngToDisplay"
        this.binding.activityMainTextFieldResearch.setText(pointToDisplay)
        this.trailsQuery = TrailFirebaseQueries.getTrailsAroundPoint(point)
        this.eventsQuery = EventFirebaseQueries.getEventsAroundPoint(point)
        when (this.fragment) {
            is TrailMapFragment -> this.fragment?.updateData(null)
            is TrailsListFragment -> this.fragment?.updateData(this.trailsQuery)
            is EventsListFragment -> this.fragment?.updateData(this.eventsQuery)
        }
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun setQueriesToSearchAroundLocation(location:Location){
        if(location.country!=null) {
            this.binding.activityMainTextFieldResearch.setText(location.toString())
            this.trailsQuery = TrailFirebaseQueries.getTrailsNearbyLocation(location)!!
            this.eventsQuery = EventFirebaseQueries.getEventsNearbyLocation(location)!!
            when (this.fragment) {
                is TrailMapFragment -> this.fragment?.updateData(null)
                is TrailsListFragment -> this.fragment?.updateData(this.trailsQuery)
                is EventsListFragment -> this.fragment?.updateData(this.eventsQuery)
            }
        }
    }

    fun resetQueries(){
        this.binding.activityMainTextFieldResearch.text = null
        this.trailsQuery = TrailFirebaseQueries.getLastTrails()
        this.eventsQuery = EventFirebaseQueries.getNextEvents()
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
        setSupportActionBar(this.binding.activityMainToolbar)
        supportActionBar?.title=""
    }

    private fun initializeSearchTextField(){
        this.binding.activityMainTextFieldResearch.setOnClickListener {
            startLocationSearchActivity()
        }
    }

    private fun initializeClearResearchButton(){
        this.binding.activityMainButtonResearchClear.setOnClickListener {
            resetQueries()
        }
    }

    private fun initializeNavigationView(){
        val toggle = ActionBarDrawerToggle(
            this, this.binding.activityMainDrawerLayout, this.binding.activityMainToolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        this.binding.activityMainDrawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        this.binding.activityMainNavigationView.setNavigationItemSelectedListener(this)
        updateNavigationViewUserItems()
    }

    private fun initializeBottomNavigationView(){
        this.binding.activityMainBottomNavigationView.setOnNavigationItemSelectedListener(this)
    }

    private fun initializeAddButton(){
        this.binding.activityMainButtonAdd.setOnClickListener {
            showAddMenuOptions()
        }
    }

    private fun updateNavigationViewUserItems(){

        /*If the user is null, then shows default info*/

        if(this.hikerViewModel.hiker.value == null){
            this.navigationHeaderUserImage.setImageResource(R.drawable.ic_person_white)
            this.navigationHeaderUserNameText.setText(R.string.message_user_unknown)
        }

        /*Else shows the user's info*/

        else{
            Glide.with(this)
                .load(this.hikerViewModel.hiker.value?.photoUrl)
                .apply(RequestOptions.circleCropTransform())
                .placeholder(R.drawable.ic_person_white)
                .into(this.navigationHeaderUserImage)
            this.navigationHeaderUserNameText.text=this.hikerViewModel.hiker.value?.name
        }
    }

    @Suppress("UnsafeExperimentalUsageError")
    private fun updateNbUnreadMessagesBadge(nbUnreadMessages: Int) {
        if (nbUnreadMessages > 0) {
            nbUnreadMessagesBadge.number = nbUnreadMessages
            nbUnreadMessagesBadge.backgroundColor = ContextCompat.getColor(this, android.R.color.holo_red_dark)
            if (!isNbUnreadMessagesBadgeShown) {
                nbUnreadMessagesBadge.maxCharacterCount = 2
                BadgeUtils.attachBadgeDrawable(nbUnreadMessagesBadge, this.binding.activityMainToolbar, R.id.menu_chat_chat)
                isNbUnreadMessagesBadgeShown = true
            }
        }
        else {
            nbUnreadMessagesBadge.clearNumber()
            nbUnreadMessagesBadge.backgroundColor = Color.TRANSPARENT
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
    }

    /******************************Fragments monitoring******************************************/

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

    private fun requestLocationPermission(){
        requestPermissions(
            KEY_REQUEST_PERMISSION_LOCATION,
            arrayOf(KEY_BUNDLE_PERMISSION_LOCATION),
            this,
            R.string.message_permission_requested_message_location
        )
    }

    override fun onPermissionsGranted(permissionsRequestCode: Int) {
        showFragment(ID_FRAGMENT_MAP)
    }

    override fun onPermissionsDenied(permissionsRequestCode: Int) {
        DialogHelper.createInfoDialog(
            this,
            R.string.message_permission_denied_title,
            R.string.message_permission_denied_message_location
        ).show()
        showFragment(ID_FRAGMENT_MAP)
    }

    /***********************************Navigation***********************************************/

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

    private fun startProfileActivity(){
        val profileActivityIntent=Intent(this, ProfileActivity::class.java)
        profileActivityIntent.putExtra(ProfileActivity.KEY_BUNDLE_HIKER_ID, this.hikerViewModel.hiker.value?.id)
        startActivity(profileActivityIntent)
    }

    private fun startProfileEditActivity(){
        val profileEditActivityIntent=Intent(this, ProfileEditActivity::class.java)
        profileEditActivityIntent.putExtra(ProfileEditActivity.KEY_BUNDLE_PROFILE_ACTION, ProfileEditActivity.ACTION_PROFILE_EDIT_SETTINGS)
        profileEditActivityIntent.putExtra(ProfileEditActivity.KEY_BUNDLE_HIKER_ID, this.hikerViewModel.hiker.value?.id)
        startActivity(profileEditActivityIntent)
    }

    private fun startTrailActivity(trailActionId: Int, trail:Trail?=null){
        val trailActivityIntent= Intent(this, TrailActivity::class.java)
        trailActivityIntent.putExtra(TrailActivity.KEY_BUNDLE_TRAIL_ACTION, trailActionId)
        if(trailActionId==TrailActivity.ACTION_TRAIL_SEE){
            trailActivityIntent.putExtra(TrailActivity.KEY_BUNDLE_TRAIL_ID, trail?.id)
        }
        startActivity(trailActivityIntent)
    }

    private fun startEventActivity(event: Event?){
        val eventActivityIntent=Intent(this, EventActivity::class.java)
        eventActivityIntent.putExtra(EventActivity.KEY_BUNDLE_EVENT_ID, event?.id)
        startActivity(eventActivityIntent)
    }

    private fun startEventEditActivity(){
        val eventEditActivityIntent= Intent(this, EventEditActivity::class.java)
        startActivity(eventEditActivityIntent)
    }

    private fun startLocationSearchActivity(){
        val locationSearchActivity=Intent(this, LocationSearchActivity::class.java)
        startActivityForResult(locationSearchActivity, KEY_REQUEST_LOCATION_SEARCH)
    }

    private fun startChatActivity() {
        val chatActivity = Intent(this, ChatActivity::class.java)
        startActivity(chatActivity)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            KEY_REQUEST_LOGIN -> handleLoginResult(resultCode, data)
            KEY_REQUEST_LOCATION_SEARCH -> handleLocationSearchResult(resultCode, data)
        }
    }

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
                Log.e(TAG, "Login failed : ${idpResponse.error?.message}")
                DialogHelper.createInfoDialog(
                    this,
                    R.string.message_log_failure_title,
                    R.string.message_log_failure_message
                ).show()
            }
            else -> {
                Log.e(TAG, "Login failed : unknown error")
                DialogHelper.createInfoDialog(
                    this,
                    R.string.message_log_failure_title,
                    R.string.message_log_failure_message
                ).show()
            }
        }
    }

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
