package com.sildian.apps.togetrail.common.utils.cloudHelpers

import com.google.firebase.firestore.DocumentReference
import com.sildian.apps.togetrail.common.baseDataRequests.DataFlowRequest
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/*************************************************************************************************
 * Data flow request using Firebase DocumentReference
 ************************************************************************************************/

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