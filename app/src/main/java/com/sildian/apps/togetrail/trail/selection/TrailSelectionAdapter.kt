package com.sildian.apps.togetrail.trail.selection

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.common.utils.MetricsHelper
import com.sildian.apps.togetrail.databinding.ItemRecyclerViewTrailSelectionBinding
import com.sildian.apps.togetrail.trail.model.core.Trail
import com.sildian.apps.togetrail.trail.model.core.TrailLevel
import kotlinx.android.synthetic.main.item_recycler_view_trail_selection.view.*

/*************************************************************************************************
 * Displays a list of trails with a purpose to select some of them
 ************************************************************************************************/

class TrailSelectionAdapter (
    options: FirestoreRecyclerOptions<Trail>,
    private val selectedTrails: List<Trail>,
    private val onTrailSelectListener: OnTrailSelectListener,
    private val onTrailClickListener: OnTrailClickListener? = null
):
    FirestoreRecyclerAdapter<Trail, TrailSelectionAdapter.TrailSelectionViewHolder>(options)
{

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrailSelectionViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemRecyclerViewTrailSelectionBinding =
            DataBindingUtil.inflate(inflater, R.layout.item_recycler_view_trail_selection, parent, false)
        return TrailSelectionViewHolder(binding, this.onTrailSelectListener, this.onTrailClickListener)
    }

    override fun onBindViewHolder(holder: TrailSelectionViewHolder, position: Int, trail: Trail) {
        val isSelected = this.selectedTrails.firstOrNull { it.id == trail.id } != null
        holder.bind(trail, isSelected)
    }

    /***********************************Callbacks************************************************/

    interface OnTrailSelectListener {
        fun onTrailSelected(trail: Trail)
        fun onTrailUnSelected(trail: Trail)
    }

    interface OnTrailClickListener {
        fun onTrailClick(trail: Trail)
    }

    /***********************************ViewHolder************************************************/

    class TrailSelectionViewHolder (
        private val binding: ItemRecyclerViewTrailSelectionBinding,
        private val onTrailSelectListener: OnTrailSelectListener,
        private val onTrailClickListener: OnTrailClickListener? = null
    )
        : RecyclerView.ViewHolder(binding.root)
    {

        /**************************************Data**********************************************/

        private lateinit var trail: Trail
        private var isSelected = false

        /**************************************Init**********************************************/

        init {
            this.binding.trailSelectionViewHolder = this
        }

        /***********************************UI Monitoring*****************************************/

        fun bind(trail:Trail, isSelected:Boolean) {
            this.trail = trail
            this.isSelected = isSelected
            this.binding.trail = this.trail
            this.binding.isSelected = this.isSelected
        }

        @Suppress("UNUSED_PARAMETER")
        fun onTrailClick(view: View) {
            this.onTrailClickListener?.onTrailClick(this.trail)
        }

        @Suppress("UNUSED_PARAMETER")
        fun onTrailCheckedChanged(view: CompoundButton, isChecked: Boolean) {
            when (isChecked) {
                true -> this.onTrailSelectListener.onTrailSelected(this.trail)
                false -> this.onTrailSelectListener.onTrailUnSelected(this.trail)
            }
        }
    }
}