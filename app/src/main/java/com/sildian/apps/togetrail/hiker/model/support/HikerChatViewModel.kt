package com.sildian.apps.togetrail.hiker.model.support

import com.sildian.apps.togetrail.chat.model.core.Duo
import com.sildian.apps.togetrail.common.baseViewModels.ListDataViewModel

/*************************************************************************************************
 * This viewModel observes chat data related to a hiker
 ************************************************************************************************/

class HikerChatViewModel: ListDataViewModel<Duo>(Duo::class.java) {

    fun loadChatsRealTime(hikerId: String) {
        loadDataRealTime(HikerFirebaseQueries.getChats(hikerId))
    }
}