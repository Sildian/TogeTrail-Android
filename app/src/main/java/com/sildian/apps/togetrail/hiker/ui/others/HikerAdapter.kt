package com.sildian.apps.togetrail.hiker.ui.others

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.databinding.ItemRecyclerViewHikerBinding
import com.sildian.apps.togetrail.hiker.data.models.Hiker

/*************************************************************************************************
 * Displays hikers within a recyclerView
 ************************************************************************************************/

class HikerAdapter(
    options: FirestoreRecyclerOptions<Hiker>,
    private val onHikerClickListener: OnHikerClickListener? = null,
    private val onHikerMessageClickListener: OnHikerMessageClickListener? = null
):
    FirestoreRecyclerAdapter<Hiker, HikerAdapter.HikerViewHolder>(options)
{

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HikerViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemRecyclerViewHikerBinding =
            DataBindingUtil.inflate(inflater, R.layout.item_recycler_view_hiker, parent, false)
        return HikerViewHolder(binding, this.onHikerClickListener, this.onHikerMessageClickListener)
    }

    override fun onBindViewHolder(holder: HikerViewHolder, position: Int, hiker: Hiker) {
        holder.bind(hiker)
    }

    /***********************************Callbacks************************************************/

    interface OnHikerClickListener {
        fun onHikerClick(hiker: Hiker)
    }

    interface OnHikerMessageClickListener {
        fun onHikerMessageClick(hiker: Hiker)
    }

    /***********************************ViewHolder************************************************/

    class HikerViewHolder(
        private val binding: ItemRecyclerViewHikerBinding,
        private val onHikerClickListener: OnHikerClickListener? = null,
        private val onHikerMessageClickListener: OnHikerMessageClickListener? = null
    ):
        RecyclerView.ViewHolder(binding.root)
    {

        /**************************************Data***********************************************/

        private lateinit var hiker: Hiker

        /**************************************Init***********************************************/

        init {
            this.binding.hikerViewHolder = this
        }

        /********************************UI monitoring********************************************/

        fun bind(hiker: Hiker) {
            this.hiker = hiker
            this.binding.hiker = this.hiker
        }

        @Suppress("UNUSED_PARAMETER")
        fun onHikerClick(view: View) {
            this.onHikerClickListener?.onHikerClick(this.hiker)
        }

        @Suppress("UNUSED_PARAMETER")
        fun onHikerMessageButtonClick(view: View) {
            this.onHikerMessageClickListener?.onHikerMessageClick(this.hiker)
        }
    }
}