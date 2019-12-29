package com.sildian.apps.togetrail.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.trail.TrailMapFragment
import kotlinx.android.synthetic.main.activity_main.*

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
        const val ID_FRAGMENT_TRAIL_MAP=1
        const val ID_FRAGMENT_EVENTS=2
        const val ID_FRAGMENT_HIKERS=3
    }

    /**********************************UI component**********************************************/

    private lateinit var fragment:Fragment
    private val bottomNavigationView by lazy {activity_main_bottom_navigation_view}

    /************************************Life cycle**********************************************/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG_ACTIVITY, "Activity '${javaClass.simpleName}' created")
        setContentView(R.layout.activity_main)
        initializeBottomNavigationView()
        showFragment(ID_FRAGMENT_TRAIL_MAP)
    }

    /*******************************Menu monitoring**********************************************/

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        Log.d(TAG_MENU, "Menu '${item.title}' clicked")
        if(item.groupId== R.id.menu_bottom_navigation_view){
            //TODO handle events
            when(item.itemId){
                R.id.menu_bottom_navigation_view_map ->
                    Log.d(TAG_MENU, "Menu '${item.title}' clicked")
                R.id.menu_bottom_navigation_view_events ->
                    Log.d(TAG_MENU, "Menu '${item.title}' clicked")
                R.id.menu_bottom_navigation_view_hikers ->
                    Log.d(TAG_MENU, "Menu '${item.title}' clicked")
            }
        }
        return true
    }

    /******************************UI monitoring**************************************************/

    private fun initializeBottomNavigationView(){
        this.bottomNavigationView.setOnNavigationItemSelectedListener(this)
    }

    /******************************Fragments monitoring******************************************/

    private fun showFragment(fragmentId:Int){
        when(fragmentId){
            ID_FRAGMENT_TRAIL_MAP->this.fragment=TrailMapFragment()
            //TODO handle other fragments
        }
        supportFragmentManager.beginTransaction()
            .replace(R.id.activity_main_fragment, this.fragment).commit()
    }
}
