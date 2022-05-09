package com.sildian.apps.togetrail.hiker.data.helpers

import com.google.firebase.auth.FirebaseUser
import com.sildian.apps.togetrail.hiker.data.models.Hiker

/*************************************************************************************************
 * Provides with functions allowing to build a Hiker
 ************************************************************************************************/

class HikerBuilder {

    /**********************************Hiker fields**********************************************/

    private var id:String=""
    private var email:String?=null
    private var name:String?=null
    private var photoUrl:String?=null

    /********************************Build steps*************************************************/

    /**
     * Uses a user from Firebase to build a hiker
     * @param user : the firebase user
     * @return an instance of HikerBuilder
     */

    fun withFirebaseUser(user:FirebaseUser):HikerBuilder{
        this.id=user.uid
        this.email=user.email
        this.name=user.displayName
        this.photoUrl=user.photoUrl?.toString()
        return this
    }

    /**
     * Builds a Hiker with the provided fields
     * @return a Hiker
     */

    fun build():Hiker{
        return Hiker(
            id = this.id,
            email = this.email,
            name = this.name,
            photoUrl = this.photoUrl
        )
    }
}