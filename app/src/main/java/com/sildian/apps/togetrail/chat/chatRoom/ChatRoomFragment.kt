package com.sildian.apps.togetrail.chat.chatRoom

import android.view.View
import androidx.fragment.app.viewModels
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.chat.model.core.Message
import com.sildian.apps.togetrail.chat.others.PrivateMessageAdapter
import com.sildian.apps.togetrail.common.baseControllers.BaseFragment
import com.sildian.apps.togetrail.common.utils.cloudHelpers.DatabaseFirebaseHelper
import com.sildian.apps.togetrail.databinding.FragmentChatRoomBinding
import com.sildian.apps.togetrail.hiker.model.support.CurrentHikerInfo
import com.sildian.apps.togetrail.hiker.model.dataRepository.HikerFirebaseQueries
import com.sildian.apps.togetrail.hiker.model.viewModels.HikerViewModel
import dagger.hilt.android.AndroidEntryPoint

/*************************************************************************************************
 * Displays a chat's content and lets the user read and send messages in the room
 ************************************************************************************************/

@AndroidEntryPoint
class ChatRoomFragment(private val interlocutorId: String? = null) :
    BaseFragment<FragmentChatRoomBinding>(),
    PrivateMessageAdapter.OnAuthorClickListener
{

    /*****************************************Data***********************************************/

    private val hikerViewModel: HikerViewModel by viewModels()

    /**********************************UI component**********************************************/

    private lateinit var messagesAdapter: PrivateMessageAdapter

    /******************************Data monitoring***********************************************/

    override fun loadData() {
        initializeData()
        observeHiker()
        loadHiker()
    }

    private fun initializeData() {
        this.binding.chatRoomFragment = this
    }

    private fun observeHiker() {
        this.hikerViewModel.data.observe(this) { hikerData ->
            hikerData?.error?.let { e ->
                onQueryError(e)
            } ?:
            hikerData?.data?.name?.let { hikerName ->
                (baseActivity as ChatActivity).setToolbarTitle(hikerName)
                markLastMessageAsRead()
            }
        }
    }

    private fun loadHiker() {
        this.interlocutorId?.let { interlocutorId ->
            this.hikerViewModel.loadHikerRealTime(interlocutorId)
        }
    }

    private fun markLastMessageAsRead() {
        this.hikerViewModel.markLastMessageAsRead()
    }

    /***********************************UI monitoring********************************************/

    override fun getLayoutId(): Int = R.layout.fragment_chat_room

    override fun initializeUI() {
        initializeMessagesRecyclerView()
    }

    private fun initializeMessagesRecyclerView() {
        CurrentHikerInfo.currentHiker?.id?.let { userId ->
            interlocutorId?.let { interlocutorId ->
                this.messagesAdapter = PrivateMessageAdapter(
                    DatabaseFirebaseHelper.generateOptionsForAdapter(
                        Message::class.java,
                        HikerFirebaseQueries.getMessages(userId, interlocutorId),
                        this
                    ), this
                )
                this.binding.fragmentChatRoomRecyclerViewMessages.adapter = this.messagesAdapter
            }
        }
    }

    @Suppress("UNUSED_PARAMETER")
    fun onCancelMessageButtonClick(view: View) {
        this.binding.fragmentChatRoomTextFieldMessage.setText("")
    }

    @Suppress("UNUSED_PARAMETER")
    fun onValidateMessageButtonClick(view: View) {
        this.interlocutorId?.let { id ->
            val text = this.binding.fragmentChatRoomTextFieldMessage.text.toString()
            if (text.isNotEmpty()) {
                this.hikerViewModel.sendMessage(text)
                this.binding.fragmentChatRoomTextFieldMessage.setText("")
            }
        }
    }

    /*************************************Chat monitoring****************************************/

    override fun onAuthorClick(authorId: String) {
        (baseActivity as ChatActivity).seeHiker(authorId)
    }
}