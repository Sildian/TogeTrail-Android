package com.sildian.apps.togetrail.chat.others

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.chat.model.core.Message

/*************************************************************************************************
 * Displays messages in a private chat space
 ************************************************************************************************/

class PrivateMessageAdapter(
    options: FirestoreRecyclerOptions<Message>,
    private val onAuthorClickListener: PrivateMessageViewHolder.OnAuthorClickListener? = null,
    private val onMessagesChangedListener: OnMessagesChangedListener? = null
)
    : FirestoreRecyclerAdapter<Message, PrivateMessageViewHolder>(options)
{

    private var recyclerView: RecyclerView? = null

    interface OnMessagesChangedListener {
        fun onMessagesChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrivateMessageViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_recycler_view_private_message, parent, false)
        return PrivateMessageViewHolder(view, onAuthorClickListener)
    }

    override fun onBindViewHolder(holder: PrivateMessageViewHolder, position: Int, message: Message) {
        holder.updateUI(message)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        this.recyclerView = recyclerView
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        this.recyclerView = null
    }

    override fun onDataChanged() {
        if (itemCount > 0) {
            this.recyclerView?.smoothScrollToPosition(itemCount - 1)
        }
        this.onMessagesChangedListener?.onMessagesChanged()
    }
}