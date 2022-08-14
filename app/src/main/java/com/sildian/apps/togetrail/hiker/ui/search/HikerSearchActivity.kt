package com.sildian.apps.togetrail.hiker.ui.search

import android.content.Intent
import android.util.Log
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.chat.ui.chatRoom.ChatActivity
import com.sildian.apps.togetrail.common.baseControllers.BaseActivity
import com.sildian.apps.togetrail.common.utils.cloudHelpers.DatabaseFirebaseHelper
import com.sildian.apps.togetrail.databinding.ActivityHikerSearchBinding
import com.sildian.apps.togetrail.hiker.data.helpers.CurrentHikerInfo
import com.sildian.apps.togetrail.hiker.data.models.Hiker
import com.sildian.apps.togetrail.hiker.data.source.HikerFirebaseQueries
import com.sildian.apps.togetrail.hiker.ui.others.HikerAdapter
import com.sildian.apps.togetrail.hiker.ui.profile.ProfileActivity

/*************************************************************************************************
 * Lets the user search for a hiker by typing a name
 ************************************************************************************************/

class HikerSearchActivity :
    BaseActivity<ActivityHikerSearchBinding>(),
    HikerAdapter.OnHikerClickListener,
    HikerAdapter.OnHikerMessageClickListener
{

    /**********************************Static items**********************************************/

    companion object {

        /**Logs**/
        private const val TAG = "HikerSearchActivity"

        /**Bundle keys for Intents**/
        const val KEY_BUNDLE_HIKER_ID = "KEY_BUNDLE_HIKER_ID"
    }

    /**********************************UI component**********************************************/

    private lateinit var hikersAdapter: HikerAdapter

    /*********************************Life cycle*************************************************/

    override fun onResume() {
        super.onResume()
        this.binding.activityHikerSearchToolbar.requestResearchFocus()
    }

    /********************************Navigation control******************************************/

    override fun onBackPressed() {
        finishCancel()
    }

    override fun onSupportNavigateUp(): Boolean {
        finishCancel()
        return true
    }

    /******************************UI monitoring**************************************************/

    override fun getLayoutId(): Int = R.layout.activity_hiker_search

    override fun initializeUI() {
        initializeToolbar()
        initializeHikersRecyclerView()
    }

    private fun initializeToolbar(){
        setSupportActionBar(this.binding.activityHikerSearchToolbar.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""
        this.binding.activityHikerSearchToolbar.onResearchUpdated = { v, research ->
            searchHikersWithNameContainingText(research)
        }
        this.binding.activityHikerSearchToolbar.onResearchCleared = { v ->
            searchHikersWithNameContainingText("")
        }
    }

    private fun initializeHikersRecyclerView() {
        this.hikersAdapter =
            HikerAdapter(
                generateOptionsForAdapter(""),
                this,
                this
            )
        this.binding.activityHikerSearchRecyclerViewHikersResults.adapter = this.hikersAdapter
    }

    /************************************Hiker research******************************************/

    private fun generateOptionsForAdapter(nameToSearch: String): FirestoreRecyclerOptions<Hiker> =
        DatabaseFirebaseHelper.generateOptionsForAdapter(
            Hiker::class.java,
            HikerFirebaseQueries.getHikersWithNameContainingText(nameToSearch, CurrentHikerInfo.currentHiker?.name?: ""),
            this
        )

    private fun searchHikersWithNameContainingText(text: String) {
        this.hikersAdapter.updateOptions(
            generateOptionsForAdapter(text)
        )
    }

    override fun onHikerClick(hiker: Hiker) {
        Log.d(TAG, "Selected hiker '${hiker.id}")
        startProfileActivity(hiker.id)
    }

    override fun onHikerMessageClick(hiker: Hiker) {
        Log.d(TAG, "Message to hiker '${hiker.id}")
        startChatActivity(hiker.id)
    }

    /***************************************Navigation*******************************************/

    private fun startProfileActivity(hikerId: String) {
        val profileActivityIntent = Intent(this, ProfileActivity::class.java)
        profileActivityIntent.putExtra(ProfileActivity.KEY_BUNDLE_HIKER_ID, hikerId)
        startActivity(profileActivityIntent)
    }

    private fun startChatActivity(hikerId: String) {
        val chatActivityIntent = Intent(this, ChatActivity::class.java)
        chatActivityIntent.putExtra(ChatActivity.KEY_BUNDLE_INTERLOCUTOR_ID, hikerId)
        startActivity(chatActivityIntent)
    }
}