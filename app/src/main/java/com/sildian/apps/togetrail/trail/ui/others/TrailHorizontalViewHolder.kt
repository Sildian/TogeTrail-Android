package com.sildian.apps.togetrail.trail.ui.others

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.sildian.apps.togetrail.databinding.ItemRecyclerViewTrailHorizontalBinding
import com.sildian.apps.togetrail.trail.data.core.Trail

/*************************************************************************************************
 * Displays a trail's main info within an horizontal RecyclerView
 ************************************************************************************************/

class TrailHorizontalViewHolder (
    private val binding: ItemRecyclerViewTrailHorizontalBinding,
    private val onTrailClickListener: OnTrailClickListener? = null,
    private val isEditable: Boolean = false,
    private val onTrailRemovedListener: OnTrailRemovedListener? = null
) :
    RecyclerView.ViewHolder(binding.root)
{

    /***********************************Callbacks************************************************/

    interface OnTrailClickListener {
        fun onTrailClick(trail:Trail)
    }

    interface OnTrailRemovedListener {
        fun onTrailRemoved(trail:Trail)
    }

    /**************************************Data**************************************************/

    private lateinit var trail: Trail

    /**************************************Init**************************************************/

    init {
        this.binding.trailHorizontalViewHolder = this
        this.binding.isEditable = this.isEditable
    }

    /***********************************UI monitoring********************************************/

    fun bind(trail:Trail) {
        this.trail = trail
        this.binding.trail = this.trail
    }

    @Suppress("UNUSED_PARAMETER")
    fun onTrailClick(view: View) {
        this.onTrailClickListener?.onTrailClick(this.trail)
    }

    @Suppress("UNUSED_PARAMETER")
    fun onTrailRemoveButtonClick(view: View) {
        this.onTrailRemovedListener?.onTrailRemoved(this.trail)
    }
}