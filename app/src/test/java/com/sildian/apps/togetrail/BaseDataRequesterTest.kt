package com.sildian.apps.togetrail

import com.google.firebase.auth.FirebaseUser
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
        TrailRepositoryShadow::class
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

        /**Allows to set params for samples to return within shadows**/
        var returnUserSampleNull = false
        var returnHikerSampleNull = false
        var returnTrailSampleNull = false
        var hikerSampleHasPhoto = false
        var trailSampleHasPhoto = false

        /**Set to true by shadows instead of requesting the server**/
        var isUserUpdated = false
        var isUserSignedOut = false
        var isUserPasswordReset = false
        var isUserAccountDeleted = false
        var isImageDeleted = false
        var isImageUploaded = false
        var isHikerUpdated = false
        var isHikerDeleted = false
        var isHistoryItemAdded = false
        var isTrailAdded = false
        var isTrailUpdated = false

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
    }

    @After
    fun finish() {
        CurrentHikerInfo.currentHiker = null
        returnUserSampleNull = false
        returnHikerSampleNull = false
        returnTrailSampleNull = false
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
        isHistoryItemAdded = false
        isTrailAdded = false
        isTrailUpdated = false
    }
}