package com.sildian.apps.togetrail.trail.others

import android.view.LayoutInflater
import android.view.ViewGroup
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.trail.model.core.Trail

/*************************************************************************************************
 * Displays a list of trails within a vertical RecyclerView
 ************************************************************************************************/

class TrailVerticalAdapter (
    options: FirestoreRecyclerOptions<Trail>,
    private val listener: TrailVerticalViewHolder.OnTrailClickListener?=null
):
    FirestoreRecyclerAdapter<Trail, TrailVerticalViewHolder>(options)
{

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrailVerticalViewHolder {
        val inflater= LayoutInflater.from(parent.context)
        val view=inflater.inflate(R.layout.item_recycler_view_trail_vertical, parent, false)
        return TrailVerticalViewHolder(view, this.listener)
    }

    override fun onBindViewHolder(holder: TrailVerticalViewHolder, position: Int, trail: Trail) {
        holder.updateUI(trail)
    }
}