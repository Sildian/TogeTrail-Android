package com.sildian.apps.togetrail.hiker.profile

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.common.baseControllers.BaseFragment
import com.sildian.apps.togetrail.common.utils.cloudHelpers.DatabaseFirebaseHelper
import com.sildian.apps.togetrail.databinding.FragmentProfileBinding
import com.sildian.apps.togetrail.hiker.model.core.HikerHistoryItem
import com.sildian.apps.togetrail.hiker.model.core.HikerHistoryType
import com.sildian.apps.togetrail.hiker.model.dataRepository.HikerFirebaseQueries
import com.sildian.apps.togetrail.hiker.model.viewModels.HikerViewModel
import dagger.hilt.android.AndroidEntryPoint

/*************************************************************************************************
 * Shows a hiker's profile
 * @param hikerId : the hiker's id
 ************************************************************************************************/

@AndroidEntryPoint
class ProfileFragment(private val hikerId: String? = null) :
    BaseFragment<FragmentProfileBinding>(),
    HikerHistoryAdapter.OnHikerHistoryItemClick
{

    /*****************************************Data***********************************************/

    private val hikerViewModel: HikerViewModel by viewModels()

    /**********************************UI component**********************************************/

    private lateinit var historyItemAdapter: HikerHistoryAdapter

    /***********************************Data monitoring******************************************/

    override fun loadData() {
        initializeData()
        observeHiker()
        observeRequestFailure()
        loadHiker()
    }

    private fun initializeData() {
        this.binding.hikerViewModel = this.hikerViewModel
    }

    private fun observeHiker() {
        this.hikerViewModel.data.observe(this, { hiker ->
            if (hiker != null) {
                refreshUI()
            }
        })
    }

    private fun observeRequestFailure() {
        this.hikerViewModel.error.observe(this) { e ->
            if (e != null) {
                onQueryError(e)
            }
        }
    }

    private fun loadHiker() {
        this.hikerId?.let { hikerId ->
            this.hikerViewModel.loadHikerRealTime(hikerId)
        }
    }

    /***********************************UI monitoring********************************************/

    override fun getLayoutId(): Int = R.layout.fragment_profile

    override fun initializeUI(){
        initializeToolbar()
    }

    override fun refreshUI(){
        updateHistoryItemsRecyclerView()
    }

    private fun initializeToolbar(){
        (activity as ProfileActivity).setSupportActionBar(this.binding.fragmentProfileToolbar)
        (activity as ProfileActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun updateHistoryItemsRecyclerView(){
        this.hikerViewModel.data.value?.let { hiker ->
            this.historyItemAdapter = HikerHistoryAdapter(
                DatabaseFirebaseHelper.generateOptionsForAdapter(
                    HikerHistoryItem::class.java,
                    HikerFirebaseQueries.getLastHistoryItems(hiker.id),
                    activity as AppCompatActivity
                ),
                hiker.name.toString(),
                this
            )
            this.binding.fragmentProfileRecyclerViewHistoryItems.adapter = this.historyItemAdapter
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
