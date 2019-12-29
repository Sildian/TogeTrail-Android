package com.sildian.apps.togetrail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*

/*************************************************************************************************
 * Lets the user navigate between the main screens
 ************************************************************************************************/

class MainActivity :
    AppCompatActivity(),
    BottomNavigationView.OnNavigationItemSelectedListener {

    /**********************************Static items**********************************************/

    companion object{

        /**Logs**/

        const val TAG_MENU="TAG_MENU"
    }

    /**********************************UI component*s********************************************/

    private val bottomNavigationView by lazy {activity_main_bottom_navigation_view}

    /*******************************Activity life cycle******************************************/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initializeBottomNavigationView()
    }

    /*******************************Menu monitoring**********************************************/

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        if(item.groupId==R.id.menu_bottom_navigation_view){
            when(item.itemId){
                R.id.menu_bottom_navigation_view_map-> Log.d(TAG_MENU, "Map")
                R.id.menu_bottom_navigation_view_events-> Log.d(TAG_MENU, "Events")
                R.id.menu_bottom_navigation_view_hikers-> Log.d(TAG_MENU, "Hikers")
            }
        }
        return true
    }

    /******************************UI monitoring**************************************************/

    private fun initializeBottomNavigationView(){
        this.bottomNavigationView.setOnNavigationItemSelectedListener(this)
    }
}
