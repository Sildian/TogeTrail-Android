package com.sildian.apps.togetrail.chat.others

import android.view.LayoutInflater
import android.view.ViewGroup
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.chat.model.core.Message

/*************************************************************************************************
 * Displays a message in a multi users chat space
 ************************************************************************************************/

class MultiUsersMessageAdapter(
    options: FirestoreRecyclerOptions<Message>,
    private val onAuthorClickListener: MultiUsersMessageViewHolder.OnAuthorClickListener? = null
)
    : FirestoreRecyclerAdapter<Message, MultiUsersMessageViewHolder>(options)
{

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MultiUsersMessageViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_recycler_view_multi_users_message, parent, false)
        return MultiUsersMessageViewHolder(view, onAuthorClickListener)
    }

    override fun onBindViewHolder(holder: MultiUsersMessageViewHolder, position: Int, message: Message) {
        holder.updateUI(message)
    }
}