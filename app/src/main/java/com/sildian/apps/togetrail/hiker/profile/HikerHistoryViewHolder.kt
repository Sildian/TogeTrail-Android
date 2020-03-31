package com.sildian.apps.togetrail.hiker.profile

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.common.utils.DateUtilities
import com.sildian.apps.togetrail.hiker.model.core.HikerHistoryItem
import kotlinx.android.synthetic.main.item_recycler_view_hiker_history.view.*

/*************************************************************************************************
 * Displays an hiker's history item
 ************************************************************************************************/

class HikerHistoryViewHolder (
    itemView:View,
    private val listener:OnHikerHistoryItemClick?=null
)
    : RecyclerView.ViewHolder(itemView) {

    /***********************************Callbacks************************************************/

    interface OnHikerHistoryItemClick{
        fun onHistoryItemClick(historyItem:HikerHistoryItem)
    }

    /**************************************Data**************************************************/

    private lateinit var hikerName:String                   //The updated hiker's name
    private lateinit var historyItem:HikerHistoryItem       //The related item

    /**********************************UI components*********************************************/

    private val image by lazy {itemView.item_recycler_view_hiker_history_image}
    private val nameText by lazy {itemView.item_recycler_view_hiker_history_text_name}
    private val dateText by lazy {itemView.item_recycler_view_hiker_history_text_date}

    /**************************************Init**************************************************/

    init{
        this.itemView.setOnClickListener { this.listener?.onHistoryItemClick(this.historyItem) }
    }

    /********************************UI monitoring**********************************************/

    fun updateUI(hikerName:String, historyItem:HikerHistoryItem){
        this.hikerName=hikerName
        this.historyItem=historyItem
        updateImage()
        updateNameText()
        updateDateText()
    }

    private fun updateImage(){
        if(this.historyItem.photoUrl!=null) {
            Glide.with(this.itemView)
                .load(this.historyItem.photoUrl)
                .apply(RequestOptions.centerCropTransform())
                .placeholder(R.drawable.ic_trail_black)
                .into(this.image)
        }else{
            this.image.setImageResource(R.drawable.ic_trail_black)
        }
    }

    private fun updateNameText(){
        this.nameText.text=this.historyItem.writeItemHistory(this.itemView.context, this.hikerName)
    }

    private fun updateDateText(){
        this.dateText.text=DateUtilities.displayDateShort(this.historyItem.date)
    }
}