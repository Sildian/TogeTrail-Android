package com.sildian.apps.togetrail.main

import android.Manifest
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
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.sildian.apps.togetrail.R
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
        private const val TAG_ACTIVITY="TAG_ACTIVITY"
        private const val TAG_MENU="TAG_MENU"
        private const val TAG_LOGIN="TAG_LOGIN"
        private const val TAG_STORAGE="TAG_STORAGE"
        private const val TAG_PERMISSION="TAG_PERMISSION"

        /**Fragments Ids***/
        private const val ID_FRAGMENT_MAP=1
        private const val ID_FRAGMENT_TRAILS=2
        private const val ID_FRAGMENT_EVENTS=3

        /**Request keys for activities**/
        private const val KEY_REQUEST_LOGIN=1001

        /**Request keys for permissions**/
        private const val KEY_REQUEST_PERMISSION_LOCATION=2001

        /**Bundle keys for intents**/
        const val KEY_BUNDLE_TRAIL_ACTION="KEY_BUNDLE_TRAIL_ACTION"
        const val KEY_BUNDLE_TRAIL="KEY_BUNDLE_TRAIL"

        /**Bundle keys for permissions**/
        private const val KEY_BUNDLE_PERMISSION_LOCATION=Manifest.permission.ACCESS_FINE_LOCATION
    }

    /****************************************Data************************************************/

    private val trails= arrayListOf<Trail>()            //The list of trails to display

    /**********************************UI component**********************************************/

    private lateinit var fragment:Fragment
    private val toolbar by lazy {activity_main_toolbar}
    private val drawerLayout by lazy {activity_main_drawer_layout}
    private val navigationView by lazy {activity_main_navigation_view}
    private val navigationViewHeader by lazy {
        layoutInflater.inflate(R.layout.navigation_view_header, this.navigationView)
    }
    private val navigationHeaderUserImage by lazy {
        navigationViewHeader.navigation_view_header_user_image
    }
    private val navigationHeaderUserNameText by lazy {
        navigationViewHeader.navigation_view_header_user_name
    }
    private val bottomNavigationView by lazy {activity_main_bottom_navigation_view}
    private val addButton by lazy {activity_main_button_add}

    /************************************Life cycle**********************************************/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG_ACTIVITY, "Activity '${javaClass.simpleName}' created")
        setContentView(R.layout.activity_main)
        JodaTimeAndroid.init(this)
        initializeToolbar()
        initializeNavigationView()
        initializeBottomNavigationView()
        initializeAddButton()
        requestLocationPermission()
    }

    /*******************************Menu monitoring**********************************************/

    /**Click on menu item from BottomNavigationView**/

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        return when(item.groupId) {
            R.id.menu_main -> {
                //TODO handle clicks
                when (item.itemId) {
                    R.id.menu_main_map -> {
                        Log.d(TAG_MENU, "Menu '${item.title}' clicked")
                        showFragment(ID_FRAGMENT_MAP)
                    }
                    R.id.menu_main_trails ->
                        Log.d(TAG_MENU, "Menu '${item.title}' clicked")
                    R.id.menu_main_events ->
                        Log.d(TAG_MENU, "Menu '${item.title}' clicked")
                }
                true
            }
            R.id.menu_user -> {
                //TODO handle clicks
                when (item.itemId) {
                    R.id.menu_user_profile -> {
                        Log.d(TAG_MENU, "Menu '${item.title}' clicked")
                    }
                    R.id.menu_user_login -> {
                        Log.d(TAG_MENU, "Menu '${item.title}' clicked")
                        login()
                    }
                }
                true
            }
            else -> false
        }
    }

    /**Click on menu item from PopupMenu**/

    private fun onPopupMenuItemClick(item: MenuItem?): Boolean {
        when(item?.groupId) {
            R.id.menu_add -> {
                //TODO handle clicks
                when (item.itemId) {
                    R.id.menu_add_event ->
                        Log.d(TAG_MENU, "Menu '${item.title}' clicked")
                }
            }
            R.id.menu_add_new_trail-> {
                when (item.itemId) {
                    R.id.menu_add_trail_load_gpx -> {
                        Log.d(TAG_MENU, "Menu '${item.title}' clicked")
                        startTrailActivity(TrailActivity.ACTION_TRAIL_CREATE_FROM_GPX)
                    }
                    R.id.menu_add_trail_draw -> {
                        Log.d(TAG_MENU, "Menu '${item.title}' clicked")
                        startTrailActivity(TrailActivity.ACTION_TRAIL_DRAW)
                    }
                    R.id.menu_add_trail_record -> {
                        Log.d(TAG_MENU, "Menu '${item.title}' clicked")
                        startTrailActivity(TrailActivity.ACTION_TRAIL_RECORD)
                    }
                }
            }
        }
        return true
    }

    /**Shows the add menu options within a PopupMenu**/

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
     * Loads the trails from the database
     * @param listener : the listener which handles the result
     */

    fun loadTrails(listener:TrailFirebaseQueries.OnTrailQueryResultListener){

        //TODO add a progress bar
        
        TrailFirebaseQueries.getTrails()
            .addSnapshotListener { querySnapshot, e ->

                /*If the query is a success, displays the results*/

                if(querySnapshot!=null){
                    Log.d(TAG_STORAGE, "Query finished with ${querySnapshot.size()} trails")
                    this.trails.clear()
                    querySnapshot.forEach { documentSnapshot ->
                        val trail=documentSnapshot.toObject(Trail::class.java)
                        trail.id=documentSnapshot.id
                        this.trails.add(trail)
                        listener.onTrailsQueryResult(this.trails)
                    }
                }

                /*Else*/

                else if(e!=null){
                    //TODO handle
                    Log.w(TAG_STORAGE, e.message.toString())
                }
            }
    }

    /******************************UI monitoring**************************************************/

    private fun initializeToolbar(){
        setSupportActionBar(this.toolbar)
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

        val user=FirebaseAuth.getInstance().currentUser
        if(user==null){
            this.navigationHeaderUserImage.setImageResource(R.drawable.ic_user_white)
            this.navigationHeaderUserNameText.setText(R.string.message_user_unknown)
        }

        /*Else shows the user's info*/

        else{
            Glide.with(this)
                .load(user.photoUrl)
                .apply(RequestOptions.circleCropTransform())
                .placeholder(R.drawable.ic_user_white)
                .into(this.navigationHeaderUserImage)
            this.navigationHeaderUserNameText.text=user.displayName
        }
    }

    /******************************Login / Logout************************************************/

    private fun login(){
        //TODO add a progress bar
        val user=FirebaseAuth.getInstance().currentUser
        if(user==null){
            startLoginActivity()
        }else{
            FirebaseAuth.getInstance().signOut()
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
            ID_FRAGMENT_MAP->this.fragment=
                TrailMapFragment(this.trails)
            //TODO handle other fragments
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
                        Log.d(TAG_PERMISSION,
                            "Permission '$KEY_BUNDLE_PERMISSION_LOCATION' granted")
                        showFragment(ID_FRAGMENT_MAP)
                    }
                    PackageManager.PERMISSION_DENIED -> {
                        //TODO handle
                        Log.d(TAG_PERMISSION,
                            "Permission '$KEY_BUNDLE_PERMISSION_LOCATION' denied")
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
     * Starts the TrailActivity
     * @param trailActionId : defines which action should be performed (choice within TrailActivity.ACTION_TRAIL_xxx)
     * @param trail (optionnal) : the trail to see
     */

    fun startTrailActivity(trailActionId: Int, trail:Trail?=null){
        val trailActivityIntent= Intent(this, TrailActivity::class.java)
        trailActivityIntent.putExtra(KEY_BUNDLE_TRAIL_ACTION, trailActionId)
        if(trailActionId==TrailActivity.ACTION_TRAIL_SEE){
            trailActivityIntent.putExtra(KEY_BUNDLE_TRAIL, trail)
        }
        startActivity(trailActivityIntent)
    }

    /**Activity result**/

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode== KEY_REQUEST_LOGIN){
            handleLoginResult(resultCode, data)
        }
    }

    /**Handles login result**/

    private fun handleLoginResult(resultCode: Int, data: Intent?){
        val idpResponse=IdpResponse.fromResultIntent(data)
        when{
            resultCode== Activity.RESULT_OK -> {
                //TODO handle
                Log.d(TAG_LOGIN, "Login successful")
                updateNavigationViewUserItems()
            }
            resultCode==Activity.RESULT_CANCELED -> {
                //TODO handle
                Log.d(TAG_LOGIN, "Login canceled")
            }
            idpResponse?.error!=null -> {
                //TODO handle
                Log.w(TAG_LOGIN, "Login failed : ${idpResponse.error?.message}")
            }
            else -> {
                //TODO handle
                Log.w(TAG_LOGIN, "Login failed : unknown error")
            }
        }
    }
}
