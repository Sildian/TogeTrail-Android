package com.sildian.apps.togetrail.chat.others

import android.view.Gravity
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.chat.model.core.Message
import com.sildian.apps.togetrail.hiker.model.support.CurrentHikerInfo
import kotlinx.android.synthetic.main.item_recycler_view_private_message.view.*

/*************************************************************************************************
 * Displays a message in a private chat space
 ************************************************************************************************/

class PrivateMessageViewHolder(
    itemView: View,
    private val onAuthorClickListener: OnAuthorClickListener? = null,
)
    : RecyclerView.ViewHolder(itemView)
{
    /***********************************Callbacks************************************************/

    interface OnAuthorClickListener {
        fun onAuthorClick(authorId: String)
    }

    /**************************************Data**************************************************/

    private lateinit var message: Message

    /**********************************UI components*********************************************/

    private val globalLayout by lazy { itemView.item_recycler_view_private_message_layout_global }
    private val authorPhotoImageView by lazy { itemView.item_recycler_view_private_message_image_author }
    private val textsLayout by lazy { itemView.item_recycler_view_private_message_layout_texts }
    private val authorNameAndDateText by lazy { itemView.item_recycler_view_private_message_text_author_name_and_date }
    private val messageText by lazy { itemView.item_recycler_view_private_message_text_message }

    /**************************************Init**************************************************/

    init {
        this.authorPhotoImageView.setOnClickListener { this.onAuthorClickListener?.onAuthorClick(this.message.authorId) }
    }

    /************************************UI update***********************************************/

    fun updateUI(message: Message) {
        this.message = message
        updateLayout()
        updateAuthorPhotoImageView()
        updateTexts()
    }

    private fun updateLayout() {
        if (this.message.authorId == CurrentHikerInfo.currentHiker?.id) {
            this.globalLayout.gravity = Gravity.END
            this.textsLayout.setBackgroundResource(R.drawable.shape_corners_round_color_secondary_ultra_light)
        }
        else {
            this.globalLayout.gravity = Gravity.START
            this.textsLayout.setBackgroundResource(R.drawable.shape_corners_round_color_gray_ultra_light)
        }
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