package com.sildian.apps.togetrail.trail.others

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.databinding.ItemRecyclerViewTrailHorizontalBinding
import com.sildian.apps.togetrail.trail.model.core.Trail

/*************************************************************************************************
 * Displays a list of trails within an horizontal RecyclerView (with offline data)
 ************************************************************************************************/

class TrailHorizontalAdapterOffline (
    private val trails:List<Trail>,
    private val onTrailClickListener: TrailHorizontalViewHolder.OnTrailClickListener? = null,
    private val isEditable:Boolean = false,
    private val onTrailRemovedListener: TrailHorizontalViewHolder.OnTrailRemovedListener? = null
)
    : RecyclerView.Adapter<TrailHorizontalViewHolder>()
{

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrailHorizontalViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemRecyclerViewTrailHorizontalBinding =
            DataBindingUtil.inflate(inflater, R.layout.item_recycler_view_trail_horizontal, parent, false)
        return TrailHorizontalViewHolder(binding, this.onTrailClickListener, this.isEditable, this.onTrailRemovedListener)
    }

    override fun getItemCount(): Int = this.trails.size

    override fun onBindViewHolder(holder: TrailHorizontalViewHolder, position: Int) {
        holder.bind(this.trails[position])
    }
}