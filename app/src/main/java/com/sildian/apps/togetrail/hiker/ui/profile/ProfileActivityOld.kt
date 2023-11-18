package com.sildian.apps.togetrail.hiker.ui.profile

import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import androidx.databinding.ViewDataBinding
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.chat.ui.chatRoom.ChatActivity
import com.sildian.apps.togetrail.common.baseControllers.BaseActivity
import com.sildian.apps.togetrail.common.baseControllers.BaseFragment
import com.sildian.apps.togetrail.common.utils.cloudHelpers.AuthFirebaseQueries
import com.sildian.apps.togetrail.databinding.ActivityProfileOldBinding
import com.sildian.apps.togetrail.event.ui.detail.EventActivity
import com.sildian.apps.togetrail.hiker.data.helpers.CurrentHikerInfo
import com.sildian.apps.togetrail.hiker.ui.profileEdit.ProfileEditActivityOld
import com.sildian.apps.togetrail.trail.ui.map.TrailActivity
import dagger.hilt.android.AndroidEntryPoint

/*************************************************************************************************
 * Allows to see a hiker's profile or related information
 * @deprecated : Replaced by [com.sildian.apps.togetrail.uiLayer.hikerProfile.HikerProfileActivity]
 ************************************************************************************************/

@Deprecated("Replaced by [com.sildian.apps.togetrail.uiLayer.hikerProfile.HikerProfileActivity]")
@AndroidEntryPoint
class ProfileActivityOld : BaseActivity<ActivityProfileOldBinding>() {

    /**********************************Static items**********************************************/

    companion object {

        /**Bundle keys for intents**/
        const val KEY_BUNDLE_HIKER_ID="KEY_BUNDLE_HIKER_ID"         //Hiker id -> Mandatory
    }

    /****************************************Data************************************************/

    private var hikerId: String?=null

    /**********************************UI component**********************************************/

    private var fragment: BaseFragment<out ViewDataBinding>?=null

    /********************************Menu monitoring*********************************************/

    /**Generates the menu within the toolbar**/

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        when {
            this.hikerId == AuthFirebaseQueries.getCurrentUser()?.uid ->
                menuInflater.inflate(R.menu.menu_edit, menu)
            AuthFirebaseQueries.getCurrentUser() != null ->
                menuInflater.inflate(R.menu.menu_chat, menu)
            else -> { }
        }
        return true
    }

    /**Click on menu item from toolbar**/

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.groupId) {
            R.id.menu_edit -> {
                if (item.itemId == R.id.menu_edit_edit) {
                    startProfileEditActivity()
                }
            }
            R.id.menu_chat -> {
                if (item.itemId == R.id.menu_chat_chat) {
                    this.hikerId?.let { interlocutorId ->
                        if (interlocutorId != CurrentHikerInfo.currentHiker?.id) {
                            startChatActivity(interlocutorId)
                        }
                    }
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /********************************Navigation control******************************************/

    override fun onBackPressed() {
        finishCancel()
    }

    override fun onSupportNavigateUp(): Boolean {
        finishCancel()
        return true
    }

    /******************************Data monitoring************************************************/

    override fun initializeData() {
        readDataFromIntent()
    }

    private fun readDataFromIntent(){
        if(intent!=null){
            if(intent.hasExtra(KEY_BUNDLE_HIKER_ID)){
                this.hikerId = intent.getStringExtra(KEY_BUNDLE_HIKER_ID)
            }
        }
    }

    /******************************UI monitoring**************************************************/

    override fun getLayoutId(): Int = R.layout.activity_profile_old

    override fun initializeUI() {
        showFragment()
    }

    /******************************Trails monitoring*********************************************/

    fun seeTrail(trailId:String){
        startTrailActivity(trailId)
    }

    /******************************Events monitoring*********************************************/

    fun seeEvent(eventId:String){
        startEventActivity(eventId)
    }

    /******************************Fragments monitoring******************************************/

    private fun showFragment(){
        this.fragment=ProfileFragmentOld(this.hikerId)
        this.fragment?.let { fragment ->
            supportFragmentManager.beginTransaction()
                .replace(R.id.activity_profile_fragment, fragment).commit()
        }
    }

    /***********************************Navigation***********************************************/

    private fun startProfileEditActivity(){
        val profileEditActivityIntent=Intent(this, ProfileEditActivityOld::class.java)
        profileEditActivityIntent.putExtra(ProfileEditActivityOld.KEY_BUNDLE_PROFILE_ACTION, ProfileEditActivityOld.ACTION_PROFILE_EDIT_INFO)
        profileEditActivityIntent.putExtra(ProfileEditActivityOld.KEY_BUNDLE_HIKER_ID, this.hikerId)
        startActivity(profileEditActivityIntent)
    }

    private fun startTrailActivity(trailId:String){
        val trailActivityIntent=Intent(this, TrailActivity::class.java)
        trailActivityIntent.putExtra(TrailActivity.KEY_BUNDLE_TRAIL_ACTION, TrailActivity.ACTION_TRAIL_SEE)
        trailActivityIntent.putExtra(TrailActivity.KEY_BUNDLE_TRAIL_ID, trailId)
        startActivity(trailActivityIntent)
    }

    private fun startEventActivity(eventId:String){
        val eventActivityIntent=Intent(this, EventActivity::class.java)
        eventActivityIntent.putExtra(EventActivity.KEY_BUNDLE_EVENT_ID, eventId)
        startActivity(eventActivityIntent)
    }

    private fun startChatActivity(interlocutorId: String) {
        val chatActivityIntent = Intent(this, ChatActivity::class.java)
        chatActivityIntent.putExtra(ChatActivity.KEY_BUNDLE_INTERLOCUTOR_ID, interlocutorId)
        startActivity(chatActivityIntent)
    }
}
