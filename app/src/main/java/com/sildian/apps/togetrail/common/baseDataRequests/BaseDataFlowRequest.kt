package com.sildian.apps.togetrail.common.baseDataRequests

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Query
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*

/*************************************************************************************************
 * This file provides with base classes to run data requests using flows
 ************************************************************************************************/

/**Base for all data flow requests**/

@ExperimentalCoroutinesApi
abstract class DataFlowRequest<T: Any>(private val dispatcher: CoroutineDispatcher): DataRequest {

    private var isRunning = false
    var flow: Flow<T?>? = null ; protected set

    final override suspend fun execute() {
        withContext(this.dispatcher) {
            isRunning = true
            flow = provideFlow()
                .flowOn(dispatcher)
                .takeWhile { isRunning }
                .catch { e ->
                    isRunning = false
                    throw e
                }
        }
    }

    final override suspend fun cancel() {
        this.isRunning = false
    }

    final override fun isRunning(): Boolean = this.isRunning

    abstract fun provideFlow(): Flow<T?>
}

/**Data flow request using Firebase DocumentReference**/

@ExperimentalCoroutinesApi
class FirebaseDocumentDataFlowRequest<T: Any>(
    dispatcher: CoroutineDispatcher,
    private val dataModelClass: Class<T>,
    private val documentReference: DocumentReference
    )
    : DataFlowRequest<T>(dispatcher)
{

    override fun provideFlow(): Flow<T?> =
        callbackFlow {
            val listenerRegistration = documentReference
                .addSnapshotListener { snapshot, e ->
                    e?.let {
                        close(e)
                    } ?: snapshot?.let {
                        trySend(snapshot.toObject(dataModelClass))
                    }
                }
            awaitClose { listenerRegistration.remove() }
        }
}

/**Data flow request using Firebase Query**/

@ExperimentalCoroutinesApi
class FirebaseQueryDataFlowRequest<T: Any>(
    dispatcher: CoroutineDispatcher,
    private val dataModelClass: Class<T>,
    private val query: Query
    )
    : DataFlowRequest<List<T>>(dispatcher)
{

    override fun provideFlow(): Flow<List<T>?> =
        callbackFlow {
            val listenerRegistration = query
                .addSnapshotListener { querySnapshot, e ->
                    e?.let {
                        close(e)
                    } ?: querySnapshot?.let {
                        trySend(querySnapshot.toObjects(dataModelClass))
                    }
                }
            awaitClose { listenerRegistration.remove() }
        }
}