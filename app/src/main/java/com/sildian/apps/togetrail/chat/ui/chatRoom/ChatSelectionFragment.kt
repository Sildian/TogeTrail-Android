package com.sildian.apps.togetrail.chat.ui.chatRoom

import androidx.lifecycle.MutableLiveData
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.chat.data.models.Duo
import com.sildian.apps.togetrail.chat.ui.others.ChatAdapter
import com.sildian.apps.togetrail.common.baseControllers.BaseFragment
import com.sildian.apps.togetrail.common.utils.cloudHelpers.DatabaseFirebaseHelper
import com.sildian.apps.togetrail.databinding.FragmentChatSelectionBinding
import com.sildian.apps.togetrail.hiker.data.helpers.CurrentHikerInfo
import com.sildian.apps.togetrail.hiker.data.source.HikerFirebaseQueries

/*************************************************************************************************
 * Displays the current user's list of current chats
 ************************************************************************************************/

class ChatSelectionFragment :
    BaseFragment<FragmentChatSelectionBinding>(),
    ChatAdapter.OnChatClickListener,
    ChatAdapter.OnChatsChangedListener
{

    /***************************************Data*************************************************/

    val nbChats = MutableLiveData<Int>()

    /**********************************UI component**********************************************/

    private lateinit var chatsAdapter: ChatAdapter

    /******************************Data monitoring***********************************************/

    override fun initializeData() {
        this.binding.chatSelectionFragment = this
    }

    /***********************************UI monitoring********************************************/

    override fun getLayoutId(): Int = R.layout.fragment_chat_selection

    override fun initializeUI() {
        initializeChatsRecyclerView()
    }

    private fun initializeChatsRecyclerView() {
        CurrentHikerInfo.currentHiker?.id?.let { userId ->
            this.chatsAdapter = ChatAdapter(
                DatabaseFirebaseHelper.generateOptionsForAdapter(
                    Duo::class.java,
                    HikerFirebaseQueries.getChats(userId),
                    this
                ), this, this
            )
            this.binding.fragmentChatSelectionRecyclerViewChats.adapter = chatsAdapter
        }
    }

    /***********************************Chats monitoring*****************************************/

    override fun onChatClick(chat: Duo) {
        (baseActivity as ChatActivity).seeChatRoom(chat.interlocutorId)
    }

    override fun onChatsChanged(itemCount: Int) {
        this.nbChats.value = itemCount
    }
}