package com.sildian.apps.togetrail.chat.ui.others

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.chat.data.models.Message
import com.sildian.apps.togetrail.databinding.ItemRecyclerViewPrivateMessageBinding
import com.sildian.apps.togetrail.hiker.data.helpers.CurrentHikerInfo

/*************************************************************************************************
 * Displays messages in a private chat space
 ************************************************************************************************/

class PrivateMessageAdapter(
    options: FirestoreRecyclerOptions<Message>,
    private val onAuthorClickListener: OnAuthorClickListener? = null,
    private val onMessagesChangedListener: OnMessagesChangedListener? = null
)
    : FirestoreRecyclerAdapter<Message, PrivateMessageAdapter.PrivateMessageViewHolder>(options)
{

    private var recyclerView: RecyclerView? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrivateMessageViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemRecyclerViewPrivateMessageBinding =
            DataBindingUtil.inflate(inflater, R.layout.item_recycler_view_private_message, parent, false)
        return PrivateMessageViewHolder(binding, onAuthorClickListener)
    }

    override fun onBindViewHolder(holder: PrivateMessageViewHolder, position: Int, message: Message) {
        holder.bind(message)
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

    /***********************************Callbacks************************************************/

    interface OnMessagesChangedListener {
        fun onMessagesChanged()
    }

    interface OnAuthorClickListener {
        fun onAuthorClick(authorId: String)
    }

    /***********************************ViewHolder***********************************************/

    class PrivateMessageViewHolder(
        private val binding: ItemRecyclerViewPrivateMessageBinding,
        private val onAuthorClickListener: OnAuthorClickListener? = null,
    )
        : RecyclerView.ViewHolder(binding.root)
    {

        /**************************************Data**********************************************/

        private lateinit var message: Message
        private var messageAuthorIsCurrentUser = false

        /**************************************Init**********************************************/

        init {
            this.binding.privateMessageViewHolder = this
        }

        /********************************UI monitoring*******************************************/

        fun bind(message: Message) {
            this.message = message
            this.messageAuthorIsCurrentUser = this.message.authorId == CurrentHikerInfo.currentHiker?.id
            this.binding.message = this.message
            this.binding.messageAuthorIsCurrentUser = this.messageAuthorIsCurrentUser
        }

        @Suppress("UNUSED_PARAMETER")
        fun onAuthorPhotoImageClick(view: View) {
            this.onAuthorClickListener?.onAuthorClick(this.message.authorId)
        }
    }
}