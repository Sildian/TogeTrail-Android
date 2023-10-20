package com.sildian.apps.togetrail.hiker.data.viewModels

import com.sildian.apps.togetrail.chat.data.models.Duo
import com.sildian.apps.togetrail.common.baseViewModels.ListDataViewModel
import com.sildian.apps.togetrail.common.utils.cloudHelpers.FirebaseQueryDataFlowRequest
import com.sildian.apps.togetrail.common.coroutines.CoroutineIODispatcher
import com.sildian.apps.togetrail.hiker.data.source.HikerFirebaseQueries
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

/*************************************************************************************************
 * This viewModel observes chat data related to a hiker
 ************************************************************************************************/

@HiltViewModel
class HikerChatViewModel @Inject constructor(@CoroutineIODispatcher dispatcher: CoroutineDispatcher): ListDataViewModel<Duo>(Duo::class.java, dispatcher) {

    fun loadChatsFlow(hikerId: String) {
        loadDataFlow(FirebaseQueryDataFlowRequest(this.dispatcher, this.dataModelClass, HikerFirebaseQueries.getChats(hikerId)))
    }
}