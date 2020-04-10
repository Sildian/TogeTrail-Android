package com.sildian.apps.togetrail.event.detail

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.common.flows.BaseDataFlowFragment
import com.sildian.apps.togetrail.common.utils.DateUtilities
import com.sildian.apps.togetrail.common.utils.cloudHelpers.AuthFirebaseHelper
import com.sildian.apps.togetrail.common.utils.cloudHelpers.DatabaseFirebaseHelper
import com.sildian.apps.togetrail.event.model.core.Event
import com.sildian.apps.togetrail.event.model.support.EventFirebaseQueries
import com.sildian.apps.togetrail.hiker.model.core.Hiker
import com.sildian.apps.togetrail.hiker.others.HikerPhotoAdapter
import com.sildian.apps.togetrail.hiker.others.HikerPhotoViewHolder
import com.sildian.apps.togetrail.trail.model.core.Trail
import com.sildian.apps.togetrail.trail.others.TrailHorizontalAdapter
import com.sildian.apps.togetrail.trail.others.TrailHorizontalViewHolder
import kotlinx.android.synthetic.main.fragment_event.view.*

/*************************************************************************************************
 * Displays an event's detail info and allows a user to register on this event
 * @param event : the related event
 ************************************************************************************************/

class EventFragment(
    private var event: Event?=null
) :
    BaseDataFlowFragment(),
    HikerPhotoViewHolder.OnHikerClickListener,
    HikerPhotoAdapter.OnHikersChangedListener,
    TrailHorizontalViewHolder.OnTrailClickListener
{

    /**********************************UI component**********************************************/

    private val toolbar by lazy {layout.fragment_event_toolbar}
    private val photoImageView by lazy {layout.fragment_event_image_view_photo}
    private val nameText by lazy {layout.fragment_event_text_name}
    private val nbDaysText by lazy {layout.fragment_event_text_nb_days}
    private val beginDateText by lazy {layout.fragment_event_text_begin_date}
    private val meetingPointText by lazy {layout.fragment_event_text_meeting_point}
    private val registeredHikersText by lazy {layout.fragment_event_text_registered_hikers}
    private val registeredHikersRecyclerView by lazy {layout.fragment_event_recycler_view_registered_hikers}
    private lateinit var registeredHikersAdapter:HikerPhotoAdapter
    private val attachedTrailsRecyclerView by lazy {layout.fragment_event_recycler_view_attached_trails}
    private lateinit var attachedTrailsAdapter:TrailHorizontalAdapter
    private val descriptionText by lazy {layout.fragment_event_text_description}
    private val registerUserButton by lazy {layout.fragment_event_button_register_user}
    private val userRegisteredText by lazy {layout.fragment_event_text_user_registered}
    private val unregisterUserButton by lazy {layout.fragment_event_button_unregister_user}

    /***********************************Data monitoring******************************************/

    override fun updateData(data: Any?) {
        if(data is Event){
            this.event=data
            refreshUI()
        }
    }

    /***********************************UI monitoring********************************************/

    override fun getLayoutId(): Int = R.layout.fragment_event

    override fun initializeUI() {
        initializeToolbar()
        initializeRegisteredHikersRecyclerView()
        initializeAttachedTrailsRecyclerView()
        refreshUI()
    }

    override fun refreshUI() {
        updatePhotoImageView()
        updateNameText()
        updateNbDaysText()
        updateBeginDateText()
        updateMeetingPointText()
        updateRegisteredHikersText()
        updateDescriptionText()
        updateRegisterUserButton()
        updateUnregisterUserButton()
    }

    private fun initializeToolbar(){
        (activity as EventActivity).setSupportActionBar(this.toolbar)
        (activity as EventActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as EventActivity).supportActionBar?.setTitle(R.string.toolbar_event)
    }

    private fun updatePhotoImageView(){
        if(this.event?.mainPhotoUrl!=null) {
            Glide.with(context!!)
                .load(this.event?.mainPhotoUrl)
                .apply(RequestOptions.centerCropTransform())
                .placeholder(R.drawable.ic_trail_white)
                .into(this.photoImageView)
        }else{
            this.photoImageView.setImageResource(R.drawable.ic_trail_white)
        }
    }

    private fun updateNameText(){
        this.nameText.text=this.event?.name
    }

    private fun updateNbDaysText(){
        if(this.event?.getNbDays()!=null){
            val nbDays=this.event?.getNbDays()
            val metric=if(nbDays!!>1){
                resources.getString(R.string.label_event_days)
            }else{
                resources.getString(R.string.label_event_day)
            }
            val nbDaysToDisplay="$nbDays $metric"
            this.nbDaysText.text=nbDaysToDisplay
        }else{
            this.nbDaysText.text=""
        }
    }

    private fun updateBeginDateText(){
        this.beginDateText.text=DateUtilities.displayDateAndTimeFull(this.event?.beginDate!!)
    }

    private fun updateMeetingPointText(){
        this.meetingPointText.text=this.event?.meetingPoint?.fullAddress
    }

    private fun updateRegisteredHikersText(){
        val nbHikers=this.event?.nbHikersRegistered!!
        val metric=if(nbHikers>1)
            resources.getString(R.string.label_event_registered_hikers_plur)
        else
            resources.getString(R.string.label_event_registered_hikers_sing)
        val nbHikersToDisplay="$nbHikers $metric"
        this.registeredHikersText.text=nbHikersToDisplay
    }

    private fun initializeRegisteredHikersRecyclerView(){
        this.registeredHikersAdapter= HikerPhotoAdapter(
            DatabaseFirebaseHelper.generateOptionsForAdapter(
                Hiker::class.java,
                EventFirebaseQueries.getRegisteredHikers(this.event?.id!!),
                activity as AppCompatActivity
            ),
            this,
            this
        )
        this.registeredHikersRecyclerView.adapter=this.registeredHikersAdapter
    }

    private fun updateDescriptionText(){
        this.descriptionText.text=this.event?.description
    }

    private fun updateRegisterUserButton(){
        this.registerUserButton.setOnClickListener {
            (activity as EventActivity).registerUserToEvent()
        }
    }

    private fun updateUnregisterUserButton(){
        this.unregisterUserButton.setOnClickListener {
            (activity as EventActivity).unregisterUserFromEvent()
        }
    }

    private fun initializeAttachedTrailsRecyclerView(){
        this.attachedTrailsAdapter= TrailHorizontalAdapter(
            DatabaseFirebaseHelper.generateOptionsForAdapter(
                Trail::class.java,
                EventFirebaseQueries.getAttachedTrails(this.event?.id!!),
                activity as AppCompatActivity
            ),
            this
        )
        this.attachedTrailsRecyclerView.adapter=this.attachedTrailsAdapter
    }

    private fun updateUserRegisterItemsVisibility(){
        val user=AuthFirebaseHelper.getCurrentUser()
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

    /***********************************Hikers monitoring****************************************/

    override fun onHikerClick(hiker: Hiker) {
        (activity as EventActivity).seeHiker(hiker.id)
    }

    override fun onHikersChanged() {
        updateRegisteredHikersText()
        updateUserRegisterItemsVisibility()
    }

    /***********************************Trails monitoring****************************************/

    override fun onTrailClick(trail: Trail) {
        trail.id?.let { id ->
            (activity as EventActivity).seeTrail(id)
        }
    }
}
