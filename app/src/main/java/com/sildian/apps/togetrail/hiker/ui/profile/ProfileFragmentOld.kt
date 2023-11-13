package com.sildian.apps.togetrail.hiker.ui.profile

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.common.baseControllers.BaseFragment
import com.sildian.apps.togetrail.common.utils.cloudHelpers.DatabaseFirebaseHelper
import com.sildian.apps.togetrail.common.utils.uiHelpers.DialogHelper
import com.sildian.apps.togetrail.databinding.FragmentProfileOldBinding
import com.sildian.apps.togetrail.hiker.data.models.HikerHistoryItem
import com.sildian.apps.togetrail.hiker.data.models.HikerHistoryType
import com.sildian.apps.togetrail.hiker.data.source.HikerFirebaseQueries
import com.sildian.apps.togetrail.hiker.data.viewModels.HikerViewModel
import dagger.hilt.android.AndroidEntryPoint

/*************************************************************************************************
 * Shows a hiker's profile
 * @param hikerId : the hiker's id
 * @deprecated : Replaced by [com.sildian.apps.togetrail.uiLayer.hikerProfile.details.HikerProfileDetailsFragment]
 ************************************************************************************************/

@Deprecated("Replaced by [com.sildian.apps.togetrail.uiLayer.hikerProfile.details.HikerProfileDetailsFragment]")
@AndroidEntryPoint
class ProfileFragmentOld(private val hikerId: String? = null) :
    BaseFragment<FragmentProfileOldBinding>(),
    HikerHistoryAdapterOld.OnHikerHistoryItemClick
{

    /*****************************************Data***********************************************/

    private val hikerViewModel: HikerViewModel by viewModels()

    /**********************************UI component**********************************************/

    private lateinit var historyItemAdapter: HikerHistoryAdapterOld

    /***********************************Data monitoring******************************************/

    override fun initializeData() {
        this.binding.hikerViewModel = this.hikerViewModel
        observeHiker()
    }

    override fun loadData() {
        lifecycleScope.launchWhenStarted {
            loadHiker()
        }
    }

    private fun observeHiker() {
        this.hikerViewModel.data.observe(this) { hikerData ->
            hikerData?.error?.let { e ->
                onQueryError(e)
            } ?:
            hikerData?.data?.let { hiker ->
                refreshUI()
            } ?:
            showNoHikerDialog()
        }
    }

    private fun loadHiker() {
        this.hikerId?.let { hikerId ->
            this.hikerViewModel.loadHikerFlow(hikerId)
        }
    }

    /***********************************UI monitoring********************************************/

    override fun getLayoutId(): Int = R.layout.fragment_profile_old

    override fun initializeUI(){
        initializeToolbar()
    }

    override fun refreshUI(){
        updateHistoryItemsRecyclerView()
    }

    private fun initializeToolbar(){
        (activity as ProfileActivityOld).setSupportActionBar(this.binding.fragmentProfileToolbar)
        (activity as ProfileActivityOld).supportActionBar?.title = ""
        (activity as ProfileActivityOld).supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun updateHistoryItemsRecyclerView(){
        this.hikerViewModel.data.value?.data?.let { hiker ->
            this.historyItemAdapter = HikerHistoryAdapterOld(
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

    private fun showNoHikerDialog() {
        context?.let { context ->
            DialogHelper.createInfoDialog(context,
                R.string.message_query_answer_no_result_title,
                R.string.message_query_answer_no_result_message_hiker)
            {
                baseActivity?.finishCancel()
            }
                .show()
        }
    }

    /********************************History monitoring******************************************/

    override fun onHistoryItemClick(historyItem: HikerHistoryItem) {
        historyItem.itemId?.let { itemId ->
            when (historyItem.type) {
                HikerHistoryType.TRAIL_CREATED -> (activity as ProfileActivityOld).seeTrail(itemId)
                HikerHistoryType.EVENT_CREATED -> (activity as ProfileActivityOld).seeEvent(itemId)
                HikerHistoryType.EVENT_ATTENDED -> (activity as ProfileActivityOld).seeEvent(itemId)
                else -> { }
            }
        }
    }
}