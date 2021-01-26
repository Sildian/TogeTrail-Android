package com.sildian.apps.togetrail.chat.others

import android.view.LayoutInflater
import android.view.ViewGroup
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.chat.model.core.Duo

/*************************************************************************************************
 * Displays a list of chats
 ************************************************************************************************/

class ChatAdapter(
    options: FirestoreRecyclerOptions<Duo>,
    private val onChatClickListener: ChatViewHolder.OnChatClickListener? = null,
    private val onChatsChangedListener: OnChatsChangedListener? = null
)
    : FirestoreRecyclerAdapter<Duo, ChatViewHolder>(options)
{

    interface OnChatsChangedListener {
        fun onChatsChanged(itemCount: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_recycler_view_chat, parent, false)
        return ChatViewHolder(view, onChatClickListener)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int, chat: Duo) {
        holder.updateUI(chat)
    }

    override fun onDataChanged() {
        this.onChatsChangedListener?.onChatsChanged(itemCount)
    }
}