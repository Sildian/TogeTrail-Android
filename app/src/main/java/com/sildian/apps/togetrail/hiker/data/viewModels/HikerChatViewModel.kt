package com.sildian.apps.togetrail.hiker.data.viewModels

import com.sildian.apps.togetrail.chat.data.models.Duo
import com.sildian.apps.togetrail.common.baseDataRequests.FirebaseQueryDataFlowRequest
import com.sildian.apps.togetrail.common.baseViewModels.ListDataViewModel
import com.sildian.apps.togetrail.hiker.data.source.HikerFirebaseQueries
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/*************************************************************************************************
 * This viewModel observes chat data related to a hiker
 ************************************************************************************************/

@HiltViewModel
class HikerChatViewModel @Inject constructor(): ListDataViewModel<Duo>(Duo::class.java) {

    fun loadChatsRealTime(hikerId: String) {
        loadDataRealTime(FirebaseQueryDataFlowRequest(this.dataModelClass, HikerFirebaseQueries.getChats(hikerId)))
    }
}