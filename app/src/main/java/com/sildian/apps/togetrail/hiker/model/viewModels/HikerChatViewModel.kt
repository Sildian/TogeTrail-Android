package com.sildian.apps.togetrail.hiker.model.viewModels

import com.sildian.apps.togetrail.chat.model.core.Duo
import com.sildian.apps.togetrail.common.baseViewModels.ListDataViewModel
import com.sildian.apps.togetrail.hiker.model.dataRepository.HikerFirebaseQueries
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/*************************************************************************************************
 * This viewModel observes chat data related to a hiker
 ************************************************************************************************/

@HiltViewModel
class HikerChatViewModel @Inject constructor(): ListDataViewModel<Duo>(Duo::class.java) {

    fun loadChatsRealTime(hikerId: String) {
        loadDataRealTime(HikerFirebaseQueries.getChats(hikerId))
    }
}