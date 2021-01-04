package com.sildian.apps.togetrail.event.detail

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.common.baseControllers.BaseActivity
import com.sildian.apps.togetrail.common.baseControllers.BaseFragment
import com.sildian.apps.togetrail.common.baseViewModels.ViewModelFactory
import com.sildian.apps.togetrail.common.utils.cloudHelpers.AuthRepository
import com.sildian.apps.togetrail.common.utils.cloudHelpers.DatabaseFirebaseHelper
import com.sildian.apps.togetrail.databinding.DialogFragmentEventMessageBinding
import com.sildian.apps.togetrail.databinding.FragmentEventBinding
import com.sildian.apps.togetrail.event.model.support.EventFirebaseQueries
import com.sildian.apps.togetrail.event.model.support.EventViewModel
import com.sildian.apps.togetrail.hiker.model.core.Hiker
import com.sildian.apps.togetrail.hiker.others.HikerPhotoAdapter
import com.sildian.apps.togetrail.hiker.others.HikerPhotoViewHolder
import com.sildian.apps.togetrail.trail.model.core.Trail
import com.sildian.apps.togetrail.trail.others.TrailHorizontalAdapter
import com.sildian.apps.togetrail.trail.others.TrailHorizontalViewHolder
import kotlinx.android.synthetic.main.dialog_fragment_event_message.view.*
import kotlinx.android.synthetic.main.fragment_event.view.*

/*************************************************************************************************
 * Displays an event's detail info and allows a user to register on this event
 * @param eventId : the event's id
 ************************************************************************************************/

class EventFragment(private val eventId: String?=null) :
    BaseFragment(),
    HikerPhotoViewHolder.OnHikerClickListener,
    HikerPhotoAdapter.OnHikersChangedListener,
    TrailHorizontalViewHolder.OnTrailClickListener
{

    /*****************************************Data***********************************************/

    private lateinit var eventViewModel: EventViewModel

    /**********************************UI component**********************************************/

    private val toolbar by lazy {layout.fragment_event_toolbar}
    private val photoImageView by lazy {layout.fragment_event_image_view_photo}
    private val registeredHikersRecyclerView by lazy {layout.fragment_event_recycler_view_registered_hikers}
    private lateinit var registeredHikersAdapter:HikerPhotoAdapter
    private val attachedTrailsRecyclerView by lazy {layout.fragment_event_recycler_view_attached_trails}
    private lateinit var attachedTrailsAdapter:TrailHorizontalAdapter
    private val registrationLayout by lazy {layout.fragment_event_layout_registration}
    private val registerUserButton by lazy {layout.fragment_event_button_register_user}
    private val userRegisteredText by lazy {layout.fragment_event_text_user_registered}
    private val unregisterUserButton by lazy {layout.fragment_event_button_unregister_user}
    private val eventMessageDialogFragment by lazy { EventMessageDialogFragment() }

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
        initializeRegistrationLayout()
    }

    override fun refreshUI() {
        updateToolbar()
        updatePhotoImageView()
        updateRegisteredHikersRecyclerView()
        updateAttachedTrailsRecyclerView()
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

    private fun initializeRegistrationLayout(){
        if(AuthRepository().getCurrentUser()==null){
            this.registrationLayout.visibility=View.GONE
        }
    }

    private fun updatePhotoImageView(){
        if(this.eventViewModel.event.value?.mainPhotoUrl!=null) {
            Glide.with(context!!)
                .load(this.eventViewModel.event.value?.mainPhotoUrl)
                .apply(RequestOptions.centerCropTransform())
                .placeholder(R.drawable.ic_trail_white)
                .into(this.photoImageView)
        }else{
            this.photoImageView.setImageResource(R.drawable.ic_trail_white)
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
        this.eventMessageDialogFragment.show(childFragmentManager, "EventMessageDialogFragment")
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

    fun sendMessage(text: String) {
        this.eventViewModel.sendMessage(text)
    }

    class EventMessageDialogFragment: BottomSheetDialogFragment() {

        private lateinit var layout: View
        private lateinit var messageTextField: EditText

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
            val binding: DialogFragmentEventMessageBinding = DataBindingUtil.inflate(inflater, R.layout.dialog_fragment_event_message, container, false)
            binding.eventMessageDialogFragment = this
            this.layout = binding.root
            return this.layout
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            this.messageTextField = this.layout.dialog_fragment_event_message_text_field_message
        }

        override fun onDismiss(dialog: DialogInterface) {
            (activity as BaseActivity).hideKeyboard()
            super.onDismiss(dialog)
        }

        @Suppress("UNUSED_PARAMETER")
        fun onCancelMessageButtonClick(view: View) {
            this.messageTextField.setText("")
            dismiss()
        }

        @Suppress("UNUSED_PARAMETER")
        fun onValidateMessageButtonClick(view: View) {
            val text = this.messageTextField.text.toString()
            if (text.isNotEmpty()) {
                (parentFragment as EventFragment).sendMessage(this.messageTextField.text.toString())
                this.messageTextField.setText("")
                dismiss()
            }
        }
    }
}
