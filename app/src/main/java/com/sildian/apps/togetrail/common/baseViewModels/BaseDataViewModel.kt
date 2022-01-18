package com.sildian.apps.togetrail.common.baseViewModels

import android.util.Log
import androidx.databinding.Observable
import androidx.databinding.PropertyChangeRegistry
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.sildian.apps.togetrail.common.baseDataRequests.DataRequest
import com.sildian.apps.togetrail.common.baseDataRequests.LoadDataRequest
import com.sildian.apps.togetrail.common.baseDataRequests.SaveDataRequest
import com.sildian.apps.togetrail.common.baseDataRequests.SpecificDataRequest
import kotlinx.coroutines.*

/*************************************************************************************************
 * This file provides with base abstract classes for data viewModels
 ************************************************************************************************/

private const val TAG = "DataViewModel"

/*****************************Base for all data viewModels***************************************/

abstract class DataViewModel<T: Any>(protected val dataModelClass: Class<T>) : ViewModel(), Observable {

    /**Callbacks and registration**/

    protected val callbacks = PropertyChangeRegistry()                  //Observable callbacks
    protected var queryRegistration: ListenerRegistration? = null       //Listener for database queries

    /**Data**/

    protected var currentDataRequest: DataRequest? = null
    //Success is only used to report save and specific requests. Load requests result is reported within specific data fields.
    protected val mutableSuccess = MutableLiveData<DataRequest?>()
    val success: LiveData<DataRequest?> = mutableSuccess
    //Error is used to report any request error
    protected val mutableError = MutableLiveData<Throwable?>()
    val error: LiveData<Throwable?> = mutableError

    /**Coroutines**/

    protected val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        Log.e(TAG, throwable.message.toString())
    }

    /**Life cycle**/

    override fun onCleared() {
        removeAllOnPropertyChangedCallbacks()
        clearQueryRegistration()
    }

    /**Query registration monitoring**/

    fun clearQueryRegistration() {
        this.queryRegistration?.remove()
        this.queryRegistration = null
    }

    fun isQueryRegistrationBusy(): Boolean = this.queryRegistration != null

    /**Callbacks monitoring**/

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

/***************************Monitors a data list of the given type T****************************/

abstract class ListDataViewModel<T: Any>(dataModelClass: Class<T>): DataViewModel<T>(dataModelClass) {

    /**Data**/

    protected val mutableData = MutableLiveData<List<T>>()
    val data: LiveData<List<T>> = mutableData

    /**Data monitoring**/

    open fun notifyDataObserver() {
        this.mutableData.value = this.mutableData.value
    }

    protected fun loadDataRealTime(query: Query) {
        this.queryRegistration?.remove()
        this.queryRegistration = query
            .addSnapshotListener { querySnapshot, e ->
                if (querySnapshot != null) {
                    val results = querySnapshot.toObjects(this.dataModelClass)
                    Log.d(TAG, "Successfully loaded ${results.size} ${dataModelClass.simpleName} from database")
                    this.mutableData.postValue(results)
                    this.mutableError.postValue(null)
                } else if (e != null) {
                    Log.e(TAG, "Failed to load ${dataModelClass.simpleName} from database : ${e.message}")
                    this.mutableError.postValue(e)
                }
            }
    }
}

/**************************Monitors a single data of the given type T****************************/

abstract class SingleDataViewModel<T: Any>(dataModelClass: Class<T>): DataViewModel<T>(dataModelClass) {

    /**Data**/

    protected val mutableData = MutableLiveData<T?>()
    val data: LiveData<T?> = mutableData

    /**Data monitoring**/

    open fun notifyDataObserver() {
        this.mutableData.value = this.mutableData.value
    }

    protected fun loadDataRealTime(documentReference: DocumentReference) {
        this.queryRegistration?.remove()
        this.queryRegistration = documentReference
            .addSnapshotListener { snapshot, e ->
                if (snapshot != null) {
                    val result = snapshot.toObject(this.dataModelClass)
                    Log.d(TAG, "Successfully loaded ${dataModelClass.simpleName} from database")
                    this.mutableData.postValue(result)
                    this.mutableError.postValue(null)
                }
                else if (e != null) {
                    Log.e(TAG, "Failed to load ${dataModelClass.simpleName} from database : ${e.message}")
                    this.mutableError.postValue(e)
                }
            }
    }

    protected fun loadData(dataRequest: LoadDataRequest<T>) {
        viewModelScope.launch(this.exceptionHandler) {
            withContext(Dispatchers.Main) {
                try {
                    currentDataRequest?.cancel()
                    currentDataRequest = dataRequest
                    dataRequest.execute()
                    dataRequest.data?.let { result ->
                        Log.d(TAG, "Successfully loaded ${dataModelClass.simpleName} from database")
                        mutableData.postValue(result)
                        mutableError.postValue(null)
                    } ?: run {
                        val e = Exception("Unknown error")
                        Log.e(TAG, "Failed to load ${dataModelClass.simpleName} from database : ${e.message}")
                        mutableError.postValue(e)
                    }
                } catch (e: Throwable) {
                    Log.e(TAG, "Failed to load ${dataModelClass.simpleName} from database : ${e.message}")
                    mutableError.postValue(e)
                }
            }
        }
    }

    protected fun saveData(dataRequest: SaveDataRequest<T>) {
        viewModelScope.launch(this.exceptionHandler) {
            withContext(Dispatchers.Main) {
                try {
                    currentDataRequest?.cancel()
                    currentDataRequest = dataRequest
                    dataRequest.execute()
                    Log.d(TAG, "Successfully saved ${dataModelClass.simpleName} in database")
                    mutableSuccess.postValue(dataRequest)
                    mutableError.postValue(null)
                } catch (e: Throwable) {
                    Log.e(TAG, "Failed to save ${dataModelClass.simpleName} in database : ${e.message}")
                    mutableSuccess.postValue(null)
                    mutableError.postValue(e)
                }
            }
        }
    }

    protected fun runSpecificRequest(dataRequest: SpecificDataRequest) {
        viewModelScope.launch(this.exceptionHandler) {
            withContext(Dispatchers.Main) {
                try {
                    currentDataRequest?.cancel()
                    currentDataRequest = dataRequest
                    dataRequest.execute()
                    Log.d(TAG, "Successfully finished running request ${dataRequest.javaClass.simpleName}")
                    mutableSuccess.postValue(dataRequest)
                    mutableError.postValue(null)
                } catch (e: Throwable) {
                    Log.e(TAG, "Failed to run request ${dataRequest.javaClass.simpleName} : ${e.message}")
                    mutableSuccess.postValue(null)
                    mutableError.postValue(e)
                }
            }
        }
    }
}