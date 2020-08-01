package com.sildian.apps.togetrail.event.detail

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.Observable
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.common.baseControllers.BaseFragment
import com.sildian.apps.togetrail.common.baseViewModels.ViewModelFactory
import com.sildian.apps.togetrail.common.utils.cloudHelpers.AuthRepository
import com.sildian.apps.togetrail.common.utils.cloudHelpers.DatabaseFirebaseHelper
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
    TrailHorizontalViewHolder.OnTrailClickListener
{

    /*****************************************Data***********************************************/

    private lateinit var eventViewModel:EventViewModel

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

    /***********************************Data monitoring******************************************/

    override fun loadData() {
        this.eventViewModel=ViewModelProviders
            .of(this, ViewModelFactory)
            .get(EventViewModel::class.java)
        (this.binding as FragmentEventBinding).eventFragment=this
        (this.binding as FragmentEventBinding).eventViewModel=this.eventViewModel
        this.eventViewModel.addOnPropertyChangedCallback(object:Observable.OnPropertyChangedCallback(){
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                refreshUI()
            }
        })
        this.eventId?.let { eventId ->
            this.eventViewModel.loadEventFromDatabaseRealTime(eventId, null, this::onQueryError)
        }
    }

    fun currentUserIsAuthor():Boolean = this.eventViewModel.currentUserIsAuthor()

    /***********************************UI monitoring********************************************/

    override fun getLayoutId(): Int = R.layout.fragment_event

    override fun useDataBinding(): Boolean = true

    override fun initializeUI() {
        initializeToolbar()
        initializeRegistrationLayout()
    }

    override fun refreshUI() {
        updatePhotoImageView()
        updateRegisteredHikersRecyclerView()
        updateAttachedTrailsRecyclerView()
    }

    private fun initializeToolbar(){
        (activity as EventActivity).setSupportActionBar(this.toolbar)
        (activity as EventActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun initializeRegistrationLayout(){
        if(AuthRepository.getCurrentUser()==null){
            this.registrationLayout.visibility=View.GONE
        }
    }

    private fun updatePhotoImageView(){
        if(this.eventViewModel.event?.mainPhotoUrl!=null) {
            Glide.with(context!!)
                .load(this.eventViewModel.event?.mainPhotoUrl)
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
                EventFirebaseQueries.getRegisteredHikers(this.eventViewModel.event?.id!!),
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
                EventFirebaseQueries.getAttachedTrails(this.eventViewModel.event?.id!!),
                activity as AppCompatActivity
            ),
            this
        )
        this.attachedTrailsRecyclerView.adapter=this.attachedTrailsAdapter
    }

    private fun updateUserRegisterItemsVisibility(){
        val user=AuthRepository.getCurrentUser()
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

    fun onRegisterUserButtonClick(view:View){
        this.eventViewModel.registerUserToEvent(null, this::onQueryError)
    }

    fun onUnregisterUserButtonClick(view:View){
        this.eventViewModel.unregisterUserFromEvent(null, this::onQueryError)
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
}
