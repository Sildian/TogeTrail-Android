package com.sildian.apps.togetrail.common.baseDataRequests

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Query
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/*************************************************************************************************
 * This file provides with base classes to run data requests using flows
 ************************************************************************************************/

/**Base for all data flow requests**/

@ExperimentalCoroutinesApi
abstract class DataFlowRequest<T: Any?>: DataRequest {

    protected var channel: SendChannel<T?>? = null
    var flow: Flow<T?>? = null ; protected set

    final override fun isRunning(): Boolean = this.channel?.isClosedForSend == false
}

/**Data flow request using Firebase DocumentReference**/

@ExperimentalCoroutinesApi
class FirebaseDocumentDataFlowRequest<T: Any>(
    private val dataModelClass: Class<T>,
    private val documentReference: DocumentReference
    )
    : DataFlowRequest<T?>()
{

    override suspend fun execute() {
        this.flow = callbackFlow {
            this@FirebaseDocumentDataFlowRequest.channel = channel
            val listenerRegistration = documentReference
                .addSnapshotListener { snapshot, e ->
                    e?.let {
                        throw e
                    } ?: snapshot?.let {
                        trySend(snapshot.toObject(dataModelClass))
                    }
                }
            awaitClose { listenerRegistration.remove() }
        }
    }

    override suspend fun cancel() {
        this.channel?.close()
    }
}

/**Data flow request using Firebase Query**/

@ExperimentalCoroutinesApi
class FirebaseQueryDataFlowRequest<T: Any>(
    private val dataModelClass: Class<T>,
    private val query: Query
    )
    : DataFlowRequest<List<T>>()
{

    override suspend fun execute() {
        this.flow = callbackFlow {
            this@FirebaseQueryDataFlowRequest.channel = channel
            val listenerRegistration = query
                .addSnapshotListener { querySnapshot, e ->
                    e?.let {
                        throw e
                    } ?: querySnapshot?.let {
                        trySend(querySnapshot.toObjects(dataModelClass))
                    }
                }
            awaitClose { listenerRegistration.remove() }
        }
    }

    override suspend fun cancel() {
        this.channel?.close()
    }
}