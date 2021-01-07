package com.sildian.apps.togetrail.chat.others

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.chat.model.core.Message
import kotlinx.android.synthetic.main.item_recycler_view_multi_users_message.view.*

/*************************************************************************************************
 * Displays a message in a multi users chat space
 ************************************************************************************************/

class MultiUsersMessageViewHolder(
    itemView: View,
    private val onAuthorClickListener: OnAuthorClickListener? = null
)
    : RecyclerView.ViewHolder(itemView)
{
    /***********************************Callbacks************************************************/

    interface OnAuthorClickListener{
        fun onAuthorClick(authorId: String)
    }

    /**************************************Data**************************************************/

    private lateinit var message: Message

    /**********************************UI components*********************************************/

    private val authorPhotoImageView by lazy { itemView.item_recycler_view_multi_users_message_image_author }
    private val authorNameAndDateText by lazy { itemView.item_recycler_view_multi_users_message_text_author_name_and_date }
    private val messageText by lazy { itemView.item_recycler_view_multi_users_message_text_message }

    /**************************************Init**************************************************/

    init {
        this.authorPhotoImageView.setOnClickListener { this.onAuthorClickListener?.onAuthorClick(this.message.authorId) }
    }

    /************************************UI update***********************************************/

    fun updateUI(message: Message) {
        this.message = message
        updateAuthorPhotoImageView()
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

    private fun updateTexts() {
        this.authorNameAndDateText.text = this.message.writeAuthorNameAndDate()
        this.messageText.text = this.message.toString()
    }
}