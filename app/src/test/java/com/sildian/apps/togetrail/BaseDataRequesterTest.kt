package com.sildian.apps.togetrail

import com.google.firebase.auth.FirebaseUser
import com.sildian.apps.togetrail.chat.model.core.Message
import com.sildian.apps.togetrail.event.model.core.Event
import com.sildian.apps.togetrail.hiker.model.core.Hiker
import com.sildian.apps.togetrail.hiker.model.support.CurrentHikerInfo
import com.sildian.apps.togetrail.trail.model.core.Trail
import org.junit.After
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/*************************************************************************************************
 * Base for all data requester tests
 * Use Shadows in order to avoid requests to the server
 * The aim of these tests is to check the business logic, not the queries
 ************************************************************************************************/

@RunWith(RobolectricTestRunner::class)
@Config(
    sdk = [28],
    shadows = [
        AuthRepositoryShadow::class,
        StorageRepositoryShadow::class,
        HikerRepositoryShadow::class,
        TrailRepositoryShadow::class,
        EventRepositoryShadow::class
    ]
)
open class BaseDataRequesterTest {

    /*************************************Static items*******************************************/

    companion object {

        /**Default values for fake objects to create**/
        const val USER_ID = "USER_TOTO"
        const val USER_NAME = "Toto"
        const val USER_EMAIL = "toto@toto.com"
        const val PHOTO_URI = "file://toto.jpg"
        const val PHOTO_URL = "https://toto.jpg"
        const val TRAIL_ID = "TRAIL_BEST"
        const val TRAIL_NAME = "Best trail in the world"
        const val EVENT_ID = "EVENT_BEST"
        const val EVENT_NAME = "Best event in the world"
        const val MESSAGE_ID = "TOTO_MESSAGE"
        const val MESSAGE_TEXT = "Coucou"

        /**Allows to set params for fake objects sent by shadows**/
        /*Set it to true to simulate a request failure*/
        var requestShouldFail = false
        /*Set it to true to send null objects*/
        var returnUserSampleNull = false
        var returnHikerSampleNull = false
        var returnTrailSampleNull = false
        var returnEventSampleNull = false
        /*Set it to true to indicate that the object has a photo*/
        var hikerSampleHasPhoto = false
        var trailSampleHasPhoto = false

        /**When a shadow is invoked, it set one of these items to true**/
        /*Auth*/
        var isUserUpdated = false
        var isUserSignedOut = false
        var isUserPasswordReset = false
        var isUserAccountDeleted = false
        /*Storage*/
        var isImageDeleted = false
        var isImageUploaded = false
        /*Hiker*/
        var isHikerUpdated = false
        var isHikerDeleted = false
        var isHikerHistoryItemAdded = false
        var isHikerHistoryItemDeleted = false
        var isHikerRegisteredToEvent = false
        var isHikerUnregisteredFromEvent = false
        /*Trail*/
        var isTrailAdded = false
        var isTrailUpdated = false
        /*Event*/
        var isEventAdded = false
        var isEventUpdated = false
        var isEventHasTrailAttached = false
        var isEventHasTrailDetached = false
        var isEventHasHikerRegistered = false
        var isEventHasHikerUnregistered = false
        var isEventMessageSent = false
        var isEventMessageUpdated = false
        var isEventMessageDeleted = false

        /**Gets a default user sample**/
        fun getUserSample(): FirebaseUser? {
            return if (returnUserSampleNull) {
                null
            } else {
                val user = Mockito.mock(FirebaseUser::class.java)
                Mockito.`when`(user?.uid).thenReturn(USER_ID)
                Mockito.`when`(user?.email).thenReturn(USER_EMAIL)
                Mockito.`when`(user?.displayName).thenReturn(USER_NAME)
                user
            }
        }

        /**Gets a default hiker sample**/
        fun getHikerSample(): Hiker? {
            return when {
                returnHikerSampleNull -> null
                hikerSampleHasPhoto -> Hiker(id = USER_ID, name = USER_NAME, email = USER_EMAIL, photoUrl = PHOTO_URL)
                else -> Hiker(id = USER_ID, name = USER_NAME, email = USER_EMAIL)
            }
        }

        /**Gets a default trail sample**/
        fun getTrailSample(): Trail? {
            return when {
                returnTrailSampleNull -> null
                trailSampleHasPhoto -> Trail(id = TRAIL_ID, name = TRAIL_NAME, mainPhotoUrl = PHOTO_URL)
                else -> Trail(id = TRAIL_ID, name = TRAIL_NAME)
            }
        }

        /**Gets a default event sample**/
        fun getEventSample(): Event? {
            return when {
                returnEventSampleNull -> null
                else -> Event(id = EVENT_ID, name = EVENT_NAME)
            }
        }

        /**Gets a default message sample**/
        fun getMessageSample(): Message = Message(id = MESSAGE_ID, text = MESSAGE_TEXT, authorId = USER_ID)
    }

    /**Reset all**/
    
    @After
    fun finish() {
        CurrentHikerInfo.currentHiker = null
        requestShouldFail = false
        returnUserSampleNull = false
        returnHikerSampleNull = false
        returnTrailSampleNull = false
        returnEventSampleNull = false
        hikerSampleHasPhoto = false
        trailSampleHasPhoto = false
        isUserUpdated = false
        isUserSignedOut = false
        isUserPasswordReset = false
        isUserAccountDeleted = false
        isImageDeleted = false
        isImageUploaded = false
        isHikerUpdated = false
        isHikerDeleted = false
        isHikerHistoryItemAdded = false
        isHikerHistoryItemDeleted = false
        isHikerRegisteredToEvent = false
        isHikerUnregisteredFromEvent = false
        isTrailAdded = false
        isTrailUpdated = false
        isEventAdded = false
        isEventUpdated = false
        isEventHasTrailAttached = false
        isEventHasTrailDetached = false
        isEventHasHikerRegistered = false
        isEventHasHikerUnregistered = false
        isEventMessageSent = false
        isEventMessageUpdated = false
        isEventMessageDeleted = false
    }
}