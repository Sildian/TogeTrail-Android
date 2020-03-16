package com.sildian.apps.togetrail.event.detail

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity

import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.common.flows.BaseDataFlowFragment
import com.sildian.apps.togetrail.common.utils.DateUtilities
import com.sildian.apps.togetrail.common.utils.cloudHelpers.RecyclerViewFirebaseHelper
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
 * @param hiker : the current hiker
 ************************************************************************************************/

class EventFragment(
    private var event: Event?=null,
    private var hiker:Hiker?=null
) :
    BaseDataFlowFragment(),
    HikerPhotoViewHolder.OnHikerClickListener,
    HikerPhotoAdapter.OnHikersChangedListener,
    TrailHorizontalViewHolder.OnTrailClickListener
{

    /**********************************Static items**********************************************/

    companion object {

        /**Logs**/
        private const val TAG_FRAGMENT = "TAG_FRAGMENT"
        private const val TAG_DATA="TAG_DATA"
    }

    /**********************************UI component**********************************************/

    private lateinit var layout:View
    private val photoImageView by lazy {layout.fragment_event_image_view_photo}
    private val registerUserButton by lazy {layout.fragment_event_button_register_user}
    private val nameText by lazy {layout.fragment_event_text_name}
    private val nbDaysText by lazy {layout.fragment_event_text_nb_days}
    private val beginDateText by lazy {layout.fragment_event_text_begin_date}
    private val meetingPointText by lazy {layout.fragment_event_text_meeting_point}
    private val registeredHikersText by lazy {layout.fragment_event_text_registered_hikers}
    private val registeredHikersRecyclerView by lazy {layout.fragment_event_recycler_view_registered_hikers}
    private val userRegisterLayout by lazy {layout.fragment_event_layout_user_registered}
    private val unregisterUserButton by lazy {layout.fragment_event_button_unregister_user}
    private lateinit var registeredHikersAdapter:HikerPhotoAdapter
    private val attachedTrailsRecyclerView by lazy {layout.fragment_event_recycler_view_attached_trails}
    private lateinit var attachedTrailsAdapter:TrailHorizontalAdapter
    private val descriptionText by lazy {layout.fragment_event_text_description}

    /************************************Life cycle**********************************************/

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(TAG_FRAGMENT, "Fragment '${javaClass.simpleName}' created")
        this.layout=inflater.inflate(R.layout.fragment_event, container, false)
        updateUI()
        return this.layout
    }

    /***********************************Data monitoring******************************************/

    fun updateEvent(event:Event?){
        this.event=event
        updateUI()
    }

    /***********************************UI monitoring********************************************/

    private fun updateUI(){
        updatePhotoImageView()
        updateRegisterUserButton()
        updateNameText()
        updateNbDaysText()
        updateBeginDateText()
        updateMeetingPointText()
        updateRegisteredHikersText()
        updateRegisteredHikersRecyclerView()
        updateUnregisterUserButton()
        updateAttachedTrailsRecyclerView()
        updateDescriptionText()
    }

    private fun updatePhotoImageView(){
        //TODO implement
    }

    private fun updateRegisterUserButton(){
        this.registerUserButton.setOnClickListener {
            (activity as EventActivity).registerUserToEvent()
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
        this.meetingPointText.text=this.event?.meetingPoint?.getFullLocation()
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

    private fun updateRegisteredHikersRecyclerView(){
        this.registeredHikersAdapter= HikerPhotoAdapter(
            RecyclerViewFirebaseHelper.generateOptionsForAdapter(
                Hiker::class.java,
                EventFirebaseQueries.getRegisteredHikers(this.event?.id!!),
                activity as AppCompatActivity
            ),
            this,
            this
        )
        this.registeredHikersRecyclerView.adapter=this.registeredHikersAdapter
    }

    private fun updateUnregisterUserButton(){
        this.unregisterUserButton.setOnClickListener {
            (activity as EventActivity).unregisterUserFromEvent()
        }
    }

    private fun updateAttachedTrailsRecyclerView(){
        this.attachedTrailsAdapter= TrailHorizontalAdapter(
            RecyclerViewFirebaseHelper.generateOptionsForAdapter(
                Trail::class.java,
                EventFirebaseQueries.getAttachedTrails(this.event?.id!!),
                activity as AppCompatActivity
            ),
            this
        )
        this.attachedTrailsRecyclerView.adapter=this.attachedTrailsAdapter
    }

    private fun updateDescriptionText(){
        this.descriptionText.text=this.event?.description
    }

    private fun updateUserRegisterItemsVisibility(){
        val userIsRegistered=
            this.registeredHikersAdapter.snapshots.firstOrNull { it.id==this.hiker?.id } !=null
        if(userIsRegistered){
            this.registerUserButton.visibility=View.GONE
            this.userRegisterLayout.visibility=View.VISIBLE
        }else{
            this.registerUserButton.visibility=View.VISIBLE
            this.userRegisterLayout.visibility=View.GONE
        }
    }

    /***********************************Hikers monitoring****************************************/

    override fun onHikerClick(hiker: Hiker) {
        //TODO implement
    }

    override fun onHikersChanged() {
        Log.d(TAG_DATA, "Registered hikers changed")
        updateRegisteredHikersText()
        updateUserRegisterItemsVisibility()
    }

    /***********************************Trails monitoring****************************************/

    override fun onTrailClick(trail: Trail) {
        //TODO implement
    }
}
