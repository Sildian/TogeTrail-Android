package com.sildian.apps.togetrail.event.detail

import android.content.DialogInterface
import android.content.Intent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProviders
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.chat.model.core.Message
import com.sildian.apps.togetrail.chat.others.MessageWriteDialogFragment
import com.sildian.apps.togetrail.chat.others.PublicMessageAdapter
import com.sildian.apps.togetrail.common.baseControllers.BaseFragment
import com.sildian.apps.togetrail.common.baseViewModels.ViewModelFactory
import com.sildian.apps.togetrail.common.utils.GoogleMapUrlHelper
import com.sildian.apps.togetrail.common.utils.cloudHelpers.DatabaseFirebaseHelper
import com.sildian.apps.togetrail.common.utils.uiHelpers.DialogHelper
import com.sildian.apps.togetrail.databinding.FragmentEventBinding
import com.sildian.apps.togetrail.event.model.support.EventFirebaseQueries
import com.sildian.apps.togetrail.event.model.support.EventViewModel
import com.sildian.apps.togetrail.hiker.model.core.Hiker
import com.sildian.apps.togetrail.hiker.model.support.CurrentHikerInfo
import com.sildian.apps.togetrail.hiker.others.HikerPhotoAdapter
import com.sildian.apps.togetrail.trail.model.core.Trail
import com.sildian.apps.togetrail.trail.others.TrailHorizontalAdapter
import com.sildian.apps.togetrail.trail.others.TrailHorizontalViewHolder

/*************************************************************************************************
 * Displays an event's detail info and allows a user to register on this event
 * @param eventId : the event's id
 ************************************************************************************************/

class EventFragment(private val eventId: String?=null) :
    BaseFragment<FragmentEventBinding>(),
    HikerPhotoAdapter.OnHikerClickListener,
    HikerPhotoAdapter.OnHikersChangedListener,
    TrailHorizontalViewHolder.OnTrailClickListener,
    PublicMessageAdapter.OnAuthorClickListener,
    PublicMessageAdapter.OnMessageModificationClickListener,
    MessageWriteDialogFragment.MessageWriteCallback
{

    /*****************************************Data***********************************************/

    private lateinit var eventViewModel: EventViewModel
    val isCurrentUserRegistered = MutableLiveData(false)
    val isCurrentUserAuthor = MutableLiveData(false)

    /**********************************UI component**********************************************/

    private lateinit var registeredHikersAdapter:HikerPhotoAdapter
    private lateinit var attachedTrailsAdapter:TrailHorizontalAdapter
    private lateinit var messagesAdapter: PublicMessageAdapter
    private var messageWriteDialogFragment: MessageWriteDialogFragment? = null

    /***********************************Data monitoring******************************************/

    override fun loadData() {
        initializeData()
        observeEvent()
        observeRequestFailure()
        loadEvent()
    }

    private fun initializeData() {
        this.eventViewModel=ViewModelProviders
            .of(this, ViewModelFactory)
            .get(EventViewModel::class.java)
        this.binding.eventFragment = this
        this.binding.eventViewModel = this.eventViewModel
    }

    private fun observeEvent() {
        this.eventViewModel.event.observe(this) { event ->
            if (event != null) {
                isCurrentUserAuthor.value = event.authorId == CurrentHikerInfo.currentHiker?.id
                refreshUI()
            }
        }
    }

    private fun observeRequestFailure() {
        this.eventViewModel.requestFailure.observe(this) { e ->
            if (e != null) {
                onQueryError(e)
            }
        }
    }

    private fun loadEvent() {
        this.eventId?.let { eventId ->
            this.eventViewModel.loadEventFromDatabaseRealTime(eventId)
        }
    }

    /***********************************UI monitoring********************************************/

    override fun getLayoutId(): Int = R.layout.fragment_event

    override fun initializeUI() {
        initializeToolbar()
    }

    override fun refreshUI() {
        updateToolbar()
        updateRegisteredHikersRecyclerView()
        updateAttachedTrailsRecyclerView()
        updateMessagesRecyclerView()
    }

    private fun initializeToolbar() {
        (activity as EventActivity).setSupportActionBar(this.binding.fragmentEventToolbar)
        (activity as EventActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun updateToolbar() {
        if (this.eventViewModel.currentUserIsAuthor()) {
            (this.baseActivity as EventActivity).allowEditMenu()
        }
    }

    private fun updateRegisteredHikersRecyclerView(){
        this.registeredHikersAdapter= HikerPhotoAdapter(
            DatabaseFirebaseHelper.generateOptionsForAdapter(
                Hiker::class.java,
                EventFirebaseQueries.getRegisteredHikers(this.eventViewModel.event.value?.id!!),
                activity as AppCompatActivity
            ),
            this,
            this
        )
        this.binding.fragmentEventRecyclerViewRegisteredHikers.adapter = this.registeredHikersAdapter
    }

    private fun updateAttachedTrailsRecyclerView(){
        this.attachedTrailsAdapter= TrailHorizontalAdapter(
            DatabaseFirebaseHelper.generateOptionsForAdapter(
                Trail::class.java,
                EventFirebaseQueries.getAttachedTrails(this.eventViewModel.event.value?.id!!),
                activity as AppCompatActivity
            ),
            this
        )
        this.binding.fragmentEventRecyclerViewAttachedTrails.adapter = this.attachedTrailsAdapter
    }

    private fun updateMessagesRecyclerView() {
        this.messagesAdapter = PublicMessageAdapter(
            DatabaseFirebaseHelper.generateOptionsForAdapter(
                Message::class.java,
                EventFirebaseQueries.getMessages(this.eventViewModel.event.value?.id!!),
                activity as AppCompatActivity
            ),
            this, this
        )
        this.binding.fragmentEventRecyclerViewMessages.adapter = this.messagesAdapter
    }

    @Suppress("UNUSED_PARAMETER")
    fun onAuthorPhotoButtonClick(view: View) {
        this.eventViewModel.event.value?.authorId?.let { authorId ->
            (activity as EventActivity).seeHiker(authorId)
        }
    }

    @Suppress("UNUSED_PARAMETER")
    fun onGoToMeetingPointButtonClick(view: View) {
        this.eventViewModel.event.value?.meetingPoint?.fullAddress?.let { fullAddress ->
            val url = GoogleMapUrlHelper.generateWithFullAddress(fullAddress)
            val googleMapIntent = Intent(Intent.ACTION_VIEW, url.toUri())
            startActivity(googleMapIntent)
        }
    }

    @Suppress("UNUSED_PARAMETER")
    fun onSendMessageButtonClick(view: View) {
        this.messageWriteDialogFragment = MessageWriteDialogFragment(this)
        this.messageWriteDialogFragment?.show(childFragmentManager, "EventMessageDialogFragment")
    }

    @Suppress("UNUSED_PARAMETER")
    fun onRegisterUserButtonClick(view:View){
        this.eventViewModel.registerUserToEvent()
    }

    @Suppress("UNUSED_PARAMETER")
    fun onUnregisterUserButtonClick(view:View){
        this.eventViewModel.unregisterUserFromEvent()
    }

    /***********************************Hikers monitoring****************************************/

    override fun onHikerClick(hiker: Hiker) {
        (activity as EventActivity).seeHiker(hiker.id)
    }

    override fun onHikersChanged() {
        this.isCurrentUserRegistered.value =
            this.registeredHikersAdapter.snapshots.firstOrNull { it.id == CurrentHikerInfo.currentHiker?.id } != null
    }

    /***********************************Trails monitoring****************************************/

    override fun onTrailClick(trail: Trail) {
        trail.id?.let { id ->
            (activity as EventActivity).seeTrail(id)
        }
    }

    /*************************************Chat monitoring****************************************/

    override fun onAuthorClick(authorId: String) {
        (activity as EventActivity).seeHiker(authorId)
    }

    override fun onMessageEditClick(message: Message) {
        this.messageWriteDialogFragment = MessageWriteDialogFragment(this, message)
        this.messageWriteDialogFragment?.show(childFragmentManager, "EventMessageDialogFragment")
    }

    @Suppress("UNUSED_ANONYMOUS_PARAMETER")
    override fun onMessageDeleteClick(message: Message) {
        context?.let { context ->
            DialogHelper.createYesNoDialog(
                context,
                R.string.message_chat_message_delete_confirmation_title,
                R.string.message_chat_message_delete_confirmation_message
            ) { dialog, which ->
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    this.eventViewModel.deleteMessage(message)
                }
            }.show()
        }
    }

    override fun sendMessage(text: String) {
        this.eventViewModel.sendMessage(text)
    }

    override fun editMessage(message: Message, newText: String) {
        this.eventViewModel.updateMessage(message, newText)
    }
}
