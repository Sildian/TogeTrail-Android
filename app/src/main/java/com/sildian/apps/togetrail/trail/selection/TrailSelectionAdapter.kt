package com.sildian.apps.togetrail.trail.selection

import android.view.LayoutInflater
import android.view.ViewGroup
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.trail.model.core.Trail

/*************************************************************************************************
 * Displays a list of trails with a purpose to select some of them
 ************************************************************************************************/

class TrailSelectionAdapter (
    options: FirestoreRecyclerOptions<Trail>,
    private val selectedTrails:List<Trail>,
    private val onTrailSelectListener: TrailSelectionViewHolder.OnTrailSelectListener,
    private val onTrailClickListener: TrailSelectionViewHolder.OnTrailClickListener?=null
):
    FirestoreRecyclerAdapter<Trail, TrailSelectionViewHolder>(options)
{

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrailSelectionViewHolder {
        val inflater= LayoutInflater.from(parent.context)
        val view=inflater.inflate(R.layout.item_recycler_view_trail_selection, parent, false)
        return TrailSelectionViewHolder(view, this.onTrailSelectListener, this.onTrailClickListener)
    }

    override fun onBindViewHolder(holder: TrailSelectionViewHolder, position: Int, trail: Trail) {
        val isSelected=this.selectedTrails.firstOrNull { it.id==trail.id }!=null
        holder.updateUI(trail, isSelected)
    }
}