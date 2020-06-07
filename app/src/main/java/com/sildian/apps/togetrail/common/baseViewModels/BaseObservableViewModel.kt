package com.sildian.apps.togetrail.common.baseViewModels

import androidx.databinding.Observable
import androidx.databinding.PropertyChangeRegistry
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ListenerRegistration

/*************************************************************************************************
 * Base for observable viewModels
 ************************************************************************************************/

abstract class BaseObservableViewModel: ViewModel(), Observable {

    /**********************************Attrs*****************************************************/

    protected val callbacks= PropertyChangeRegistry()                 //Observable callbacks
    protected var queryRegistration: ListenerRegistration? = null     //Listener for database queries

    /***********************************Life cycle***********************************************/

    override fun onCleared() {
        this.callbacks.clear()
        this.queryRegistration?.remove()
    }

    /*****************************Callbacks monitoring*******************************************/

    protected fun notifyDataChanged() {
        this.callbacks.notifyCallbacks(this, 0, null)
    }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
        this.callbacks.add(callback)
    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
        this.callbacks.remove(callback)
    }
}