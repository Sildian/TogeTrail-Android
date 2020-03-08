package com.sildian.apps.togetrail.common.utils.cloudHelpers

import androidx.lifecycle.LifecycleOwner
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.Query

/*************************************************************************************************
 * Provides with functions allowing to populate RecyclerViews with Firebase queries
 ************************************************************************************************/

object RecyclerViewFirebaseHelper {

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