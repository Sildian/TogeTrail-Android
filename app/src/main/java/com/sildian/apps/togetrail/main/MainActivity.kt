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

        /**Fragments Ids***/
        const val ID_FRAGMENT_MAP=1
        const val ID_FRAGMENT_TRAILS=2
        const val ID_FRAGMENT_EVENTS=3

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
        showFragment(ID_FRAGMENT_MAP)
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
                //TODO handle clicks
                when (item.itemId) {
                    R.id.menu_add_trail_load_gpx -> {
                        Log.d(TAG_MENU, "Menu '${item.title}' clicked")
                        startTrailActivity(TrailActivity.ACTION_TRAIL_CREATE_FROM_GPX)
                    }
                    R.id.menu_add_trail_draw ->
                        Log.d(TAG_MENU, "Menu '${item.title}' clicked")
                    R.id.menu_add_trail_record ->
                        Log.d(TAG_MENU, "Menu '${item.title}' clicked")
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
            .replace(R.id.activity_main_fragment, this.fragment).commit()
    }

    /***********************************Navigation***********************************************/

    /**
     * Starts the TrailActivity
     * @param trailActionId : defines which action should be performed (choice within TrailActivity.ACTION_TRAIL_xxx)
     */

    private fun startTrailActivity(trailActionId: Int){
        val trailActivityIntent= Intent(this, TrailActivity::class.java)
        trailActivityIntent.putExtra(KEY_BUNDLE_TRAIL_ACTION, trailActionId)
        startActivity(trailActivityIntent)
    }
}
