package com.sildian.apps.togetrail.chat.others

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.chat.model.core.Message
import com.sildian.apps.togetrail.databinding.ItemRecyclerViewPublicMessageBinding
import com.sildian.apps.togetrail.hiker.model.support.CurrentHikerInfo

/*************************************************************************************************
 * Displays messages in a public chat space
 ************************************************************************************************/

class PublicMessageAdapter(
    options: FirestoreRecyclerOptions<Message>,
    private val onAuthorClickListener: OnAuthorClickListener? = null,
    private val onMessageModificationClickListener: OnMessageModificationClickListener? = null,
    private val onMessagesChangedListener: OnMessagesChangedListener? = null
)
    : FirestoreRecyclerAdapter<Message, PublicMessageAdapter.PublicMessageViewHolder>(options)
{

    private var recyclerView: RecyclerView? = null

    interface OnMessagesChangedListener {
        fun onMessagesChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PublicMessageViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemRecyclerViewPublicMessageBinding =
            DataBindingUtil.inflate(inflater, R.layout.item_recycler_view_public_message, parent, false)
        return PublicMessageViewHolder(binding, onAuthorClickListener, onMessageModificationClickListener)
    }

    override fun onBindViewHolder(holder: PublicMessageViewHolder, position: Int, message: Message) {
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

    interface OnAuthorClickListener {
        fun onAuthorClick(authorId: String)
    }

    interface OnMessageModificationClickListener {
        fun onMessageEditClick(message: Message)
        fun onMessageDeleteClick(message: Message)
    }

    /***********************************ViewHolder************************************************/

    class PublicMessageViewHolder(
        private val binding: ItemRecyclerViewPublicMessageBinding,
        private val onAuthorClickListener: OnAuthorClickListener? = null,
        private val onMessageModificationClickListener: OnMessageModificationClickListener? = null
    )
        : RecyclerView.ViewHolder(binding.root)
    {

        /**************************************Data***********************************************/

        private lateinit var message: Message
        private var messageAuthorIsCurrentUser = false

        /**************************************Init***********************************************/

        init {
            this.binding.publicMessageViewHolder = this
        }

        /**********************************UI monitoring******************************************/

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

        @Suppress("UNUSED_PARAMETER")
        fun onEditButtonClick(view: View) {
            this.onMessageModificationClickListener?.onMessageEditClick(this.message)
        }

        @Suppress("UNUSED_PARAMETER")
        fun onDeleteButtonClick(view: View) {
            this.onMessageModificationClickListener?.onMessageDeleteClick(this.message)
        }
    }
}