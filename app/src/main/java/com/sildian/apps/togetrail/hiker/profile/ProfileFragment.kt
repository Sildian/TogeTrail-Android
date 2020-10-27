package com.sildian.apps.togetrail.hiker.profile

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.common.baseControllers.BaseFragment
import com.sildian.apps.togetrail.common.utils.cloudHelpers.DatabaseFirebaseHelper
import com.sildian.apps.togetrail.common.baseViewModels.ViewModelFactory
import com.sildian.apps.togetrail.databinding.FragmentProfileBinding
import com.sildian.apps.togetrail.hiker.model.core.HikerHistoryItem
import com.sildian.apps.togetrail.hiker.model.core.HikerHistoryType
import com.sildian.apps.togetrail.hiker.model.support.HikerFirebaseQueries
import com.sildian.apps.togetrail.hiker.model.support.HikerViewModel
import kotlinx.android.synthetic.main.fragment_profile.view.*

/*************************************************************************************************
 * Shows a hiker's profile
 * @param hikerId : the hiker's id
 ************************************************************************************************/

class ProfileFragment (private val hikerId: String?)
    : BaseFragment(),
    HikerHistoryViewHolder.OnHikerHistoryItemClick
{

    /*****************************************Data***********************************************/

    private lateinit var hikerViewModel:HikerViewModel

    /**********************************UI component**********************************************/

    private val toolbar by lazy {layout.fragment_profile_toolbar}
    private val photoImageView by lazy {layout.fragment_profile_image_view_photo}
    private val historyItemsRecyclerView by lazy {layout.fragment_profile_recycler_view_history_items}
    private lateinit var historyItemAdapter: HikerHistoryAdapter

    /***********************************Data monitoring******************************************/

    override fun loadData() {
        initializeData()
        observeHiker()
        observeRequestFailure()
        loadHiker()
    }

    private fun initializeData() {
        this.hikerViewModel = ViewModelProviders
            .of(this, ViewModelFactory)
            .get(HikerViewModel::class.java)
        this.binding.lifecycleOwner = this
        (this.binding as FragmentProfileBinding).hikerViewModel = this.hikerViewModel
    }

    private fun observeHiker() {
        this.hikerViewModel.hiker.observe(this, { hiker ->
            if (hiker != null) {
                refreshUI()
            }
        })
    }

    private fun observeRequestFailure() {
        this.hikerViewModel.requestFailure.observe(this) { e ->
            if (e != null) {
                onQueryError(e)
            }
        }
    }

    private fun loadHiker() {
        this.hikerId?.let { hikerId ->
            this.hikerViewModel.loadHikerFromDatabaseRealTime(hikerId)
        }
    }

    /***********************************UI monitoring********************************************/

    override fun getLayoutId(): Int = R.layout.fragment_profile

    override fun useDataBinding(): Boolean = true

    override fun initializeUI(){
        initializeToolbar()
    }

    override fun refreshUI(){
        updatePhotoImageView()
        updateHistoryItemsRecyclerView()
    }

    private fun initializeToolbar(){
        (activity as ProfileActivity).setSupportActionBar(this.toolbar)
        (activity as ProfileActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun updatePhotoImageView(){
        Glide.with(this)
            .load(this.hikerViewModel.hiker.value?.photoUrl)
            .apply(RequestOptions.circleCropTransform())
            .placeholder(R.drawable.ic_person_white)
            .into(this.photoImageView)
    }

    private fun updateHistoryItemsRecyclerView(){
        this.hikerViewModel.hiker.value?.let { hiker ->
            this.historyItemAdapter = HikerHistoryAdapter(
                DatabaseFirebaseHelper.generateOptionsForAdapter(
                    HikerHistoryItem::class.java,
                    HikerFirebaseQueries.getLastHistoryItems(hiker.id),
                    activity as AppCompatActivity
                ),
                hiker.name.toString(),
                this
            )
            this.historyItemsRecyclerView.adapter = this.historyItemAdapter
        }
    }

    /********************************History monitoring******************************************/

    override fun onHistoryItemClick(historyItem: HikerHistoryItem) {
        historyItem.itemId?.let { itemId ->
            when (historyItem.type) {
                HikerHistoryType.TRAIL_CREATED -> (activity as ProfileActivity).seeTrail(itemId)
                HikerHistoryType.EVENT_CREATED -> (activity as ProfileActivity).seeEvent(itemId)
                HikerHistoryType.EVENT_ATTENDED -> (activity as ProfileActivity).seeEvent(itemId)
                else -> { }
            }
        }
    }
}
