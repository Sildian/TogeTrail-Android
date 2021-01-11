package com.sildian.apps.togetrail.chat.others

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.chat.model.core.Message
import com.sildian.apps.togetrail.hiker.model.support.CurrentHikerInfo
import kotlinx.android.synthetic.main.item_recycler_view_message.view.*

/*************************************************************************************************
 * Displays a message chat space
 ************************************************************************************************/

class MessageViewHolder(
    itemView: View,
    private val onAuthorClickListener: OnAuthorClickListener? = null,
    private val onMessageModificationClickListener: OnMessageModificationClickListener? = null
)
    : RecyclerView.ViewHolder(itemView)
{
    /***********************************Callbacks************************************************/

    interface OnAuthorClickListener {
        fun onAuthorClick(authorId: String)
    }

    interface OnMessageModificationClickListener {
        fun onMessageEditClick(message: Message)
        fun onMessageDeleteClick(message: Message)
    }

    /**************************************Data**************************************************/

    private lateinit var message: Message

    /**********************************UI components*********************************************/

    private val authorPhotoImageView by lazy { itemView.item_recycler_view_message_image_author }
    private val authorNameAndDateText by lazy { itemView.item_recycler_view_message_text_author_name_and_date }
    private val editButton by lazy { itemView.item_recycler_view_message_button_edit }
    private val deleteButton by lazy { itemView.item_recycler_view_message_button_delete }
    private val messageText by lazy { itemView.item_recycler_view_message_text_message }

    /**************************************Init**************************************************/

    init {
        this.authorPhotoImageView.setOnClickListener { this.onAuthorClickListener?.onAuthorClick(this.message.authorId) }
        this.editButton.setOnClickListener { this.onMessageModificationClickListener?.onMessageEditClick(this.message) }
        this.deleteButton.setOnClickListener { this.onMessageModificationClickListener?.onMessageDeleteClick(this.message) }
    }

    /************************************UI update***********************************************/

    fun updateUI(message: Message) {
        this.message = message
        updateAuthorPhotoImageView()
        updateModificationButtonsVisibility()
        updateTexts()
    }

    private fun updateAuthorPhotoImageView(){
        if (this.message.authorPhotoUrl != null) {
            Glide.with(this.itemView)
                .load(this.message.authorPhotoUrl)
                .apply(RequestOptions.circleCropTransform())
                .placeholder(R.drawable.ic_person_black)
                .into(this.authorPhotoImageView)
        } else {
            this.authorPhotoImageView.setImageResource(R.drawable.ic_person_black)
        }
    }

    private fun updateModificationButtonsVisibility() {
        val visibility = if (this.message.authorId == CurrentHikerInfo.currentHiker?.id) {
            View.VISIBLE
        } else {
            View.GONE
        }
        this.editButton.visibility = visibility
        this.deleteButton.visibility = visibility
    }

    private fun updateTexts() {
        this.authorNameAndDateText.text = this.message.writeAuthorNameAndDate()
        this.messageText.text = this.message.toString()
    }
}