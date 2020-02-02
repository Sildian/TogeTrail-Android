package com.sildian.apps.togetrail.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.view.menu.MenuPopupHelper
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.trail.map.TrailActivity
import com.sildian.apps.togetrail.trail.map.TrailMapFragment
import kotlinx.android.synthetic.main.activity_main.*
import net.danlew.android.joda.JodaTimeAndroid
import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth

/*************************************************************************************************
 * Lets the user navigate between the main screens
 ************************************************************************************************/

class MainActivity :
    AppCompatActivity(),
    BottomNavigationView.OnNavigationItemSelectedListener
{

    /**********************************Static items**********************************************/

    companion object{

        /**Logs**/
        const val TAG_ACTIVITY="TAG_ACTIVITY"
        const val TAG_MENU="TAG_MENU"
        const val TAG_LOGIN="TAG_LOGIN"
        const val TAG_PERMISSION="TAG_PERMISSION"

        /**Fragments Ids***/
        const val ID_FRAGMENT_MAP=1
        const val ID_FRAGMENT_TRAILS=2
        const val ID_FRAGMENT_EVENTS=3

        /**Request keys for permissions**/
        const val KEY_REQUEST_LOGIN=1001
        const val KEY_REQUEST_PERMISSION_LOCATION=2001

        /**Bundle keys for permissions**/
        const val KEY_BUNDLE_PERMISSION_LOCATION=Manifest.permission.ACCESS_FINE_LOCATION

        /**Bundle keys for intents**/
        const val KEY_BUNDLE_TRAIL_ACTION="KEY_BUNDLE_TRAIL_ACTION"
    }

    /**********************************UI component**********************************************/

    private lateinit var fragment:Fragment
    private val bottomNavigationView by lazy {activity_main_bottom_navigation_view}
    private val addButton by lazy {activity_main_button_add}

    /************************************Life cycle**********************************************/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG_ACTIVITY, "Activity '${javaClass.simpleName}' created")
        setContentView(R.layout.activity_main)
        JodaTimeAndroid.init(this)
        initializeBottomNavigationView()
        initializeAddButton()
        login()
        requestLocationPermission()
    }

    /*******************************Menu monitoring**********************************************/

    /**Click on menu item from BottomNavigationView**/

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        if(item.groupId== R.id.menu_main){
            //TODO handle clicks
            when(item.itemId){
                R.id.menu_main_map ->
                    Log.d(TAG_MENU, "Menu '${item.title}' clicked")
                R.id.menu_main_trails ->
                    Log.d(TAG_MENU, "Menu '${item.title}' clicked")
                R.id.menu_main_events ->
                    Log.d(TAG_MENU, "Menu '${item.title}' clicked")
            }
        }
        return true
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

    /******************************UI monitoring**************************************************/

    private fun initializeBottomNavigationView(){
        this.bottomNavigationView.setOnNavigationItemSelectedListener(this)
    }

    private fun initializeAddButton(){
        this.addButton.setOnClickListener {
            showAddMenuOptions()
        }
    }

    /******************************Login / Logout************************************************/

    private fun login(){
        val user=FirebaseAuth.getInstance().currentUser
        if(user==null){
            startLoginActivity()
        }else{
            //TODO handle
        }
    }

    /******************************Fragments monitoring******************************************/

    /**
     * Shows a fragment
     * @param fragmentId : defines which fragment to display (choice within ID_FRAGMENT_xxx)
     */

    private fun showFragment(fragmentId:Int){
        when(fragmentId){
            ID_FRAGMENT_MAP->this.fragment=
                TrailMapFragment()
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
                .setAvailableProviders(listOf(AuthUI.IdpConfig.EmailBuilder().build()))
                .setIsSmartLockEnabled(false, true)
                .build(),
            KEY_REQUEST_LOGIN
        )
    }

    /**
     * Starts the TrailActivity
     * @param trailActionId : defines which action should be performed (choice within TrailActivity.ACTION_TRAIL_xxx)
     */

    private fun startTrailActivity(trailActionId: Int){
        val trailActivityIntent= Intent(this, TrailActivity::class.java)
        trailActivityIntent.putExtra(KEY_BUNDLE_TRAIL_ACTION, trailActionId)
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
