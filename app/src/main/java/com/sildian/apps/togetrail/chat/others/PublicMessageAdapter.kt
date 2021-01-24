package com.sildian.apps.togetrail.chat.others

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.chat.model.core.Message

/*************************************************************************************************
 * Displays messages in a public chat space
 ************************************************************************************************/

class PublicMessageAdapter(
    options: FirestoreRecyclerOptions<Message>,
    private val onAuthorClickListener: PublicMessageViewHolder.OnAuthorClickListener? = null,
    private val onMessageModificationClickListener: PublicMessageViewHolder.OnMessageModificationClickListener? = null,
    private val onMessagesChangedListener: OnMessagesChangedListener? = null
)
    : FirestoreRecyclerAdapter<Message, PublicMessageViewHolder>(options)
{

    private var recyclerView: RecyclerView? = null

    interface OnMessagesChangedListener {
        fun onMessagesChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PublicMessageViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_recycler_view_public_message, parent, false)
        return PublicMessageViewHolder(view, onAuthorClickListener, onMessageModificationClickListener)
    }

    override fun onBindViewHolder(holder: PublicMessageViewHolder, position: Int, message: Message) {
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