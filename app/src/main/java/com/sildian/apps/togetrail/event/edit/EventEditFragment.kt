package com.sildian.apps.togetrail.event.edit

import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.common.flows.BaseDataFlowFragment
import com.sildian.apps.togetrail.common.utils.DateUtilities
import com.sildian.apps.togetrail.common.utils.cloudHelpers.DatabaseFirebaseHelper
import com.sildian.apps.togetrail.common.utils.uiHelpers.DialogHelper
import com.sildian.apps.togetrail.common.utils.uiHelpers.PickerHelper
import com.sildian.apps.togetrail.common.utils.uiHelpers.TextFieldHelper
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

    /**************************************Data**************************************************/

    private val attachedTrails= arrayListOf<Trail>()    //The list of attached trails (useful only when the event has no id yet)

    /**********************************UI component**********************************************/

    private val nameTextFieldLayout by lazy {layout.fragment_event_edit_text_field_layout_name}
    private val nameTextField by lazy {layout.fragment_event_edit_text_field_name}
    private val beginDateTextFieldLayout by lazy {layout.fragment_event_edit_text_field_layout_begin_date}
    private val beginDateTextField by lazy {layout.fragment_event_edit_text_field_begin_date}
    private val beginTimeTextFieldLayout by lazy {layout.fragment_event_edit_text_field_layout_begin_time}
    private val beginTimeTextField by lazy {layout.fragment_event_edit_text_field_begin_time}
    private val endDateTextFieldLayout by lazy {layout.fragment_event_edit_text_field_layout_end_date}
    private val endDateTextField by lazy {layout.fragment_event_edit_text_field_end_date}
    private val endTimeTextFieldLayout by lazy {layout.fragment_event_edit_text_field_layout_end_time}
    private val endTimeTextField by lazy {layout.fragment_event_edit_text_field_end_time}
    private val attachedTrailsRecyclerView by lazy {layout.fragment_event_edit_recycler_view_attached_trails}
    private lateinit var attachedTrailsAdapter:TrailHorizontalAdapter
    private lateinit var attachedTrailsAdapterOffline:TrailHorizontalAdapterOffline
    private val addTrailsButton by lazy {layout.fragment_event_edit_button_add_trails}
    private val meetingPointTextFieldLayout by lazy {layout.fragment_event_edit_text_field_layout_meeting_point}
    private val meetingPointTextField by lazy {layout.fragment_event_edit_text_field_meeting_point}
    private val descriptionTextFieldLayout by lazy {layout.fragment_event_edit_text_field_layout_description}
    private val descriptionTextField by lazy {layout.fragment_event_edit_text_field_description}

    /*********************************Data monitoring********************************************/

    /**Updates data**/

    override fun updateData(data:Any?) {
        if(data is Location){
            this.event?.meetingPoint=data
            updateMeetingPointTextField()
        }
        else if(data is List<*>){
            if(data.firstOrNull() is Trail){
                attachTrails(data as List<Trail>)
            }
        }
    }

    /**Saves data**/

    override fun saveData() {
        updateDates()
        if(checkDataIsValid()) {
            this.event?.name = this.nameTextField.text.toString()
            this.event?.description = this.descriptionTextField.text.toString()
            if (this.event?.id == null) {
                (activity as EventEditActivity).updateAttachedTrailsToUpdate(this.attachedTrails)
            }
            (activity as EventEditActivity).saveEventInDatabase()
        }
    }

    /**Checks data is valid**/

    override fun checkDataIsValid():Boolean{
        if(checkTextFieldsAreNotEmpty()){
            if(checkBeginDateIsBeforeEndDate()){
                if(checkTrailsAreAttached()){
                    return true
                }else{
                    //TODO handle
                }
            }else{
                //TODO handle
            }
        }
        else{
            //TODO handle
        }
        return false
    }

    /**Checks that no text field is empty**/

    private fun checkTextFieldsAreNotEmpty():Boolean{
        val textFieldsAndLayouts= hashMapOf<TextInputEditText, TextInputLayout>()
        textFieldsAndLayouts[this.nameTextField]=this.nameTextFieldLayout
        textFieldsAndLayouts[this.beginDateTextField]=this.beginDateTextFieldLayout
        textFieldsAndLayouts[this.beginTimeTextField]=this.beginTimeTextFieldLayout
        textFieldsAndLayouts[this.endDateTextField]=this.endDateTextFieldLayout
        textFieldsAndLayouts[this.endTimeTextField]=this.endTimeTextFieldLayout
        textFieldsAndLayouts[this.meetingPointTextField]=this.meetingPointTextFieldLayout
        textFieldsAndLayouts[this.descriptionTextField]=this.descriptionTextFieldLayout
        return TextFieldHelper.checkAllTextFieldsAreNotEmpty(textFieldsAndLayouts)
    }

    /**Checks that the begin date is before the end date**/

    private fun checkBeginDateIsBeforeEndDate():Boolean {
        this.event?.beginDate?.let { beginDate ->
            this.event.endDate?.let { endDate ->
                return beginDate.time < endDate.time
            }
        }
        return false
    }

    /**Checks that at least 1 trail is attached to the event**/

    private fun checkTrailsAreAttached():Boolean{
        val trails=if(this.event?.id==null){
            this.attachedTrails
        }else{
            this.attachedTrailsAdapter.snapshots
        }
        return trails.isNotEmpty()
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
            if(this.event?.id!=null){
                attachedTrails.addAll(this.attachedTrailsAdapter.snapshots)
            }
            (activity as EventEditActivity).selectTrail(this.attachedTrails)
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

    /**
     * Attaches the given list of trails to the event
     * @param trails : the list of trails to attach
     */

    private fun attachTrails(trails:List<Trail>){

        /*Updates some data in the event with the given trail*/

        updateEventPosition(trails.first())
        updateEventMainPhotoUrl(trails.first())

        /*If the event has no id yet, updates the offline adapter*/

        if(this.event?.id==null){
            this.attachedTrails.clear()
            this.attachedTrails.addAll(trails)
            this.attachedTrailsAdapterOffline.notifyDataSetChanged()
        }
        else{

            /*Else, deletes each attached trail which is not in the new list of selected trails*/

            this.attachedTrails.forEach { trail ->
                if(trails.firstOrNull { it.id==trail.id } ==null){
                    (activity as EventEditActivity).deleteAttachedTrail(trail)
                }
            }

            /*And updates the attached trails with each item in the new list of selected trails*/

            trails.forEach { trail ->
                (activity as EventEditActivity).updateAttachedTrail(trail)
            }
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
