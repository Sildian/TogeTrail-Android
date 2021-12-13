package com.sildian.apps.togetrail.dataRequestTestSupport

import androidx.core.net.toUri
import com.google.firebase.auth.FirebaseUser
import com.sildian.apps.togetrail.chat.model.core.Duo
import com.sildian.apps.togetrail.chat.model.core.Message
import com.sildian.apps.togetrail.event.model.core.Event
import com.sildian.apps.togetrail.hiker.model.core.Hiker
import com.sildian.apps.togetrail.hiker.model.core.HikerHistoryItem
import com.sildian.apps.togetrail.trail.model.core.Trail
import org.mockito.Mockito

/*************************************************************************************************
 * Allow simulating Firebase Auth, Storage and Firestore during data request tests
 ************************************************************************************************/

object FirebaseSimulator {

    /***********************Flags defining the behavior of the simulator**************************/

    var requestShouldFail = false           //If set to true, any request will fail

    /********************************Items to be stored******************************************/

    /**Auth**/
    var currentUser: FirebaseUser? = null

    /**Storage**/
    val storageUrls = arrayListOf<String>()

    /**Firestore
     * Note that for each hashMap, the key is the id of the parent item
     **/

    /*Hiker*/
    val hikers = arrayListOf<Hiker>()
    val hikerHistoryItems = hashMapOf<String, ArrayList<HikerHistoryItem>>()
    val hikerAttendedEvents = hashMapOf<String, ArrayList<Event>>()
    val hikerLikedTrails = hashMapOf<String, ArrayList<Trail>>()
    val hikerMarkedTrails = hashMapOf<String, ArrayList<Trail>>()
    val hikerChats = hashMapOf<String, ArrayList<Duo>>()
    val hikerChatMessages = hashMapOf<String, HashMap<String, ArrayList<Message>>>()

    /*Trail*/
    val trails = arrayListOf<Trail>()

    /*Event*/
    val events = arrayListOf<Event>()
    val eventAttachedTrails = hashMapOf<String, ArrayList<Trail>>()
    val eventRegisteredHikers = hashMapOf<String, ArrayList<Hiker>>()
    val eventMessages = hashMapOf<String, ArrayList<Message>>()

    /***********************************Monitoring************************************************/

    fun reset() {
        requestShouldFail = false
        currentUser = null
        storageUrls.clear()
        hikers.clear()
        hikerHistoryItems.clear()
        hikerAttendedEvents.clear()
        hikerLikedTrails.clear()
        hikerMarkedTrails.clear()
        hikerChats.clear()
        hikerChatMessages.clear()
        trails.clear()
        events.clear()
        eventAttachedTrails.clear()
        eventRegisteredHikers.clear()
        eventMessages.clear()
    }

    fun setCurrentUser(email: String, displayName: String, photoUrl: String?) {
        val user = Mockito.mock(FirebaseUser::class.java)
        Mockito.`when`(user?.uid).thenReturn("UID")
        Mockito.`when`(user?.email).thenReturn(email)
        Mockito.`when`(user?.displayName).thenReturn(displayName)
        Mockito.`when`(user?.photoUrl).thenReturn(photoUrl?.toUri())
        currentUser = user
    }
}