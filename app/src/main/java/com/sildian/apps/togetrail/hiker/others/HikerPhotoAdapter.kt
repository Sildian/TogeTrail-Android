package com.sildian.apps.togetrail.hiker.others

import android.view.LayoutInflater
import android.view.ViewGroup
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.hiker.model.core.Hiker

/*************************************************************************************************
 * Displays hikers's photo within a recyclerView
 ************************************************************************************************/

class HikerPhotoAdapter(
    options:FirestoreRecyclerOptions<Hiker>,
    private val onHikerClickListener:HikerPhotoViewHolder.OnHikerClickListener?=null,
    private val onHikersChangedListener: OnHikersChangedListener?=null
)
    :FirestoreRecyclerAdapter<Hiker, HikerPhotoViewHolder> (options)
{

    interface OnHikersChangedListener{
        fun onHikersChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HikerPhotoViewHolder {
        val inflater= LayoutInflater.from(parent.context)
        val view=inflater.inflate(R.layout.item_recycler_view_hiker_photo, parent, false)
        return HikerPhotoViewHolder(view, this.onHikerClickListener)
    }

    override fun onBindViewHolder(holder: HikerPhotoViewHolder, position: Int, hiker: Hiker) {
        holder.updateUI(hiker)
    }

    override fun onDataChanged() {
        this.onHikersChangedListener?.onHikersChanged()
    }
}