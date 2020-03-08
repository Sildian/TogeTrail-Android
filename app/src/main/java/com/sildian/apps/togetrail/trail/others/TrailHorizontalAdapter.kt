package com.sildian.apps.togetrail.trail.others

import android.view.LayoutInflater
import android.view.ViewGroup
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.trail.model.core.Trail

/*************************************************************************************************
 * Displays a list of trails within an horizontal RecyclerView
 ************************************************************************************************/

class TrailHorizontalAdapter(
    options:FirestoreRecyclerOptions<Trail>,
    private val listener: TrailHorizontalViewHolder.OnTrailClickListener?=null
):
    FirestoreRecyclerAdapter<Trail, TrailHorizontalViewHolder> (options)
{

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrailHorizontalViewHolder {
        val inflater=LayoutInflater.from(parent.context)
        val view=inflater.inflate(R.layout.item_recycler_view_trail_horizontal, parent, false)
        return TrailHorizontalViewHolder(view, this.listener)
    }

    override fun onBindViewHolder(holder: TrailHorizontalViewHolder, position: Int, trail: Trail) {
        holder.updateUI(trail)
    }
}