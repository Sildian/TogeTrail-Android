package com.sildian.apps.togetrail.common.baseViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sildian.apps.togetrail.event.model.viewModels.EventViewModel
import com.sildian.apps.togetrail.event.model.viewModels.EventsViewModel
import com.sildian.apps.togetrail.hiker.model.viewModels.HikerChatViewModel
import com.sildian.apps.togetrail.hiker.model.viewModels.HikerViewModel
import com.sildian.apps.togetrail.trail.model.viewModels.HikerLikedTrailsViewModel
import com.sildian.apps.togetrail.trail.model.viewModels.HikerMarkedTrailsViewModel
import com.sildian.apps.togetrail.trail.model.viewModels.TrailViewModel
import com.sildian.apps.togetrail.trail.model.viewModels.TrailsViewModel

/*************************************************************************************************
 * Factory allowing to create ViewModel classes
 ************************************************************************************************/

object ViewModelFactory : ViewModelProvider.Factory{

    /**Messages**/

    private const val EXCEPTION_MESSAGE_UNKNOWN_VIEWMODEL="Unknown ViewModel class"

    /**
     * Creates a ViewModel class
     * @param modelClass : the ViewModel class type to create
     * @return the viewModel class
     */

    @Throws(IllegalArgumentException::class)
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(HikerViewModel::class.java) -> HikerViewModel() as T
            modelClass.isAssignableFrom(TrailsViewModel::class.java) -> TrailsViewModel() as T
            modelClass.isAssignableFrom(TrailViewModel::class.java) -> TrailViewModel() as T
            modelClass.isAssignableFrom(EventsViewModel::class.java) -> EventsViewModel() as T
            modelClass.isAssignableFrom(EventViewModel::class.java) -> EventViewModel() as T
            modelClass.isAssignableFrom(HikerChatViewModel::class.java) -> HikerChatViewModel() as T
            modelClass.isAssignableFrom(HikerLikedTrailsViewModel::class.java) -> HikerLikedTrailsViewModel() as T
            modelClass.isAssignableFrom(HikerMarkedTrailsViewModel::class.java) -> HikerMarkedTrailsViewModel() as T
            else -> throw IllegalArgumentException(EXCEPTION_MESSAGE_UNKNOWN_VIEWMODEL)
        }
    }
}