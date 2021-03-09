package com.sildian.apps.togetrail.event.detail

import android.content.DialogInterface
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.chat.model.core.Message
import com.sildian.apps.togetrail.chat.others.MessageWriteDialogFragment
import com.sildian.apps.togetrail.chat.others.PublicMessageAdapter
import com.sildian.apps.togetrail.chat.others.PublicMessageViewHolder
import com.sildian.apps.togetrail.common.baseControllers.BaseFragment
import com.sildian.apps.togetrail.common.baseViewModels.ViewModelFactory
import com.sildian.apps.togetrail.common.utils.cloudHelpers.AuthRepository
import com.sildian.apps.togetrail.common.utils.cloudHelpers.DatabaseFirebaseHelper
import com.sildian.apps.togetrail.common.utils.uiHelpers.DialogHelper
import com.sildian.apps.togetrail.databinding.FragmentEventBinding
import com.sildian.apps.togetrail.event.model.support.EventFirebaseQueries
import com.sildian.apps.togetrail.event.model.support.EventViewModel
import com.sildian.apps.togetrail.hiker.model.core.Hiker
import com.sildian.apps.togetrail.hiker.others.HikerPhotoAdapter
import com.sildian.apps.togetrail.hiker.others.HikerPhotoViewHolder
import com.sildian.apps.togetrail.trail.model.core.Trail
import com.sildian.apps.togetrail.trail.others.TrailHorizontalAdapter
import com.sildian.apps.togetrail.trail.others.TrailHorizontalViewHolder
import kotlinx.android.synthetic.main.fragment_event.view.*

/*************************************************************************************************
 * Displays an event's detail info and allows a user to register on this event
 * @param eventId : the event's id
 ************************************************************************************************/

class EventFragment(private val eventId: String?=null) :
    BaseFragment(),
    HikerPhotoViewHolder.OnHikerClickListener,
    HikerPhotoAdapter.OnHikersChangedListener,
    TrailHorizontalViewHolder.OnTrailClickListener,
    PublicMessageViewHolder.OnAuthorClickListener,
    PublicMessageViewHolder.OnMessageModificationClickListener,
    MessageWriteDialogFragment.MessageWriteCallback
{

    /*****************************************Data***********************************************/

    private lateinit var eventViewModel: EventViewModel

    /**********************************UI component**********************************************/

    private val toolbar by lazy {layout.fragment_event_toolbar}
    private val registeredHikersRecyclerView by lazy {layout.fragment_event_recycler_view_registered_hikers}
    private lateinit var registeredHikersAdapter:HikerPhotoAdapter
    private val attachedTrailsRecyclerView by lazy {layout.fragment_event_recycler_view_attached_trails}
    private lateinit var attachedTrailsAdapter:TrailHorizontalAdapter
    private val messagesRecyclerView by lazy { layout.fragment_event_recycler_view_messages }
    private lateinit var messagesAdapter: PublicMessageAdapter
    private val sendMessageButton by lazy { layout.fragment_event_button_send_message }
    private val registrationLayout by lazy {layout.fragment_event_layout_registration}
    private val registerUserButton by lazy {layout.fragment_event_button_register_user}
    private val userRegisteredText by lazy {layout.fragment_event_text_user_registered}
    private val unregisterUserButton by lazy {layout.fragment_event_button_unregister_user}
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
        this.binding.lifecycleOwner = this
        (this.binding as FragmentEventBinding).eventFragment = this
        (this.binding as FragmentEventBinding).eventViewModel = this.eventViewModel
    }

    private fun observeEvent() {
        this.eventViewModel.event.observe(this) { event ->
            if (event != null) {
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

    override fun useDataBinding(): Boolean = true

    override fun initializeUI() {
        initializeToolbar()
        initializeSendMessageButton()
        initializeRegistrationLayout()
    }

    override fun refreshUI() {
        updateToolbar()
        updateRegisteredHikersRecyclerView()
        updateAttachedTrailsRecyclerView()
        updateMessagesRecyclerView()
    }

    private fun initializeToolbar() {
        (activity as EventActivity).setSupportActionBar(this.toolbar)
        (activity as EventActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun updateToolbar() {
        if (this.eventViewModel.currentUserIsAuthor()) {
            (this.baseActivity as EventActivity).allowEditMenu()
        }
    }

    private fun initializeSendMessageButton() {
        if (AuthRepository().getCurrentUser()==null) {
            this.sendMessageButton.visibility = View.GONE
        }
    }

    private fun initializeRegistrationLayout(){
        if(AuthRepository().getCurrentUser()==null){
            this.registrationLayout.visibility=View.GONE
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
        this.registeredHikersRecyclerView.adapter=this.registeredHikersAdapter
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
        this.attachedTrailsRecyclerView.adapter=this.attachedTrailsAdapter
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
        this.messagesRecyclerView.adapter = this.messagesAdapter
    }

    private fun updateUserRegisterItemsVisibility(){
        val user=AuthRepository().getCurrentUser()
        val userIsRegistered=
            this.registeredHikersAdapter.snapshots.firstOrNull { it.id==user?.uid } !=null
        if(userIsRegistered){
            this.registerUserButton.visibility=View.GONE
            this.userRegisteredText.visibility=View.VISIBLE
            this.unregisterUserButton.visibility=View.VISIBLE
        }else{
            this.registerUserButton.visibility=View.VISIBLE
            this.userRegisteredText.visibility=View.GONE
            this.unregisterUserButton.visibility=View.GONE
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
        updateUserRegisterItemsVisibility()
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
