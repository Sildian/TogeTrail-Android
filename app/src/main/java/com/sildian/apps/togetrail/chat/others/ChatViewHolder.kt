package com.sildian.apps.togetrail.chat.others

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.chat.model.core.Duo
import com.sildian.apps.togetrail.common.utils.DateUtilities
import kotlinx.android.synthetic.main.item_recycler_view_chat.view.*

/*************************************************************************************************
 * Displays a chat
 ************************************************************************************************/

class ChatViewHolder(
    itemView: View,
    private val onChatClickListener: OnChatClickListener? = null
)
    : RecyclerView.ViewHolder(itemView)
{

    /***********************************Callbacks************************************************/

    interface OnChatClickListener {
        fun onChatClick(chat: Duo)
    }

    /**************************************Data**************************************************/

    private lateinit var chat: Duo

    /**********************************UI components*********************************************/

    private val image by lazy { itemView.item_recycler_view_chat_image }
    private val nameText by lazy { itemView.item_recycler_view_chat_text_name }
    private val lastMessageText by lazy { itemView.item_recycler_view_chat_text_last_message }
    private val lastMessageDateText by lazy { itemView.item_recycler_view_chat_text_last_message_date }

    /**************************************Init**************************************************/

    init {
        this.itemView.setOnClickListener { this.onChatClickListener?.onChatClick(this.chat) }
    }

    /************************************UI update***********************************************/

    fun updateUI(chat: Duo) {
        this.chat = chat
        updateImage()
        updateTexts()
    }

    private fun updateImage(){
        if (this.chat.interlocutorPhotoUrl != null) {
            Glide.with(this.itemView)
                .load(this.chat.interlocutorPhotoUrl)
                .apply(RequestOptions.circleCropTransform())
                .placeholder(R.drawable.ic_person_black)
                .into(this.image)
        } else {
            this.image.setImageResource(R.drawable.ic_person_black)
        }
    }

    private fun updateTexts() {
        this.nameText.text = this.chat.interlocutorName?:""
        this.lastMessageText.text = this.chat.lastMessage?.text?:""
        this.lastMessageDateText.text = if (this.chat.lastMessage != null)
            DateUtilities.displayDateAndTimeRelative(this.chat.lastMessage!!.date)?:""
        else
            ""
    }
}