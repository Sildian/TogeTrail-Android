package com.sildian.apps.togetrail.event.edit

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.common.baseControllers.BaseFragment
import com.sildian.apps.togetrail.common.baseViewModels.ViewModelFactory
import com.sildian.apps.togetrail.common.utils.DateUtilities
import com.sildian.apps.togetrail.common.utils.cloudHelpers.DatabaseFirebaseHelper
import com.sildian.apps.togetrail.common.utils.uiHelpers.PickerHelper
import com.sildian.apps.togetrail.common.utils.uiHelpers.SnackbarHelper
import com.sildian.apps.togetrail.common.utils.uiHelpers.TextFieldHelper
import com.sildian.apps.togetrail.databinding.FragmentEventEditBinding
import com.sildian.apps.togetrail.event.model.support.EventFirebaseQueries
import com.sildian.apps.togetrail.event.model.support.EventViewModel
import com.sildian.apps.togetrail.location.model.core.Location
import com.sildian.apps.togetrail.trail.model.core.Trail
import com.sildian.apps.togetrail.trail.others.TrailHorizontalAdapter
import com.sildian.apps.togetrail.trail.others.TrailHorizontalAdapterOffline
import com.sildian.apps.togetrail.trail.others.TrailHorizontalViewHolder
import kotlinx.android.synthetic.main.fragment_event_edit.view.*

/*************************************************************************************************
 * Allows to edit an event
 * @param eventId : the event's id
 ************************************************************************************************/

class EventEditFragment(private val eventId: String?=null) :
    BaseFragment(),
    TrailHorizontalViewHolder.OnTrailClickListener,
    TrailHorizontalViewHolder.OnTrailRemovedListener
{

    /**************************************Data**************************************************/

    private lateinit var eventViewModel: EventViewModel

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
    private val meetingPointTextFieldLayout by lazy {layout.fragment_event_edit_text_field_layout_meeting_point}
    private val meetingPointTextField by lazy {layout.fragment_event_edit_text_field_meeting_point}
    private val descriptionTextFieldLayout by lazy {layout.fragment_event_edit_text_field_layout_description}
    private val descriptionTextField by lazy {layout.fragment_event_edit_text_field_description}
    private val messageView by lazy {layout.fragment_event_edit_view_message}

    /*********************************Data monitoring********************************************/

    override fun loadData() {
        initializeData()
        observeEvent()
        observeSaveRequestSuccess()
        observeRequestFailure()
        loadEvent()
    }

    private fun initializeData() {
        this.eventViewModel= ViewModelProviders
            .of(this, ViewModelFactory)
            .get(EventViewModel::class.java)
        this.binding.lifecycleOwner = this
        (this.binding as FragmentEventEditBinding).eventEditFragment = this
        (this.binding as FragmentEventEditBinding).eventViewModel = this.eventViewModel
    }

    private fun observeEvent() {
        this.eventViewModel.event.observe(this) { event ->
            if (event != null) {
                refreshUI()
            }
        }
    }

    private fun observeSaveRequestSuccess() {
        this.eventViewModel.saveRequestSuccess.observe(this) { success ->
            if (success) {
                onSaveSuccess()
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
        if(this.eventId != null){
            this.eventViewModel.loadEventFromDatabase(eventId)
        }else {
            this.eventViewModel.initNewEvent()
        }
    }

    override fun updateData(data:Any?) {
        if(data is Location){
            this.eventViewModel.event.value?.meetingPoint=data
            this.meetingPointTextField.setText(data.fullAddress)
        }
        else if(data is List<*>){
            if(data.firstOrNull() is Trail){
                attachTrails(data as List<Trail>)
            }
        }
    }

    override fun saveData() {
        updateDates()
        if(checkDataIsValid()) {
            this.eventViewModel.event.value?.name = this.nameTextField.text.toString()
            this.eventViewModel.event.value?.description = this.descriptionTextField.text.toString()
            this.baseActivity?.showProgressDialog()
            this.eventViewModel.saveEventInDatabase()
        }
    }

    override fun checkDataIsValid():Boolean{
        if(checkTextFieldsAreNotEmpty()){
            if(checkBeginDateIsBeforeEndDate()){
                if(checkTrailsAreAttached()){
                    return true
                }else{
                    SnackbarHelper
                        .createSimpleSnackbar(this.messageView, null, R.string.message_event_no_trail_attached)
                        .show()
                }
            }else{
                SnackbarHelper
                    .createSimpleSnackbar(this.messageView, null, R.string.message_event_dates_issue)
                    .show()
            }
        }
        else{
            SnackbarHelper
                .createSimpleSnackbar(this.messageView, null, R.string.message_text_fields_empty)
                .show()
        }
        return false
    }

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

    private fun checkBeginDateIsBeforeEndDate():Boolean {
        this.eventViewModel.event.value?.beginDate?.let { beginDate ->
            this.eventViewModel.event.value?.endDate?.let { endDate ->
                return beginDate.time < endDate.time
            }
        }
        return false
    }

    private fun checkTrailsAreAttached():Boolean{
        val trails=if(this.eventViewModel.event.value?.id == null){
            this.eventViewModel.attachedTrails
        }else{
            this.attachedTrailsAdapter.snapshots
        }
        return trails.isNotEmpty()
    }

    /***********************************UI monitoring********************************************/

    override fun getLayoutId(): Int = R.layout.fragment_event_edit

    override fun useDataBinding(): Boolean = true

    override fun refreshUI() {
        updateBeginDateTextField()
        updateBeginTimeTextField()
        updateEndDateTextField()
        updateEndTimeTextField()
        updateAttachedTrailsRecyclerView()
    }

    private fun updateBeginDateTextField(){
        PickerHelper.populateEditTextWithDatePicker(
            this.beginDateTextField, activity as AppCompatActivity, this.eventViewModel.event.value?.beginDate
        )
    }

    private fun updateBeginTimeTextField(){
        PickerHelper.populateEditTextWithTimePicker(
            this.beginTimeTextField, activity as AppCompatActivity, this.eventViewModel.event.value?.beginDate
        )
    }

    private fun updateEndDateTextField(){
        PickerHelper.populateEditTextWithDatePicker(
            this.endDateTextField, activity as AppCompatActivity, this.eventViewModel.event.value?.endDate
        )
    }

    private fun updateEndTimeTextField(){
        PickerHelper.populateEditTextWithTimePicker(
            this.endTimeTextField, activity as AppCompatActivity, this.eventViewModel.event.value?.endDate
        )
    }

    private fun updateAttachedTrailsRecyclerView(){

        /*If the event is not created in the database yet, then sets an offline adapter*/

        if(this.eventViewModel.event.value?.id==null){
            this.attachedTrailsAdapterOffline=
                TrailHorizontalAdapterOffline(this.eventViewModel.attachedTrails, this, true, this)
            this.attachedTrailsRecyclerView.adapter=this.attachedTrailsAdapterOffline

            /*Else sets an online adapter*/

        }else {
            this.attachedTrailsAdapter=
                TrailHorizontalAdapter(
                    DatabaseFirebaseHelper.generateOptionsForAdapter(
                        Trail::class.java,
                        EventFirebaseQueries.getAttachedTrails(this.eventViewModel.event.value?.id.toString()),
                        activity as AppCompatActivity
                ), this, true, this
            )
            this.attachedTrailsRecyclerView.adapter=this.attachedTrailsAdapter
        }
    }

    @Suppress("UNUSED_PARAMETER")
    fun onAddTrailsButtonClick(view: View){
        if(this.eventViewModel.event.value?.id!=null){
            this.eventViewModel.attachedTrails.addAll(this.attachedTrailsAdapter.snapshots)
        }
        (activity as EventEditActivity).selectTrail(this.eventViewModel.attachedTrails)
    }

    @Suppress("UNUSED_PARAMETER")
    fun onMeetingPointTextFieldClick(view: View){
        (activity as EventEditActivity).searchLocation()
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
            this.eventViewModel.event.value?.beginDate=DateUtilities.mergeDateAndTime(beginDate!!, beginTime!!)
            this.eventViewModel.event.value?.endDate=DateUtilities.mergeDateAndTime(endDate!!, endTime!!)
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

        if (this.eventViewModel.event.value?.id == null) {
            this.eventViewModel.attachedTrails.clear()
            this.eventViewModel.attachedTrails.addAll(trails)
            this.attachedTrailsAdapterOffline.notifyDataSetChanged()
        }
        else{

            /*Else, deletes each attached trail which is not in the new list of selected trails*/

            this.eventViewModel.attachedTrails.forEach { trail ->
                if(trails.firstOrNull { it.id==trail.id } ==null){
                    this.eventViewModel.detachTrail(trail)
                }
            }

            /*And updates the attached trails with each item in the new list of selected trails*/

            trails.forEach { trail ->
                this.eventViewModel.attachTrail(trail)
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

        if (this.eventViewModel.event.value?.id == null) {
            this.eventViewModel.attachedTrails.removeAll { it.id==trail.id }
            this.attachedTrailsAdapterOffline.notifyDataSetChanged()
        }
        else{
            this.eventViewModel.detachTrail(trail)
        }
    }

    /**Updates the event's position with the given trail**/

    private fun updateEventPosition(trail: Trail) {
        this.eventViewModel.event.value?.position = trail.position
    }

    /**Updates the event's main photo url with the first photo url found in the given trail**/

    private fun updateEventMainPhotoUrl(trail: Trail) {
        this.eventViewModel.event.value?.mainPhotoUrl = trail.getFirstPhotoUrl()
    }
}
