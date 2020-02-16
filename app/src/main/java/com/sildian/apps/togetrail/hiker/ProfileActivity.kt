package com.sildian.apps.togetrail.hiker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.hiker.model.core.Hiker
import com.sildian.apps.togetrail.main.MainActivity
import com.sildian.apps.togetrail.trail.info.TrailInfoFragment
import com.sildian.apps.togetrail.trail.map.TrailActivity
import kotlinx.android.synthetic.main.activity_profile.*

/*************************************************************************************************
 * Lets a user see and edit his own profile
 ************************************************************************************************/

class ProfileActivity : AppCompatActivity() {

    /**********************************Static items**********************************************/

    companion object {

        /**Logs**/
        private const val TAG_ACTIVITY = "TAG_ACTIVITY"

        /**Fragments ids**/
        private const val ID_FRAGMENT_INFO=0
        private const val ID_FRAGMENT_TRAILS=1
        private const val ID_FRAGMENT_SETTINGS=2
    }

    /****************************************Data************************************************/

    private var hiker: Hiker?=null

    /**********************************UI component**********************************************/

    private val toolbar by lazy {activity_profile_toolbar}
    private val userImage by lazy {activity_profile_user_image}
    private val userNameText by lazy {activity_profile_text_user_name}
    private val tabLayout by lazy {activity_profile_tab_layout}
    private val viewPager by lazy {activity_profile_view_pager}

    /************************************Life cycle**********************************************/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG_ACTIVITY, "Activity '${javaClass.simpleName}' created")
        setContentView(R.layout.activity_profile)
        readDataFromIntent(intent)
        initializeToolbar()
        initializeUserMainInfo()
        initializeViewPager()
    }

    /******************************Data monitoring************************************************/

    private fun readDataFromIntent(intent: Intent?){
        if(intent!=null){
            if(intent.hasExtra(MainActivity.KEY_BUNDLE_HIKER)){
                this.hiker= intent.getParcelableExtra(MainActivity.KEY_BUNDLE_HIKER)
            }
        }
    }

    /******************************UI monitoring**************************************************/

    private fun initializeToolbar(){
        setSupportActionBar(this.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle(R.string.toolbar_profile)
    }

    private fun initializeUserMainInfo(){

        /*If the user is null, then shows default info*/

        val user=FirebaseAuth.getInstance().currentUser
        if(user==null){
            this.userImage.setImageResource(R.drawable.ic_user_black)
            this.userNameText.setText(R.string.message_user_unknown)
        }

        /*Else shows the user's info*/

        else{
            Glide.with(this)
                .load(user.photoUrl)
                .apply(RequestOptions.circleCropTransform())
                .placeholder(R.drawable.ic_user_black)
                .into(this.userImage)
            this.userNameText.text=user.displayName
        }
    }

    private fun initializeViewPager(){
        val profileCollectionAdapter=ProfileCollectionAdapter(this)
        this.viewPager.adapter=profileCollectionAdapter
        TabLayoutMediator(this.tabLayout, this.viewPager) { tab, position ->
            when(position){
                ID_FRAGMENT_INFO->{
                    tab.setIcon(R.drawable.ic_user_black)
                    tab.setText(R.string.menu_user_info)
                }
                ID_FRAGMENT_TRAILS->{
                    tab.setIcon(R.drawable.ic_trail_black)
                    tab.setText(R.string.menu_user_trails)
                }
                ID_FRAGMENT_SETTINGS->{
                    tab.setIcon(R.drawable.ic_settings_black)
                    tab.setText(R.string.menu_user_settings)
                }
            }
        }.attach()
    }

    /********************This class defines the viewPager's behavior*****************************/

    class ProfileCollectionAdapter(activity:FragmentActivity) : FragmentStateAdapter(activity) {

        override fun getItemCount() = 3

        override fun createFragment(position: Int): Fragment {

            //TODO define the fragments

            return when(position){
                ID_FRAGMENT_INFO->TrailInfoFragment()
                ID_FRAGMENT_TRAILS->TrailInfoFragment()
                ID_FRAGMENT_SETTINGS->TrailInfoFragment()
                else->TrailInfoFragment()
            }
        }
    }
}
