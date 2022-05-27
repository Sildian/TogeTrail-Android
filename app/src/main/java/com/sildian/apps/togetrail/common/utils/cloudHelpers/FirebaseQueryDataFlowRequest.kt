package com.sildian.apps.togetrail.common.utils.cloudHelpers

import com.google.firebase.firestore.Query
import com.sildian.apps.togetrail.common.baseDataRequests.DataFlowRequest
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/*************************************************************************************************
 * Data flow request using Firebase Query
 ************************************************************************************************/

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