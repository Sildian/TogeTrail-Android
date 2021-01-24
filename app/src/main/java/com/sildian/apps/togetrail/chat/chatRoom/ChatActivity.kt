package com.sildian.apps.togetrail.chat.chatRoom

import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.common.baseControllers.BaseActivity
import com.sildian.apps.togetrail.common.baseControllers.BaseFragment
import kotlinx.android.synthetic.main.activity_chat.*

/*************************************************************************************************
 * This activity allows the user to chat with other hikers
 ************************************************************************************************/

class ChatActivity : BaseActivity() {

    /**********************************Static items**********************************************/

    companion object {

        /**Bundle keys for intent**/
        const val KEY_BUNDLE_INTERLOCUTOR_ID = "KEY_BUNDLE_INTERLOCUTOR_ID"     //The interlocutor id -> Optional, only if already known
    }

    /**********************************UI component**********************************************/

    private val toolbar by lazy { activity_chat_toolbar }
    private lateinit var fragment: BaseFragment

    /********************************Navigation control******************************************/

    override fun onBackPressed() {
        finishCancel()
    }

    override fun onSupportNavigateUp(): Boolean {
        finishCancel()
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
            showChatListFragment()
        }
    }

    /*************************************UI monitoring******************************************/

    override fun getLayoutId(): Int = R.layout.activity_chat

    override fun initializeUI() {
        initializeToolbar()
    }

    private fun initializeToolbar() {
        setSupportActionBar(this.toolbar)
        supportActionBar?.setTitle(R.string.toolbar_chat)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    /******************************Fragments monitoring******************************************/

    private fun showChatListFragment() {

    }

    private fun showChatRoomFragment(interlocutorId: String) {
        this.fragment = ChatRoomFragment(interlocutorId)
        supportFragmentManager.beginTransaction()
            .replace(R.id.activity_chat_fragment, this.fragment).commit()
    }
}