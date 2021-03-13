package com.sildian.apps.togetrail.chat.others

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.chat.model.core.Duo
import com.sildian.apps.togetrail.databinding.ItemRecyclerViewChatBinding
import java.util.*

/*************************************************************************************************
 * Displays a list of chats
 ************************************************************************************************/

class ChatAdapter(
    options: FirestoreRecyclerOptions<Duo>,
    private val onChatClickListener: OnChatClickListener? = null,
    private val onChatsChangedListener: OnChatsChangedListener? = null
)
    : FirestoreRecyclerAdapter<Duo, ChatAdapter.ChatViewHolder>(options)
{

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemRecyclerViewChatBinding =
            DataBindingUtil.inflate(inflater, R.layout.item_recycler_view_chat, parent, false)
        return ChatViewHolder(binding, onChatClickListener)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int, chat: Duo) {
        holder.updateUI(chat)
    }

    override fun onDataChanged() {
        this.onChatsChangedListener?.onChatsChanged(itemCount)
    }

    /***********************************Callbacks************************************************/

    interface OnChatsChangedListener {
        fun onChatsChanged(itemCount: Int)
    }

    interface OnChatClickListener {
        fun onChatClick(chat: Duo)
    }

    /***********************************ViewHolder************************************************/

    class ChatViewHolder(
        private val binding: ItemRecyclerViewChatBinding,
        private val onChatClickListener: OnChatClickListener? = null
    )
        : RecyclerView.ViewHolder(binding.root)
    {

        /**************************************Data***********************************************/

        private lateinit var chat: Duo

        /**************************************Init***********************************************/

        init {
            this.binding.chatViewHolder = this
        }

        /**********************************UI monitoring******************************************/

        fun updateUI(chat: Duo) {
            this.chat = chat
            this.binding.chat = this.chat
            this.binding.date = Date()
        }

        @Suppress("UNUSED_PARAMETER")
        fun onChatClick(view: View) {
            this.onChatClickListener?.onChatClick(this.chat)
        }
    }
}