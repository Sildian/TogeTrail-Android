package com.sildian.apps.togetrail.trail.info

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sildian.apps.togetrail.R

/*************************************************************************************************
 * Displays a set of photos
 * @param photosUrl : the list of photos url to display
 ************************************************************************************************/

class PhotoAdapter (val photosUrl:List<String>) : RecyclerView.Adapter<PhotoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val inflater=LayoutInflater.from(parent.context)
        val view=inflater.inflate(R.layout.item_recycler_view_photo, parent, false)
        return PhotoViewHolder(view)
    }

    override fun getItemCount(): Int {
        return this.photosUrl.size
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        holder.updateUI(this.photosUrl[position])
    }
}