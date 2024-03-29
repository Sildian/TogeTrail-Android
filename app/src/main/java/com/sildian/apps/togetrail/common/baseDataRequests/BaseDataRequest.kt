package com.sildian.apps.togetrail.common.baseDataRequests

import kotlinx.coroutines.*

/*************************************************************************************************
 * This file provides with base interface and abstract class to run data requests
 ************************************************************************************************/

/**Base interface for all data requests**/

interface DataRequest {
    suspend fun execute()
    suspend fun cancel()
}

/**Base for all data requests aiming to load data matching the given type T**/

abstract class LoadDataRequest<T: Any>: DataRequest {

    private var job: Deferred<T?>? = null
    var data: T? = null

    final override suspend fun execute() {
        withContext(Dispatchers.IO) {
            job = async { load() }
            data = job?.await()
        }
    }

    final override suspend fun cancel() {
        job?.cancel()
    }

    protected abstract suspend fun load(): T?
}

/**Base for all data requests aiming to save data matching the given type T**/

abstract class SaveDataRequest<T: Any>(protected val data: T?): DataRequest {

    private var job: Job? = null

    final override suspend fun execute() {
        withContext(Dispatchers.IO) {
            job = launch { save() }
            job?.join()
        }
    }

    final override suspend fun cancel() {
        job?.cancel()
    }

    protected abstract suspend fun save()
}

/**Base for all data requests aiming to run specific tasks**/

abstract class SpecificDataRequest: DataRequest {

    private var job: Job? = null

    final override suspend fun execute() {
        withContext(Dispatchers.IO) {
            job = launch { run() }
            job?.join()
        }
    }

    final override suspend fun cancel() {
        job?.cancel()
    }

    protected abstract suspend fun run()
}