package com.sildian.apps.togetrail.uiLayer.hikerProfile.details

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.databinding.ItemRecyclerViewHikerHistoryItemBinding
import com.sildian.apps.togetrail.uiLayer.entities.hiker.HikerHistoryItemUI

class HikerHistoryItemAdapter (
    private val historyItems: List<HikerHistoryItemUI>,
    private val onHistoryItemClick: (HikerHistoryItemUI) -> Unit,
) : RecyclerView.Adapter<HikerHistoryItemAdapter.HikerHistoryItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HikerHistoryItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemRecyclerViewHikerHistoryItemBinding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.item_recycler_view_hiker_history_item, parent,
                false,
            )
        return HikerHistoryItemViewHolder(binding, onHistoryItemClick)
    }

    override fun onBindViewHolder(holder: HikerHistoryItemViewHolder, position: Int) {
        holder.bind(historyItems[position])
    }

    override fun getItemCount(): Int =
        historyItems.size

    class HikerHistoryItemViewHolder (
        private val binding: ItemRecyclerViewHikerHistoryItemBinding,
        private val onHistoryItemClick: (HikerHistoryItemUI) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {

        private lateinit var historyItem: HikerHistoryItemUI

        init {
            binding.historyItemViewHolder = this
        }

        fun bind(historyItem: HikerHistoryItemUI) {
            this.historyItem = historyItem
            this.binding.historyItem = this.historyItem
        }

        @Suppress("UNUSED_PARAMETER")
        fun onHistoryItemClick(view: View) {
            onHistoryItemClick(historyItem)
        }
    }
}