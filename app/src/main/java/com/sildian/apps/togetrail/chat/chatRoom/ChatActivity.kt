package com.sildian.apps.togetrail.chat.chatRoom

import android.content.Intent
import androidx.databinding.ViewDataBinding
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.common.baseControllers.BaseActivity
import com.sildian.apps.togetrail.common.baseControllers.BaseFragment
import com.sildian.apps.togetrail.databinding.ActivityChatBinding
import com.sildian.apps.togetrail.hiker.profile.ProfileActivity
import dagger.hilt.android.AndroidEntryPoint

/*************************************************************************************************
 * This activity allows the user to chat with other hikers
 ************************************************************************************************/

@AndroidEntryPoint
class ChatActivity : BaseActivity<ActivityChatBinding>() {

    /**********************************Static items**********************************************/

    companion object {

        /**Bundle keys for intent**/
        const val KEY_BUNDLE_INTERLOCUTOR_ID = "KEY_BUNDLE_INTERLOCUTOR_ID"     //The interlocutor id -> Optional, only if already known
    }

    /**********************************UI component**********************************************/

    private lateinit var fragment: BaseFragment<out ViewDataBinding>

    /********************************Navigation control******************************************/

    override fun onBackPressed() {
        if (this.fragment is ChatRoomFragment) {
            showChatSelectionFragment()
        }
        else {
            finishCancel()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        if (this.fragment is ChatRoomFragment) {
            showChatSelectionFragment()
        }
        else {
            finishCancel()
        }
        return true
    }

    /***********************************Data monitoring******************************************/

    override fun loadData() {
        readDataFromIntent()
    }

    private fun readDataFromIntent() {
        if (intent != null && intent.hasExtra(KEY_BUNDLE_INTERLOCUTOR_ID)) {
            val interlocutorId = intent.getStringExtra(KEY_BUNDLE_INTERLOCUTOR_ID)
            interlocutorId?.let { id ->
                showChatRoomFragment(id)
            }
        }
        else {
            showChatSelectionFragment()
        }
    }

    /*************************************UI monitoring******************************************/

    override fun getLayoutId(): Int = R.layout.activity_chat

    override fun initializeUI() {
        initializeToolbar()
    }

    private fun initializeToolbar() {
        setSupportActionBar(this.binding.activityChatToolbar)
        supportActionBar?.setTitle(R.string.toolbar_chat)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    fun setToolbarTitle(title: String) {
        supportActionBar?.title = title
    }

    /******************************Fragments monitoring******************************************/

    fun seeChatRoom(interlocutorId: String) {
        showChatRoomFragment(interlocutorId)
    }

    private fun showChatSelectionFragment() {
        supportActionBar?.setTitle(R.string.toolbar_chat)
        this.fragment = ChatSelectionFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.activity_chat_fragment, this.fragment).commit()
    }

    private fun showChatRoomFragment(interlocutorId: String) {
        this.fragment = ChatRoomFragment(interlocutorId)
        supportFragmentManager.beginTransaction()
            .replace(R.id.activity_chat_fragment, this.fragment).commit()
    }

    /*********************************Hikers monitoring******************************************/

    fun seeHiker(hikerId: String){
        startProfileActivity(hikerId)
    }

    /***********************************Navigation***********************************************/

    private fun startProfileActivity(hikerId:String){
        val profileActivityIntent= Intent(this, ProfileActivity::class.java)
        profileActivityIntent.putExtra(ProfileActivity.KEY_BUNDLE_HIKER_ID, hikerId)
        startActivity(profileActivityIntent)
    }
}