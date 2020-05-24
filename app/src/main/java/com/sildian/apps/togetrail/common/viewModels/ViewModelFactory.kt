package com.sildian.apps.togetrail.common.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sildian.apps.togetrail.hiker.model.support.HikerViewModel

/*************************************************************************************************
 * Factory allowing to create ViewModel classes
 ************************************************************************************************/

object ViewModelFactory : ViewModelProvider.Factory{

    /**
     * Creates a ViewModel class
     * @param modelClass : the ViewModel class type to create
     * @return the viewModel class
     */

    @Throws(IllegalArgumentException::class)
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when{
            modelClass.isAssignableFrom(HikerViewModel::class.java) -> HikerViewModel() as T
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}