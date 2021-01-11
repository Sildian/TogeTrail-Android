package com.sildian.apps.togetrail.chat.others

import android.view.LayoutInflater
import android.view.ViewGroup
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.chat.model.core.Message

/*************************************************************************************************
 * Displays messages in a chat space
 ************************************************************************************************/

class MessageAdapter(
    options: FirestoreRecyclerOptions<Message>,
    private val onAuthorClickListener: MessageViewHolder.OnAuthorClickListener? = null,
    private val onMessageModificationClickListener: MessageViewHolder.OnMessageModificationClickListener? = null,
    private val onMessagesChangedListener: OnMessagesChangedListener? = null
)
    : FirestoreRecyclerAdapter<Message, MessageViewHolder>(options)
{

    interface OnMessagesChangedListener {
        fun onMessagesChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_recycler_view_message, parent, false)
        return MessageViewHolder(view, onAuthorClickListener, onMessageModificationClickListener)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int, message: Message) {
        holder.updateUI(message)
    }

    override fun onDataChanged() {
        this.onMessagesChangedListener?.onMessagesChanged()
    }
}