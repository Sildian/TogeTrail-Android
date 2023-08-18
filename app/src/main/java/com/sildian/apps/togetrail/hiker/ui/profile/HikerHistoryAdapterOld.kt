package com.sildian.apps.togetrail.hiker.ui.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.databinding.ItemRecyclerViewHikerHistoryOldBinding
import com.sildian.apps.togetrail.hiker.data.models.HikerHistoryItem

/*************************************************************************************************
 * Displays an hiker's history
 * @deprecated : Replaced by [com.sildian.apps.togetrail.uiLayer.hikerProfile.details.HikerHistoryItemAdapter]
 ************************************************************************************************/

@Deprecated("Replaced by [com.sildian.apps.togetrail.uiLayer.hikerProfile.details.HikerHistoryItemAdapter]")
class HikerHistoryAdapterOld (
    options:FirestoreRecyclerOptions<HikerHistoryItem>,
    private val hikerName: String,
    private val listener: OnHikerHistoryItemClick? = null
)
    : FirestoreRecyclerAdapter<HikerHistoryItem, HikerHistoryAdapterOld.HikerHistoryViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HikerHistoryViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemRecyclerViewHikerHistoryOldBinding =
            DataBindingUtil.inflate(inflater, R.layout.item_recycler_view_hiker_history_old, parent, false)
        return HikerHistoryViewHolder(binding, this.listener)
    }

    override fun onBindViewHolder(holder: HikerHistoryViewHolder, position: Int, historyItem: HikerHistoryItem) {
        holder.bind(this.hikerName, historyItem)
    }

    /***********************************Callbacks************************************************/

    interface OnHikerHistoryItemClick {
        fun onHistoryItemClick(historyItem:HikerHistoryItem)
    }

    /***********************************ViewHolders************************************************/

    class HikerHistoryViewHolder (
        private val binding: ItemRecyclerViewHikerHistoryOldBinding,
        private val listener: OnHikerHistoryItemClick? = null
    )
        : RecyclerView.ViewHolder(binding.root) {

        /**************************************Data***********************************************/

        private lateinit var hikerName: String
        private lateinit var historyItem: HikerHistoryItem

        /**************************************Init***********************************************/

        init {
            this.binding.hikerHistoryViewHolder = this
        }

        /********************************UI monitoring********************************************/

        fun bind(hikerName: String, historyItem: HikerHistoryItem){
            this.hikerName = hikerName
            this.historyItem = historyItem
            this.binding.hikerName = this.hikerName
            this.binding.historyItem = this.historyItem
        }

        @Suppress("UNUSED_PARAMETER")
        fun onHistoryItemClick(view: View) {
            this.listener?.onHistoryItemClick(this.historyItem)
        }
    }
}