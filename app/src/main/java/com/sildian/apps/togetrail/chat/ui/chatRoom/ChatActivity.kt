package com.sildian.apps.togetrail.chat.ui.chatRoom

import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import androidx.databinding.ViewDataBinding
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.common.baseControllers.BaseActivity
import com.sildian.apps.togetrail.common.baseControllers.BaseFragment
import com.sildian.apps.togetrail.databinding.ActivityChatBinding
import com.sildian.apps.togetrail.hiker.ui.profile.ProfileActivity
import com.sildian.apps.togetrail.hiker.ui.search.HikerSearchActivity
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
        navigateBack()
    }

    override fun onSupportNavigateUp(): Boolean {
        return navigateBack()
    }

    /********************************Menu monitoring*********************************************/

    /**Generates the menu within the toolbar**/

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (this.fragment is ChatSelectionFragment) {
            menuInflater.inflate(R.menu.menu_search, menu)
        } else {
            menu?.clear()
        }
        return true
    }

    /**Click on menu item from toolbar**/

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.groupId == R.id.menu_search) {
            if (item.itemId == R.id.menu_search_search) {
                startHikerSearchActivity()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /***********************************Data monitoring******************************************/

    override fun initializeData() {
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
        initFragmentTransactionCallback()
        initializeToolbar()
    }

    private fun initializeToolbar() {
        setSupportActionBar(this.binding.activityChatToolbar.viewToolbarSimpleToolbar)
        supportActionBar?.setTitle(R.string.toolbar_chat)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    fun setToolbarTitle(title: String) {
        supportActionBar?.title = title
    }

    /******************************Fragments monitoring******************************************/

    private fun initFragmentTransactionCallback() {
        supportFragmentManager.addFragmentOnAttachListener { fragmentManager, fragment ->
            invalidateOptionsMenu()
        }
    }

    fun seeChatRoom(interlocutorId: String) {
        showChatRoomFragment(interlocutorId)
    }

    private fun showChatSelectionFragment() {
        supportActionBar?.setTitle(R.string.toolbar_chat)
        this.fragment = ChatSelectionFragment()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.activity_chat_fragment, this.fragment)
            .addToBackStack(this.fragment.javaClass.simpleName)
            .commit()
    }

    private fun showChatRoomFragment(interlocutorId: String) {
        this.fragment = ChatRoomFragment(interlocutorId)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.activity_chat_fragment, this.fragment)
            .addToBackStack(this.fragment.javaClass.simpleName)
            .commit()
    }

    private fun navigateBack(): Boolean {
        return if (supportFragmentManager.backStackEntryCount > 1) {
            supportFragmentManager.popBackStack()
            false
        } else {
            finishCancel()
            true
        }
    }

    /*********************************Hikers monitoring******************************************/

    fun seeHiker(hikerId: String){
        startProfileActivity(hikerId)
    }

    /***********************************Navigation***********************************************/

    private fun startProfileActivity(hikerId:String) {
        val profileActivityIntent= Intent(this, ProfileActivity::class.java)
        profileActivityIntent.putExtra(ProfileActivity.KEY_BUNDLE_HIKER_ID, hikerId)
        startActivity(profileActivityIntent)
    }

    private fun startHikerSearchActivity() {
        val hikerSearchActivityIntent = Intent(this, HikerSearchActivity::class.java)
        startActivity(hikerSearchActivityIntent)
    }
}