package com.sildian.apps.togetrail.uiLayer.hikerProfile

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sildian.apps.togetrail.uiLayer.entities.hiker.HikerHistoryItemUI
import com.sildian.apps.togetrail.uiLayer.hikerProfile.details.HikerHistoryItemAdapter

object HikerProfileDataBindingAdapters {

    @JvmStatic
    @BindingAdapter("items", "onItemClick")
    fun bindHikerHistoryItemsAdapter(
        recyclerView: RecyclerView,
        items: List<HikerHistoryItemUI>,
        hikerHistoryItemClickListener: HikerHistoryItemClickListener,
    ) {
        recyclerView.adapter = HikerHistoryItemAdapter(
            historyItems = items,
            onHistoryItemClick = { item ->
                hikerHistoryItemClickListener.onHikerHistoryItemClick(item = item)
            },
        )
    }

    interface HikerHistoryItemClickListener {
        fun onHikerHistoryItemClick(item: HikerHistoryItemUI)
    }
}