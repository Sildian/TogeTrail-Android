package com.sildian.apps.togetrail.hiker.model.support

import android.util.Log
import com.google.firebase.firestore.DocumentReference
import com.sildian.apps.togetrail.chat.model.core.Duo
import com.sildian.apps.togetrail.chat.model.core.Message
import com.sildian.apps.togetrail.common.utils.cloudHelpers.AuthRepository
import com.sildian.apps.togetrail.common.utils.cloudHelpers.StorageRepository
import com.sildian.apps.togetrail.hiker.model.core.Hiker
import com.sildian.apps.togetrail.hiker.model.core.HikerHistoryItem
import com.sildian.apps.togetrail.hiker.model.core.HikerHistoryType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/*************************************************************************************************
 * Run jobs to request data related to Hiker
 ************************************************************************************************/

class HikerDataRequester {

    /************************************Static items********************************************/

    companion object {

        /**Logs**/
        private const val TAG = "HikerDataRequester"

        /**Exceptions messages**/
        private const val EXCEPTION_MESSAGE_NULL_USER = "Cannot perform the requested operation with a null user"
        private const val EXCEPTION_MESSAGE_NULL_HIKER = "Cannot perform the requested operation with a null hiker"
        private const val EXCEPTION_MESSAGE_NO_TEXT_MESSAGE = "Cannot perform the requested operation with a message without text"
    }

    /************************************Repositories********************************************/

    private val authRepository = AuthRepository()
    private val storageRepository = StorageRepository()
    private val hikerRepository = HikerRepository()

    /***************************************Requests********************************************/

    /**
     * Loads an hiker from the database in real time
     * @param hikerId : the hiker's id
     */

    fun loadHikerFromDatabaseRealTime(hikerId:String): DocumentReference =
        hikerRepository.getHikerReference(hikerId)

    /**
     * Loads an hiker from the database
     * @param hikerId : the hiker's id
     * @return the resulted Hiker
     * @throws Exception if the request fails
     */

    @Throws(Exception::class)
    suspend fun loadHikerFromDatabase(hikerId: String): Hiker? =
        withContext(Dispatchers.IO) {
            try {
                async { hikerRepository.getHiker(hikerId) }.await()
            }
            catch (e: Exception) {
                throw e
            }
        }

    /**
     * Saves an hiker within the database, after uploading or deleting the profile's image
     * @param hiker : the hiker to be saved
     * @param imagePathToDelete : path to the hiker's profile image to be deleted if needed
     * @param imagePathToUpload : path to the hiker's profile image to be uploaded if needed
     * @throws NullPointerException if the hiker is null
     * @throws Exception if the request fails
     */

    @Throws(Exception::class)
    suspend fun saveHikerInDatabase(hiker: Hiker?, imagePathToDelete: String?, imagePathToUpload: String?) {
        withContext(Dispatchers.IO) {
            try {
                if (hiker != null) {

                    /*Deletes the existing image from storage if needed*/

                    imagePathToDelete?.let { url ->
                        try {
                            launch { storageRepository.deleteImage(url) }.join()
                        } catch (e: Exception) {
                            Log.w(TAG, "Failed to delete photo at url $url : ${e.message}")
                        }
                    }

                    /*Uploads a new image to storage if needed*/

                    imagePathToUpload?.let { uri ->
                        val newImageUrl = async { storageRepository.uploadImage(uri) }.await()
                        hiker.photoUrl = newImageUrl
                    }

                    /*Then updates the hiker*/

                    launch { hikerRepository.updateHiker(hiker) }.join()
                    hiker.name?.let { hikerName ->
                        launch { authRepository.updateUserProfile(hikerName, hiker.photoUrl) }.join()
                    }
                }
                else {
                    throw NullPointerException(EXCEPTION_MESSAGE_NULL_HIKER)
                }
            }
            catch (e: Exception) {
                throw e
            }
        }
    }

    /**
     * Logs the user in and if this is a new user, creates a new Hiker
     * @return the existing hiker or the new created hiker
     * @throws NullPointerException if the user is null
     * @throws Exception if the request fails
     */

    @Throws(Exception::class)
    suspend fun loginUser(): Hiker? =
        withContext(Dispatchers.IO) {

            try {

                /*Gets the current user and the related Hiker info*/

                val user = authRepository.getCurrentUser()
                if (user != null) {

                    var hiker = async { hikerRepository.getHiker(user.uid) }.await()

                    /*If the hiker is null, then creates a new Hiker in the database*/

                    if (hiker == null) {
                        hiker = HikerBuilder()
                            .withFirebaseUser(user)
                            .build()
                        launch { hikerRepository.updateHiker(hiker) }.join()

                        /*Also adds an history item*/

                        val historyItem = HikerHistoryItem(
                            HikerHistoryType.HIKER_REGISTERED,
                            hiker.registrationDate
                        )
                        launch { hikerRepository.addHikerHistoryItem(hiker.id, historyItem) }.join()
                    }

                    /*Then return the resulted hiker*/

                    CurrentHikerInfo.currentHiker = hiker
                    hiker

                }
                else {
                    throw NullPointerException(EXCEPTION_MESSAGE_NULL_USER)
                }
            }
            catch (e: Exception) {
                throw e
            }
        }

    /**Logs the user out**/

    fun logoutUser() {
        CurrentHikerInfo.currentHiker = null
        authRepository.signUserOut()
    }

    /**
     * Resets the user's password
     * @throws NullPointerException if the user is null
     * @throws Exception if the request fails
     */

    @Throws(Exception::class)
    suspend fun resetUserPassword() {
        withContext(Dispatchers.IO) {
            try {
                val user = authRepository.getCurrentUser()
                if (user != null) {
                    launch { authRepository.resetUserPassword() }.join()
                }
                else {
                    throw NullPointerException(EXCEPTION_MESSAGE_NULL_USER)
                }
            }
            catch (e: Exception) {
                throw e
            }
        }
    }

    /**
     * Deletes the user's account as well as all related hiker data and the profile's image
     * @throws NullPointerException if the user or the hiker is null
     * @throws Exception if the request fails
     */

    @Throws(Exception::class)
    suspend fun deleteUserAccount() {
        withContext(Dispatchers.IO) {
            try{

                /*Gets the current user and the related Hiker info*/

                val user = authRepository.getCurrentUser()
                if (user != null) {

                    val hiker = async { hikerRepository.getHiker(user.uid) }.await()
                    if (hiker != null) {

                        /*Deletes the hiker's photo from storage if exists*/

                        hiker.photoUrl?.let { photoUrl ->
                            launch {
                                try {
                                    storageRepository.deleteImage(photoUrl)
                                } catch (e: Exception) {
                                    Log.w(TAG, "Failed to delete photo at url $photoUrl : ${e.message}")
                                }
                            }.join()
                        }

                        /*Then deletes the hiker's profile and the user account*/

                        launch { hikerRepository.deleteHiker(hiker) }.join()
                        launch { authRepository.deleteUserAccount() }.join()
                        CurrentHikerInfo.currentHiker = null
                    }
                    else {
                        throw NullPointerException(EXCEPTION_MESSAGE_NULL_HIKER)
                    }
                }
                else {
                    throw NullPointerException(EXCEPTION_MESSAGE_NULL_USER)
                }
            }
            catch(e:Exception){
                throw e
            }
        }
    }

    /**
     * Sends a message to the given interlocutor
     * @param interlocutor : the interlocutor
     * @param text : the text
     * @throws IllegalArgumentException if the text is empty
     * @throws NullPointerException if the current user or the interlocutor is null
     * @throws Exception if the request fails
     */

    @Throws(Exception::class)
    suspend fun sendMessage(interlocutor: Hiker?, text: String) {
        withContext(Dispatchers.IO) {
            try {

                /*Checks that the current user and the interlocutor are not null and that the text is not empty*/

                val user = authRepository.getCurrentUser()
                if (user != null) {
                    if (interlocutor != null) {
                        if (text.isNotEmpty()) {

                            /*If no chat exist between the user and the interlocutor, creates it (in both sides)*/

                            var chatUserToInterlocutor = async { hikerRepository.getChatBetweenUsers(user.uid, interlocutor.id) }.await()
                            if (chatUserToInterlocutor == null) {
                                chatUserToInterlocutor = Duo(interlocutor.id, user.uid, interlocutor.name, interlocutor.photoUrl)
                                launch { hikerRepository.createOrUpdateHikerChat(user.uid, chatUserToInterlocutor) }.join()
                            }
                            else {
                                chatUserToInterlocutor.interlocutorName = interlocutor.name
                                chatUserToInterlocutor.interlocutorPhotoUrl = interlocutor.photoUrl
                            }

                            var chatInterlocutorToUser = async { hikerRepository.getChatBetweenUsers(interlocutor.id, user.uid) }.await()
                            if (chatInterlocutorToUser == null) {
                                chatInterlocutorToUser = Duo(user.uid, interlocutor.id, user.displayName, user.photoUrl?.toString())
                                launch { hikerRepository.createOrUpdateHikerChat(interlocutor.id, chatInterlocutorToUser) }.join()
                            }
                            else {
                                chatInterlocutorToUser.interlocutorName = user.displayName
                                chatInterlocutorToUser.interlocutorPhotoUrl = user.photoUrl?.toString()
                            }

                            /*Then creates and sends the message (in both sides)*/

                            val message = Message(
                                text = text,
                                authorId = user.uid,
                                authorName = user.displayName,
                                authorPhotoUrl = user.photoUrl?.toString()
                            )
                            launch { hikerRepository.createOrUpdateHikerMessage(user.uid, interlocutor.id, message) }
                            launch { hikerRepository.createOrUpdateHikerMessage(interlocutor.id, user.uid, message) }

                            /*And updates the chat with the last message (in both sides)*/

                            chatUserToInterlocutor.lastMessage = message
                            chatUserToInterlocutor.lastMessageReadId = message.id
                            chatInterlocutorToUser.lastMessage = message
                            launch { hikerRepository.createOrUpdateHikerChat(user.uid, chatUserToInterlocutor) }.join()
                            launch { hikerRepository.createOrUpdateHikerChat(interlocutor.id, chatInterlocutorToUser) }.join()

                        } else {
                            throw IllegalArgumentException(EXCEPTION_MESSAGE_NO_TEXT_MESSAGE)
                        }
                    }
                    else {
                        throw NullPointerException(EXCEPTION_MESSAGE_NULL_HIKER)
                    }
                }
                else {
                    throw NullPointerException(EXCEPTION_MESSAGE_NULL_USER)
                }
            }
            catch (e: Exception) {
                throw e
            }
        }
    }

    /**
     * Marks the last message as read for the chat with the given interlocutor
     * @param interlocutor : the interlocutor
     * @throws NullPointerException if the user or the hiker is null
     * @throws Exception if the request fails
     */

    suspend fun markLastMessageAsRead(interlocutor: Hiker?) {
        withContext(Dispatchers.IO) {
            try {
                val user = authRepository.getCurrentUser()
                if (user != null) {
                    if (interlocutor != null) {
                        val chat = async { hikerRepository.getChatBetweenUsers(user.uid, interlocutor.id) }.await()
                        chat?.let {
                            chat.lastMessageReadId = chat.lastMessage?.id
                            launch { hikerRepository.createOrUpdateHikerChat(user.uid, chat) }
                        }
                    }
                    else {
                        throw NullPointerException(EXCEPTION_MESSAGE_NULL_HIKER)
                    }
                }
                else {
                    throw NullPointerException(EXCEPTION_MESSAGE_NULL_USER)
                }
            }
            catch (e: Exception) {
                throw e
            }
        }
    }

    /**
     * Deletes a message
     * @param interlocutor : the interlocutor
     * @param message : the message to delete
     * @throws NullPointerException if the current user or the interlocutor is null
     * @throws Exception if the request fails
     */

    @Throws(Exception::class)
    suspend fun deleteMessage(interlocutor: Hiker?, message: Message) {
        withContext(Dispatchers.IO) {
            try {

                /*Checks that the current user and the interlocutor are not null*/

                val user = authRepository.getCurrentUser()
                if (user != null) {
                    if (interlocutor != null) {

                        /*Deletes the message*/

                        launch { hikerRepository.deleteHikerMessage(user.uid, interlocutor.id, message.id) }.join()

                        /*Then updates the last message in the chat (or deletes the chat if there is no remaining message)*/

                        val duo = async { hikerRepository.getChatBetweenUsers(user.uid, interlocutor.id) }.await()
                        if (duo != null) {
                            val lastMessage = async { hikerRepository.getLastHikerMessage(user.uid, interlocutor.id) }.await()
                            if (lastMessage != null) {
                                duo.lastMessage = lastMessage
                                duo.lastMessageReadId = lastMessage.id
                                launch { hikerRepository.createOrUpdateHikerChat(interlocutor.id, duo) }
                            } else {
                                launch { hikerRepository.deleteHikerChat(user.uid, interlocutor.id) }
                            }
                        }
                    }
                    else {
                        throw NullPointerException(EXCEPTION_MESSAGE_NULL_HIKER)
                    }
                }
                else {
                    throw NullPointerException(EXCEPTION_MESSAGE_NULL_USER)
                }
            }
            catch (e: Exception) {
                throw e
            }
        }
    }

    /**
     * Deletes a chat
     * @param interlocutor : the interlocutor
     * @throws NullPointerException if the current user or the interlocutor is null
     * @throws Exception if the request fails
     */

    @Throws(Exception::class)
    suspend fun deleteChat(interlocutor: Hiker?) {
        withContext(Dispatchers.IO) {
            try {
                val user = authRepository.getCurrentUser()
                if (user != null) {
                    if (interlocutor != null) {
                        launch { hikerRepository.deleteHikerChat(user.uid, interlocutor.id) }
                    }
                    else {
                        throw NullPointerException(EXCEPTION_MESSAGE_NULL_HIKER)
                    }
                }
                else {
                    throw NullPointerException(EXCEPTION_MESSAGE_NULL_USER)
                }
            }
            catch (e: Exception) {
                throw e
            }
        }
    }
}