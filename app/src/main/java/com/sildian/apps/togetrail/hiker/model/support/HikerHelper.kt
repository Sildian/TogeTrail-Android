package com.sildian.apps.togetrail.hiker.model.support

import com.google.firebase.auth.FirebaseUser
import com.sildian.apps.togetrail.hiker.model.core.Hiker

/*************************************************************************************************
 * Provides with functions allowing to build a Hiker
 ************************************************************************************************/

object HikerHelper {

    /**
     * Builds a Hiker from a FirebaseUser
     * @param user : the firebase user
     * @return the resulted Hiker
     */

    fun buildFromFirebaseUser(user:FirebaseUser): Hiker {
        return Hiker(
            id = user.uid,
            email = user.email?:"",
            name = user.displayName?:"",
            photoUrl = user.photoUrl?.path
            )
    }
}