package com.sildian.apps.togetrail.hiker.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.hiker.model.core.HikerHistoryItem

/*************************************************************************************************
 * Displays an hiker's history
 ************************************************************************************************/

class HikerHistoryAdapter (
    options:FirestoreRecyclerOptions<HikerHistoryItem>,
    private val hikerName:String,
    private val listener:HikerHistoryViewHolder.OnHikerHistoryItemClick?=null
)
    : FirestoreRecyclerAdapter<HikerHistoryItem, HikerHistoryViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HikerHistoryViewHolder {
        val inflater= LayoutInflater.from(parent.context)
        val view=inflater.inflate(R.layout.item_recycler_view_hiker_history, parent, false)
        return HikerHistoryViewHolder(view, this.listener)
    }

    override fun onBindViewHolder(holder: HikerHistoryViewHolder, position: Int, historyItem: HikerHistoryItem) {
        holder.updateUI(this.hikerName, historyItem)
    }
}