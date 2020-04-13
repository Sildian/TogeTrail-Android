package com.sildian.apps.togetrail.common.utils.cloudHelpers

import androidx.lifecycle.LifecycleOwner
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.sildian.apps.togetrail.event.model.core.Event
import com.sildian.apps.togetrail.hiker.model.core.Hiker
import com.sildian.apps.togetrail.trail.model.core.Trail
import java.lang.Exception

/*************************************************************************************************
 * Provides with some support functions to use Firebase queries
 ************************************************************************************************/

object DatabaseFirebaseHelper {

    /**
     * Gets an item from the database
     * @param type : the type class of the related item to get
     * @param documentReference : the document reference containing the item
     * @param successCallback : callback invoked when success, the resulted item as parameter
     * @param failureCallback : callback invoked when failure, the exception as parameter
     */

    fun <T:Any> getItem(
        type:Class<T>, documentReference: DocumentReference,
        successCallback:(T?)->Unit, failureCallback:(Exception)->Unit){

        documentReference.get()
            .addOnSuccessListener { snapshot ->
                if (snapshot != null) {
                    val item = when{
                        type.isAssignableFrom(Hiker::class.java) -> snapshot.toObject(Hiker::class.java)
                        type.isAssignableFrom(Trail::class.java) -> snapshot.toObject(Trail::class.java)
                        type.isAssignableFrom(Event::class.java) -> snapshot.toObject(Event::class.java)
                        else -> null
                    }
                    successCallback.invoke(item as T?)
                }
            }
            .addOnFailureListener { e ->
                failureCallback.invoke(e)
            }
    }

    /**
     * Gets an item from the database (updated in real time)
     * @param type : the type class of the related item to get
     * @param documentReference : the document reference containing the item
     * @param successCallback : callback invoked when success, the resulted item as parameter
     * @param failureCallback : callback invoked when failure, the exception as parameter
     * @return a listener registration, which should be removed when the query is not used anymore
     */

    fun <T:Any> getItemRealTime(
        type:Class<T>, documentReference: DocumentReference,
        successCallback:(T?)->Unit, failureCallback:(Exception)->Unit):ListenerRegistration{

        return documentReference
            .addSnapshotListener { snapshot, e ->
                if (snapshot != null) {
                    val item = when{
                        type.isAssignableFrom(Hiker::class.java) -> snapshot.toObject(Hiker::class.java)
                        type.isAssignableFrom(Trail::class.java) -> snapshot.toObject(Trail::class.java)
                        type.isAssignableFrom(Event::class.java) -> snapshot.toObject(Event::class.java)
                        else -> null
                    }
                    successCallback.invoke(item as T?)
                }
                else if(e!=null){
                    failureCallback.invoke(e)
                }
            }
    }

    /**
     * Gets a list of items from the database (updated in real time)
     * @param type : the type class of the related item to get
     * @param query : the query to invoke
     * @param successCallback : callback invoked when success, the resulted item as parameter
     * @param failureCallback : callback invoked when failure, the exception as parameter
     * @return a listener registration, which should be removed when the query is not used anymore
     */

    fun <T:Any> getItemsListRealTime(
        type:Class<T>, query:Query,
        successCallback:(List<T>)->Unit, failureCallback:(Exception)->Unit):ListenerRegistration{

        return query
            .addSnapshotListener { querySnapshot, e ->
                if(querySnapshot!=null){
                    val items = when{
                        type.isAssignableFrom(Hiker::class.java) -> querySnapshot.toObjects(Hiker::class.java)
                        type.isAssignableFrom(Trail::class.java) -> querySnapshot.toObjects(Trail::class.java)
                        type.isAssignableFrom(Event::class.java) -> querySnapshot.toObjects(Event::class.java)
                        else -> emptyList()
                    }
                    successCallback.invoke(items as List<T>)
                }
                else if(e!=null){
                    failureCallback.invoke(e)
                }
            }
    }

    /**
     * Adds an item in the database
     * @param type : the type class of the related item to add
     * @param task : the task
     * @param successCallback : callback invoked when success, the resulted item as parameter
     * @param failureCallback : callback invoked when failure, the exception as parameter
     */

    fun <T:Any> addItem(
        type:Class<T>, task: Task<DocumentReference>,
        successCallback:((String)->Unit)?=null, failureCallback:((Exception)->Unit)?=null){

        task
            .addOnSuccessListener { documentReference ->
                successCallback?.invoke(documentReference.id)
            }
            .addOnFailureListener { e ->
                failureCallback?.invoke(e)
            }
    }

    /**
     * Updates an item in the database
     * @param type : the type class of the related item to add
     * @param task : the task
     * @param successCallback : callback invoked when success, the resulted item as parameter
     * @param failureCallback : callback invoked when failure, the exception as parameter
     */

    fun <T:Any> updateItem(
        type:Class<T>, task: Task<Void>,
        successCallback:(()->Unit)?=null, failureCallback:((Exception)->Unit)?=null){

        task
            .addOnSuccessListener {
                successCallback?.invoke()
            }
            .addOnFailureListener { e ->
                failureCallback?.invoke(e)
            }
    }

    /**
     * Deletes an item from the database
     * @param type : the type class of the related item to add
     * @param task : the task
     * @param successCallback : callback invoked when success, the resulted item as parameter
     * @param failureCallback : callback invoked when failure, the exception as parameter
     */

    fun <T:Any> deleteItem(
        type:Class<T>, task: Task<Void>,
        successCallback:(()->Unit)?=null, failureCallback:((Exception)->Unit)?=null){

        task
            .addOnSuccessListener {
                successCallback?.invoke()
            }
            .addOnFailureListener { e ->
                failureCallback?.invoke(e)
            }
    }

    /**
     * Generates options for adapter allowing to populate a RecyclerView with Firebase data
     * @param type : the data class populating the RecyclerView
     * @param query : the Firebase query
     * @param lifecycleOwner : the life cycle Owner (for example : an activity)
     * @return the options
     */

    fun <T:Any> generateOptionsForAdapter(
        type:Class<T>, query: Query, lifecycleOwner: LifecycleOwner): FirestoreRecyclerOptions<T> {

        return FirestoreRecyclerOptions.Builder<T>()
            .setQuery(query, type)
            .setLifecycleOwner(lifecycleOwner)
            .build()
    }
}