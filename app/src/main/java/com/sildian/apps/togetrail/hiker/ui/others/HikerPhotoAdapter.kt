package com.sildian.apps.togetrail.hiker.ui.others

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.databinding.ItemRecyclerViewHikerPhotoBinding
import com.sildian.apps.togetrail.hiker.data.models.Hiker

/*************************************************************************************************
 * Displays hikers's photo within a recyclerView
 ************************************************************************************************/

class HikerPhotoAdapter(
    options:FirestoreRecyclerOptions<Hiker>,
    private val onHikerClickListener: OnHikerClickListener? = null,
    private val onHikersChangedListener: OnHikersChangedListener? = null
)
    : FirestoreRecyclerAdapter<Hiker, HikerPhotoAdapter.HikerPhotoViewHolder> (options)
{

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HikerPhotoViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemRecyclerViewHikerPhotoBinding =
            DataBindingUtil.inflate(inflater, R.layout.item_recycler_view_hiker_photo, parent, false)
        return HikerPhotoViewHolder(binding, this.onHikerClickListener)
    }

    override fun onBindViewHolder(holder: HikerPhotoViewHolder, position: Int, hiker: Hiker) {
        holder.bind(hiker)
    }

    override fun onDataChanged() {
        this.onHikersChangedListener?.onHikersChanged()
    }

    /***********************************Callbacks************************************************/

    interface OnHikersChangedListener {
        fun onHikersChanged()
    }

    interface OnHikerClickListener {
        fun onHikerClick(hiker: Hiker)
    }

    /***********************************ViewHolder************************************************/

    class HikerPhotoViewHolder(
        private val binding: ItemRecyclerViewHikerPhotoBinding,
        private val onHikerClickListener:OnHikerClickListener? = null
    )
        : RecyclerView.ViewHolder(binding.root)
    {

        /**************************************Data***********************************************/

        private lateinit var hiker: Hiker

        /**************************************Init***********************************************/

        init {
            this.binding.hikerPhotoViewHolder = this
        }

        /********************************UI monitoring********************************************/

        fun bind(hiker:Hiker) {
            this.hiker = hiker
            this.binding.hiker = this.hiker
        }

        @Suppress("UNUSED_PARAMETER")
        fun onHikerPhotoImageClick(view: View) {
            this.onHikerClickListener?.onHikerClick(this.hiker)
        }
    }
}