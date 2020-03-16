package com.sildian.apps.togetrail.hiker.others

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.hiker.model.core.Hiker
import kotlinx.android.synthetic.main.item_recycler_view_hiker_photo.view.*

/*************************************************************************************************
 * Displays a hiker's photo within a recyclerView
 ************************************************************************************************/

class HikerPhotoViewHolder(
    itemView:View,
    private val onHikerClickListener:OnHikerClickListener?=null
)
    : RecyclerView.ViewHolder(itemView)
{

    /***********************************Callbacks************************************************/

    interface OnHikerClickListener{
        fun onHikerClick(hiker: Hiker)
    }

    /**************************************Data**************************************************/

    private lateinit var hiker:Hiker                    //The related hiker

    /**********************************UI components*********************************************/

    private val photoImageView by lazy {itemView.item_recycler_view_hiker_photo_image_view}

    /**************************************Init**************************************************/

    init{
        this.photoImageView.setOnClickListener { this.onHikerClickListener?.onHikerClick(this.hiker) }
    }

    /************************************UI update***********************************************/

    fun updateUI(hiker:Hiker){
        this.hiker=hiker
        updatePhotoImageView()
    }

    private fun updatePhotoImageView(){
        if(this.hiker.photoUrl!=null){
            Glide.with(this.itemView)
                .load(this.hiker.photoUrl)
                .apply(RequestOptions.circleCropTransform())
                .placeholder(R.drawable.ic_person_black)
                .into(this.photoImageView)
        }
    }
}