package com.sildian.apps.togetrail.event.edit

import androidx.appcompat.app.AppCompatActivity
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.common.flows.BaseDataFlowFragment
import com.sildian.apps.togetrail.common.utils.DateUtilities
import com.sildian.apps.togetrail.common.utils.cloudHelpers.DatabaseFirebaseHelper
import com.sildian.apps.togetrail.common.utils.uiHelpers.DialogHelper
import com.sildian.apps.togetrail.common.utils.uiHelpers.PickerHelper
import com.sildian.apps.togetrail.event.model.core.Event
import com.sildian.apps.togetrail.event.model.support.EventFirebaseQueries
import com.sildian.apps.togetrail.location.model.core.Location
import com.sildian.apps.togetrail.trail.model.core.Trail
import com.sildian.apps.togetrail.trail.others.TrailHorizontalAdapter
import com.sildian.apps.togetrail.trail.others.TrailHorizontalAdapterOffline
import com.sildian.apps.togetrail.trail.others.TrailHorizontalViewHolder
import kotlinx.android.synthetic.main.fragment_event_edit.view.*

/*************************************************************************************************
 * Allows to edit an event
 * @param event : the related event
 ************************************************************************************************/

class EventEditFragment(private val event: Event?=null) :
    BaseDataFlowFragment(),
    TrailHorizontalViewHolder.OnTrailClickListener,
    TrailHorizontalViewHolder.OnTrailRemovedListener
{

    /**********************************Static items**********************************************/

    companion object {

        /**Logs**/
        private const val TAG="EventEditFragment"
    }

    /**************************************Data**************************************************/

    private val attachedTrails= arrayListOf<Trail>()    //The list of attached trails (useful only when the event has no id yet)

    /**********************************UI component**********************************************/

    private val nameTextField by lazy {layout.fragment_event_edit_text_field_name}
    private val beginDateTextField by lazy {layout.fragment_event_edit_text_field_dropdown_begin_date}
    private val beginTimeTextField by lazy {layout.fragment_event_edit_text_field_dropdown_begin_time}
    private val endDateTextField by lazy {layout.fragment_event_edit_text_field_dropdown_end_date}
    private val endTimeTextField by lazy {layout.fragment_event_edit_text_field_dropdown_end_time}
    private val attachedTrailsRecyclerView by lazy {layout.fragment_event_edit_recycler_view_attached_trails}
    private lateinit var attachedTrailsAdapter:TrailHorizontalAdapter
    private lateinit var attachedTrailsAdapterOffline:TrailHorizontalAdapterOffline
    private val addTrailsButton by lazy {layout.fragment_event_edit_button_add_trails}
    private val meetingPointTextField by lazy {layout.fragment_event_edit_text_field_meeting_point}
    private val descriptionTextField by lazy {layout.fragment_event_edit_text_field_description}

    /*********************************Data monitoring********************************************/

    override fun updateData(data:Any?) {
        if(data is Location){
            this.event?.meetingPoint=data
            updateMeetingPointTextField()
        }
    }

    override fun saveData() {
        this.event?.name=this.nameTextField.text.toString()
        updateDates()
        this.event?.description=this.descriptionTextField.text.toString()
        if(this.event?.id==null){
            (activity as EventEditActivity).updateAttachedTrailsToUpdate(this.attachedTrails)
        }
        (activity as EventEditActivity).saveEventInDatabase()
    }

    /***********************************UI monitoring********************************************/

    override fun getLayoutId(): Int = R.layout.fragment_event_edit

    override fun initializeUI() {
        initializeAttachedTrailsRecyclerView()
        initializeAddTrailsButton()
        refreshUI()
    }

    override fun refreshUI() {
        updateNameTextField()
        updateBeginDateTextField()
        updateBeginTimeTextField()
        updateEndDateTextField()
        updateEndTimeTextField()
        updateMeetingPointTextField()
        updateDescriptionTextField()
    }

    private fun updateNameTextField(){
        this.nameTextField.setText(this.event?.name)
    }

    private fun updateBeginDateTextField(){
        PickerHelper.populateEditTextWithDatePicker(
            this.beginDateTextField, activity as AppCompatActivity, this.event?.beginDate
        )
    }

    private fun updateBeginTimeTextField(){
        PickerHelper.populateEditTextWithTimePicker(
            this.beginTimeTextField, activity as AppCompatActivity, this.event?.beginDate
        )
    }

    private fun updateEndDateTextField(){
        PickerHelper.populateEditTextWithDatePicker(
            this.endDateTextField, activity as AppCompatActivity, this.event?.endDate
        )
    }

    private fun updateEndTimeTextField(){
        PickerHelper.populateEditTextWithTimePicker(
            this.endTimeTextField, activity as AppCompatActivity, this.event?.endDate
        )
    }

    private fun initializeAttachedTrailsRecyclerView(){

        /*If the event is not created in the database yet, then sets an offline adapter*/

        if(this.event?.id==null){
            this.attachedTrailsAdapterOffline=
                TrailHorizontalAdapterOffline(this.attachedTrails, this, true, this)
            this.attachedTrailsRecyclerView.adapter=this.attachedTrailsAdapterOffline

            /*Else sets an online adapter*/

        }else {
            this.attachedTrailsAdapter=
                TrailHorizontalAdapter(
                    DatabaseFirebaseHelper.generateOptionsForAdapter(
                        Trail::class.java,
                        EventFirebaseQueries.getAttachedTrails(this.event.id.toString()),
                        activity as AppCompatActivity
                ), this, true, this
            )
            this.attachedTrailsRecyclerView.adapter=this.attachedTrailsAdapter
        }
    }

    private fun initializeAddTrailsButton(){
        this.addTrailsButton.setOnClickListener {
            showTrailsList()
        }
    }

    private fun updateMeetingPointTextField(){
        this.meetingPointTextField.setText(this.event?.meetingPoint?.fullAddress)
        this.meetingPointTextField.setOnClickListener {
            (activity as EventEditActivity).searchLocation()
        }
    }

    private fun updateDescriptionTextField(){
        this.descriptionTextField.setText(this.event?.description)
    }

    /*****************************Trails / Events monitoring************************************/

    /**Updates the begin and end dates**/

    private fun updateDates(){

        /*If the dates and times fields are not empty, refreshes the event's dates*/

        if(!(this.beginDateTextField.text.isNullOrEmpty()
            ||this.beginTimeTextField.text.isNullOrEmpty()
            ||this.endDateTextField.text.isNullOrEmpty()
            ||this.endTimeTextField.text.isNullOrEmpty()))
        {
            val beginDate=DateUtilities.getDateFromString(this.beginDateTextField.text.toString())
            val beginTime=DateUtilities.getTimeFromString(this.beginTimeTextField.text.toString())
            val endDate=DateUtilities.getDateFromString(this.endDateTextField.text.toString())
            val endTime=DateUtilities.getTimeFromString(this.endTimeTextField.text.toString())
            this.event?.beginDate=DateUtilities.mergeDateAndTime(beginDate!!, beginTime!!)
            this.event?.endDate=DateUtilities.mergeDateAndTime(endDate!!, endTime!!)
        }
    }

    /**Runs a dialog allowing to add a trail to the event**/

    private fun showTrailsList() {
        DialogHelper
            .createTrailSelectionDialog(
                activity as AppCompatActivity,
                this::addTrail)
            .show()
    }

    /**Adds a trail to the event**/

    private fun addTrail(trail:Trail){

        /*Updates some data in the event with the given trail*/

        updateEventPosition(trail)
        updateEventMainPhotoUrl(trail)

        /*If the event has no id yet, updates the offline adapter. Else updates the attached trail in the database*/

        if(this.event?.id==null){
            this.attachedTrails.add(trail)
            this.attachedTrailsAdapterOffline.notifyDataSetChanged()
        }
        else{
            (activity as EventEditActivity).updateAttachedTrail(trail)
        }
    }

    /**On trail click**/

    override fun onTrailClick(trail: Trail) {
        trail.id?.let { id ->
            (activity as EventEditActivity).seeTrail(id)
        }
    }

    /**Removes a trail from the event**/

    override fun onTrailRemoved(trail: Trail) {

        /*If the event has no id yet, updates the offline adapter. Else updates the attached trail in the database*/

        if(this.event?.id==null){
            this.attachedTrails.removeAll { it.id==trail.id }
            this.attachedTrailsAdapterOffline.notifyDataSetChanged()
        }
        else{
            (activity as EventEditActivity).deleteAttachedTrail(trail)
        }
    }

    /**Updates the event's position with the given trail**/

    private fun updateEventPosition(trail:Trail){
        this.event?.position=trail.position
    }

    /**Updates the event's main photo url with the first photo url found in the given trail**/

    private fun updateEventMainPhotoUrl(trail: Trail){
        this.event?.mainPhotoUrl=trail.getFirstPhotoUrl()
    }
}
