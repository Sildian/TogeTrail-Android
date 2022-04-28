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

    protected val mutableData = MutableLiveData<ListDataHolder<T>>()
    val data: LiveData<ListDataHolder<T>> = mutableData

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
                    this.mutableData.postValue(ListDataHolder(results))
                } else if (e != null) {
                    Log.e(TAG, "Failed to load ${dataModelClass.simpleName} from database : ${e.message}")
                    this.mutableData.postValue(ListDataHolder(this.mutableData.value?.data?: emptyList(), e))
                }
            }
    }
}

/**************************Monitors a single data of the given type T****************************/

abstract class SingleDataViewModel<T: Any>(dataModelClass: Class<T>): DataViewModel<T>(dataModelClass) {

    /**Data**/

    protected val mutableData = MutableLiveData<SingleDataHolder<T?>>()
    val data: LiveData<SingleDataHolder<T?>> = mutableData
    /*Some data requests doesn't return any data so a state holder is used to watch success state*/
    protected val mutableDataRequestState = MutableLiveData<DataRequestStateHolder>()
    val dataRequestState: LiveData<DataRequestStateHolder> = mutableDataRequestState

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
                    this.mutableData.postValue(SingleDataHolder(result))
                }
                else if (e != null) {
                    Log.e(TAG, "Failed to load ${dataModelClass.simpleName} from database : ${e.message}")
                    this.mutableData.postValue(SingleDataHolder(this.mutableData.value?.data, e))
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
                        mutableData.postValue(SingleDataHolder(result))
                    } ?: run {
                        val e = Exception("Unknown error")
                        Log.e(TAG, "Failed to load ${dataModelClass.simpleName} from database : ${e.message}")
                        mutableData.postValue(SingleDataHolder(mutableData.value?.data, e))
                    }
                } catch (e: Throwable) {
                    Log.e(TAG, "Failed to load ${dataModelClass.simpleName} from database : ${e.message}")
                    mutableData.postValue(SingleDataHolder(mutableData.value?.data, e))
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
                    mutableDataRequestState.postValue(DataRequestStateHolder(dataRequest, null))
                } catch (e: Throwable) {
                    Log.e(TAG, "Failed to save ${dataModelClass.simpleName} in database : ${e.message}")
                    mutableDataRequestState.postValue(DataRequestStateHolder(dataRequest, e))
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
                    mutableDataRequestState.postValue(DataRequestStateHolder(dataRequest, null))
                } catch (e: Throwable) {
                    Log.e(TAG, "Failed to run request ${dataRequest.javaClass.simpleName} : ${e.message}")
                    mutableDataRequestState.postValue(DataRequestStateHolder(dataRequest, e))
                }
            }
        }
    }
}