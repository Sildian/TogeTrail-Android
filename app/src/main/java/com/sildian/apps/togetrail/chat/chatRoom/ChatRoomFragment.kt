package com.sildian.apps.togetrail.chat.chatRoom

import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.chat.model.core.Message
import com.sildian.apps.togetrail.chat.others.PrivateMessageAdapter
import com.sildian.apps.togetrail.common.baseControllers.BaseFragment
import com.sildian.apps.togetrail.common.baseViewModels.ViewModelFactory
import com.sildian.apps.togetrail.common.utils.cloudHelpers.DatabaseFirebaseHelper
import com.sildian.apps.togetrail.databinding.FragmentChatRoomBinding
import com.sildian.apps.togetrail.hiker.model.support.CurrentHikerInfo
import com.sildian.apps.togetrail.hiker.model.support.HikerFirebaseQueries
import com.sildian.apps.togetrail.hiker.model.support.HikerViewModel
import kotlinx.android.synthetic.main.fragment_chat_room.view.*

/*************************************************************************************************
 * Displays a chat's content and lets the user read and send messages in the room
 ************************************************************************************************/

class ChatRoomFragment(private val interlocutorId: String? = null) :
    BaseFragment(),
    PrivateMessageAdapter.OnAuthorClickListener
{

    /*****************************************Data***********************************************/

    private lateinit var hikerViewModel: HikerViewModel

    /**********************************UI component**********************************************/

    private val messagesRecyclerView by lazy { layout.fragment_chat_room_recycler_view_messages }
    private lateinit var messagesAdapter: PrivateMessageAdapter
    private val messageTextField by lazy { layout.fragment_chat_room_text_field_message }

    /******************************Data monitoring***********************************************/

    override fun loadData() {
        initializeData()
        observeHiker()
        observeRequestFailure()
        loadHiker()
    }

    private fun initializeData() {
        this.hikerViewModel = ViewModelProviders
            .of(this, ViewModelFactory)
            .get(HikerViewModel::class.java)
        this.binding.lifecycleOwner = this
        (this.binding as FragmentChatRoomBinding).chatRoomFragment = this
    }

    private fun observeHiker() {
        this.hikerViewModel.hiker.observe(this) { hiker ->
            hiker?.name?.let { hikerName ->
                (baseActivity as ChatActivity).setToolbarTitle(hikerName)
                markLastMessageAsRead()
            }
        }
    }

    private fun observeRequestFailure() {
        this.hikerViewModel.requestFailure.observe(this) { e ->
            if (e != null) {
                onQueryError(e)
            }
        }
    }

    private fun loadHiker() {
        this.interlocutorId?.let { interlocutorId ->
            this.hikerViewModel.loadHikerFromDatabaseRealTime(interlocutorId)
        }
    }

    private fun markLastMessageAsRead() {
        this.hikerViewModel.markLastMessageAsRead()
    }

    /***********************************UI monitoring********************************************/

    override fun getLayoutId(): Int = R.layout.fragment_chat_room

    override fun useDataBinding(): Boolean = true

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
                this.messagesRecyclerView.adapter = this.messagesAdapter
            }
        }
    }

    @Suppress("UNUSED_PARAMETER")
    fun onCancelMessageButtonClick(view: View) {
        this.messageTextField.setText("")
    }

    @Suppress("UNUSED_PARAMETER")
    fun onValidateMessageButtonClick(view: View) {
        this.interlocutorId?.let { id ->
            val text = this.messageTextField.text.toString()
            if (text.isNotEmpty()) {
                this.hikerViewModel.sendMessage(text)
                this.messageTextField.setText("")
            }
        }
    }

    /*************************************Chat monitoring****************************************/

    override fun onAuthorClick(authorId: String) {
        (baseActivity as ChatActivity).seeHiker(authorId)
    }
}