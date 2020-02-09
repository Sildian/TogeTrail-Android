package com.sildian.apps.togetrail.trail.info

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.sildian.apps.togetrail.R
import kotlinx.android.synthetic.main.item_recycler_view_photo.view.*

/*************************************************************************************************
 * Displays a photo
 ************************************************************************************************/

class PhotoViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {

    /**********************************UI components*********************************************/

    private val photoImageView by lazy {itemView.item_recycler_view_photo_image_view}

    /************************************UI update***********************************************/

    /**
     * Updates UI
     * @param photoUrl : the photo url
     */

    fun updateUI(photoUrl:String){
        Glide.with(this.itemView)
            .load(photoUrl)
            .apply(RequestOptions.fitCenterTransform())
            .placeholder(R.drawable.ic_trail_black)
            .into(this.photoImageView)
    }
}