package com.sildian.apps.togetrail

import com.sildian.apps.togetrail.hiker.model.core.Hiker
import com.sildian.apps.togetrail.hiker.model.core.HikerHistoryItem
import com.sildian.apps.togetrail.hiker.model.support.HikerRepository
import org.robolectric.annotation.Implementation
import org.robolectric.annotation.Implements

/*************************************************************************************************
 * Shadow used to avoid requests to the server during data requester tests
 ************************************************************************************************/

@Implements(HikerRepository::class)
class HikerRepositoryShadow {

    @Implementation
    suspend fun getHiker(hikerId: String): Hiker? {
        println("FAKE HikerRepository : Get hiker")
        return BaseDataRequesterTest.getHikerSample()
    }

    @Implementation
    suspend fun updateHiker(hiker: Hiker) {
        println("FAKE HikerRepository : Update hiker")
        BaseDataRequesterTest.isHikerUpdated = true
    }

    @Implementation
    suspend fun deleteHiker(hiker: Hiker) {
        println("FAKE HikerRepository : Delete hiker")
        BaseDataRequesterTest.isHikerDeleted = true
    }

    @Implementation
    suspend fun addHikerHistoryItem(hikerId: String, historyItem: HikerHistoryItem) {
        println("FAKE HikerRepository : Add history item")
        BaseDataRequesterTest.isHistoryItemAdded = true
    }
}