package com.sildian.apps.togetrail.common.baseViewModels

import android.util.Log
import androidx.databinding.Observable
import androidx.databinding.PropertyChangeRegistry
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sildian.apps.togetrail.common.baseDataRequests.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect

/*************************************************************************************************
 * This file provides with base abstract classes for data viewModels
 ************************************************************************************************/

private const val TAG = "DataViewModel"

/*****************************Base for all data viewModels***************************************/

abstract class DataViewModel<T: Any>(protected val dataModelClass: Class<T>, protected val dispatcher: CoroutineDispatcher) : ViewModel(), Observable {

    /**Callbacks**/

    protected val callbacks = PropertyChangeRegistry()                  //Observable callbacks

    /**Data**/

    protected var currentDataRequest: DataRequest? = null

    /**Coroutines**/

    protected val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        Log.e(TAG, throwable.message.toString())
    }

    /**Life cycle**/

    override fun onCleared() {
        removeAllOnPropertyChangedCallbacks()
        cancelCurrentDataRequest()
    }

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

    /**Data request monitoring**/

    fun cancelCurrentDataRequest() {
        viewModelScope.launch(this.exceptionHandler) {
            currentDataRequest?.cancel()
        }
    }

    fun isDataRequestRunning(): Boolean = this.currentDataRequest?.isRunning() == true
}

/***************************Monitors a data list of the given type T****************************/

abstract class ListDataViewModel<T: Any>(dataModelClass: Class<T>, dispatcher: CoroutineDispatcher): DataViewModel<T>(dataModelClass, dispatcher) {

    /**Data**/

    protected val mutableData = MutableLiveData<ListDataHolder<T>>()
    val data: LiveData<ListDataHolder<T>> = mutableData

    /**Data monitoring**/

    open fun notifyDataObserver() {
        this.mutableData.value = this.mutableData.value
    }

    @ExperimentalCoroutinesApi
    protected fun loadDataFlow(dataRequest: DataFlowRequest<List<T>>) {
        viewModelScope.launch(this.exceptionHandler) {
            currentDataRequest?.cancel()
            currentDataRequest = dataRequest
            dataRequest.execute()
            dataRequest.flow
                ?.catch { e ->
                    Log.e(TAG, "Failed to load ${dataModelClass.simpleName} : ${e.message}")
                    mutableData.postValue(ListDataHolder(mutableData.value?.data?: emptyList(), e))
                }
                ?.collect { results ->
                    results?.let {
                        Log.d(TAG, "Successfully loaded ${results.size} ${dataModelClass.simpleName}")
                        mutableData.postValue(ListDataHolder(results))
                    } ?: run {
                        val e = Exception("Unknown error")
                        Log.e(TAG, "Failed to load ${dataModelClass.simpleName} : ${e.message}")
                        mutableData.postValue(ListDataHolder(mutableData.value?.data?: emptyList(), e))
                    }
                }
        }
    }
}

/**************************Monitors a single data of the given type T****************************/

abstract class SingleDataViewModel<T: Any>(dataModelClass: Class<T>, dispatcher: CoroutineDispatcher): DataViewModel<T>(dataModelClass, dispatcher) {

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

    @ExperimentalCoroutinesApi
    protected fun loadDataFlow(dataRequest: DataFlowRequest<T>) {
        viewModelScope.launch(this.exceptionHandler) {
            currentDataRequest?.cancel()
            currentDataRequest = dataRequest
            dataRequest.execute()
            dataRequest.flow
                ?.catch { e ->
                    Log.e(TAG, "Failed to load ${dataModelClass.simpleName} : ${e.message}")
                    mutableData.postValue(SingleDataHolder(mutableData.value?.data, e))
                }
                ?.collect { result ->
                    result?.let {
                        Log.d(TAG, "Successfully loaded ${dataModelClass.simpleName}")
                        mutableData.postValue(SingleDataHolder(result))
                    } ?: run {
                        val e = Exception("Unknown error")
                        Log.e(TAG, "Failed to load ${dataModelClass.simpleName} : ${e.message}")
                        mutableData.postValue(SingleDataHolder(mutableData.value?.data, e))
                    }
                }
        }
    }

    protected fun loadData(dataRequest: LoadDataRequest<T>) {
        viewModelScope.launch(this.exceptionHandler) {
            try {
                currentDataRequest?.cancel()
                currentDataRequest = dataRequest
                dataRequest.execute()
                dataRequest.data?.let { result ->
                    Log.d(TAG, "Successfully loaded ${dataModelClass.simpleName}")
                    mutableData.postValue(SingleDataHolder(result))
                } ?: run {
                    val e = Exception("Unknown error")
                    Log.e(TAG, "Failed to load ${dataModelClass.simpleName} : ${e.message}")
                    mutableData.postValue(SingleDataHolder(mutableData.value?.data, e))
                }
            } catch (e: Throwable) {
                Log.e(TAG, "Failed to load ${dataModelClass.simpleName} : ${e.message}")
                mutableData.postValue(SingleDataHolder(mutableData.value?.data, e))
            }
        }
    }

    protected fun saveData(dataRequest: SaveDataRequest<T>) {
        viewModelScope.launch(this.exceptionHandler) {
            try {
                currentDataRequest?.cancel()
                currentDataRequest = dataRequest
                dataRequest.execute()
                Log.d(TAG, "Successfully saved ${dataModelClass.simpleName}")
                mutableDataRequestState.postValue(DataRequestStateHolder(dataRequest, null))
            } catch (e: Throwable) {
                Log.e(TAG, "Failed to save ${dataModelClass.simpleName} : ${e.message}")
                mutableDataRequestState.postValue(DataRequestStateHolder(dataRequest, e))
            }
        }
    }

    protected fun runSpecificRequest(dataRequest: SpecificDataRequest) {
        viewModelScope.launch(this.exceptionHandler) {
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