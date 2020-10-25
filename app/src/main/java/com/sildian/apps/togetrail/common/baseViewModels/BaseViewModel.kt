package com.sildian.apps.togetrail.common.baseViewModels

import androidx.databinding.Observable
import androidx.databinding.PropertyChangeRegistry
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ListenerRegistration

/*************************************************************************************************
 * Base for viewModels
 ************************************************************************************************/

abstract class BaseViewModel: ViewModel(), Observable {

    /**********************************Attrs*****************************************************/

    protected val callbacks= PropertyChangeRegistry()                 //Observable callbacks
    protected var queryRegistration: ListenerRegistration? = null     //Listener for database queries

    /***********************************Life cycle***********************************************/

    override fun onCleared() {
        this.callbacks.clear()
        clearQueryRegistration()
    }

    /**************************Query registration monitoring*************************************/

    protected fun clearQueryRegistration() {
        this.queryRegistration?.remove()
        this.queryRegistration = null
    }

    fun isQueryRegistrationBusy(): Boolean = this.queryRegistration != null

    /*****************************Callbacks monitoring*******************************************/

    fun notifyDataChanged() {
        this.callbacks.notifyCallbacks(this, 0, null)
    }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
        this.callbacks.add(callback)
    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
        this.callbacks.remove(callback)
    }

    fun removeAllOnPropertyChangedCallbacks() {
        this.callbacks.clear()
    }
}