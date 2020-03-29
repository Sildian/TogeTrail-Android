package com.sildian.apps.togetrail.trail.others

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
    private val listener: TrailSelectionViewHolder.OnTrailClickListener?=null
):
    FirestoreRecyclerAdapter<Trail, TrailSelectionViewHolder>(options)
{

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrailSelectionViewHolder {
        val inflater= LayoutInflater.from(parent.context)
        val view=inflater.inflate(R.layout.item_recycler_view_trail_selection, parent, false)
        return TrailSelectionViewHolder(view, this.listener)
    }

    override fun onBindViewHolder(holder: TrailSelectionViewHolder, position: Int, trail: Trail) {
        holder.updateUI(trail)
    }
}