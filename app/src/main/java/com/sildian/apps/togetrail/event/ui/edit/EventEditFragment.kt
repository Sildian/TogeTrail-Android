package com.sildian.apps.togetrail.event.ui.edit

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.common.baseControllers.BaseFragment
import com.sildian.apps.togetrail.common.utils.DateUtilities
import com.sildian.apps.togetrail.common.utils.cloudHelpers.DatabaseFirebaseHelper
import com.sildian.apps.togetrail.common.utils.uiHelpers.PickerHelper
import com.sildian.apps.togetrail.common.utils.uiHelpers.SnackbarHelper
import com.sildian.apps.togetrail.common.utils.uiHelpers.TextFieldHelper
import com.sildian.apps.togetrail.databinding.FragmentEventEditBinding
import com.sildian.apps.togetrail.event.data.dataRequests.EventSaveDataRequest
import com.sildian.apps.togetrail.event.data.source.EventFirebaseQueries
import com.sildian.apps.togetrail.event.data.viewModels.EventViewModel
import com.sildian.apps.togetrail.location.data.models.Location
import com.sildian.apps.togetrail.trail.data.models.Trail
import com.sildian.apps.togetrail.trail.ui.others.TrailHorizontalAdapter
import com.sildian.apps.togetrail.trail.ui.others.TrailHorizontalAdapterOffline
import com.sildian.apps.togetrail.trail.ui.others.TrailHorizontalViewHolder
import dagger.hilt.android.AndroidEntryPoint

/*************************************************************************************************
 * Allows to edit an event
 * @param eventId : the event's id
 ************************************************************************************************/

@AndroidEntryPoint
class EventEditFragment(private val eventId: String?=null) :
    BaseFragment<FragmentEventEditBinding>(),
    TrailHorizontalViewHolder.OnTrailClickListener,
    TrailHorizontalViewHolder.OnTrailRemovedListener
{

    /**************************************Data**************************************************/

    private val eventViewModel: EventViewModel by viewModels()

    /**********************************UI component**********************************************/

    private lateinit var attachedTrailsAdapter: TrailHorizontalAdapter
    private lateinit var attachedTrailsAdapterOffline: TrailHorizontalAdapterOffline

    /*********************************Data monitoring********************************************/

    override fun initializeData() {
        this.binding.eventEditFragment = this
        this.binding.eventViewModel = this.eventViewModel
        observeEvent()
        observeDataRequestState()
    }

    override fun loadData() {
        lifecycleScope.launchWhenStarted {
            loadEvent()
        }
    }

    private fun observeEvent() {
        this.eventViewModel.data.observe(this) { eventData ->
            eventData?.error?.let { e ->
                onQueryError(e)
            } ?:
            eventData?.data?.let { event ->
                refreshUI()
            }
        }
    }

    private fun observeDataRequestState() {
        this.eventViewModel.dataRequestState.observe(this) { dataRequestState ->
            if (dataRequestState?.dataRequest is EventSaveDataRequest) {
                dataRequestState.error?.let { e ->
                    onQueryError(e)
                } ?: onQuerySuccess()
            }
        }
    }

    private fun loadEvent() {
        if(this.eventId != null){
            this.eventViewModel.loadEvent(eventId)
        }else {
            this.eventViewModel.initNewEvent()
        }
    }

    override fun updateData(data:Any?) {
        if(data is Location){
            this.eventViewModel.data.value?.data?.meetingPoint = data
            this.binding.fragmentEventEditTextFieldMeetingPoint.setText(data.fullAddress)
        }
        else if (data is List<*>) {
            if (data.firstOrNull() is Trail) {
                attachTrails(data as List<Trail>)
            }
        }
    }

    override fun saveData() {
        updateDates()
        if(checkDataIsValid()) {
            this.eventViewModel.data.value?.data?.name = this.binding.fragmentEventEditTextFieldName.text.toString()
            this.eventViewModel.data.value?.data?.description = this.binding.fragmentEventEditTextFieldDescription.text.toString()
            this.baseActivity?.showProgressDialog()
            this.eventViewModel.saveEvent()
        }
    }

    override fun checkDataIsValid():Boolean{
        if (checkTextFieldsAreNotEmpty()) {
            if (checkBeginDateIsBeforeEndDate()) {
                if (checkTrailsAreAttached()) {
                    return true
                } else {
                    SnackbarHelper
                        .createSimpleSnackbar(this.binding.fragmentEventEditViewMessage, null, R.string.message_event_no_trail_attached)
                        .show()
                }
            } else {
                SnackbarHelper
                    .createSimpleSnackbar(this.binding.fragmentEventEditViewMessage, null, R.string.message_event_dates_issue)
                    .show()
            }
        }
        else{
            SnackbarHelper
                .createSimpleSnackbar(this.binding.fragmentEventEditViewMessage, null, R.string.message_text_fields_empty)
                .show()
        }
        return false
    }

    private fun checkTextFieldsAreNotEmpty():Boolean{
        val textFieldsAndLayouts= hashMapOf<TextInputEditText, TextInputLayout>()
        textFieldsAndLayouts[this.binding.fragmentEventEditTextFieldName] = this.binding.fragmentEventEditTextFieldLayoutName
        textFieldsAndLayouts[this.binding.fragmentEventEditTextFieldBeginDate] = this.binding.fragmentEventEditTextFieldLayoutBeginDate
        textFieldsAndLayouts[this.binding.fragmentEventEditTextFieldBeginTime] = this.binding.fragmentEventEditTextFieldLayoutBeginTime
        textFieldsAndLayouts[this.binding.fragmentEventEditTextFieldEndDate] = this.binding.fragmentEventEditTextFieldLayoutEndDate
        textFieldsAndLayouts[this.binding.fragmentEventEditTextFieldEndTime] = this.binding.fragmentEventEditTextFieldLayoutEndTime
        textFieldsAndLayouts[this.binding.fragmentEventEditTextFieldMeetingPoint] = this.binding.fragmentEventEditTextFieldLayoutMeetingPoint
        textFieldsAndLayouts[this.binding.fragmentEventEditTextFieldDescription] = this.binding.fragmentEventEditTextFieldLayoutDescription
        return TextFieldHelper.checkAllTextFieldsAreNotEmpty(textFieldsAndLayouts)
    }

    private fun checkBeginDateIsBeforeEndDate():Boolean {
        this.eventViewModel.data.value?.data?.beginDate?.let { beginDate ->
            this.eventViewModel.data.value?.data?.endDate?.let { endDate ->
                return beginDate.time < endDate.time
            }
        }
        return false
    }

    private fun checkTrailsAreAttached():Boolean {
        val trails = if (this.eventViewModel.data.value?.data?.id == null) {
            this.eventViewModel.attachedTrails
        } else {
            this.attachedTrailsAdapter.snapshots
        }
        return trails.isNotEmpty()
    }

    /***********************************UI monitoring********************************************/

    override fun getLayoutId(): Int = R.layout.fragment_event_edit

    override fun refreshUI() {
        updateBeginDateTextField()
        updateBeginTimeTextField()
        updateEndDateTextField()
        updateEndTimeTextField()
        updateAttachedTrailsRecyclerView()
    }

    private fun updateBeginDateTextField() {
        PickerHelper.populateEditTextWithDatePicker(
            this.binding.fragmentEventEditTextFieldBeginDate, activity as AppCompatActivity, this.eventViewModel.data.value?.data?.beginDate
        )
    }

    private fun updateBeginTimeTextField() {
        PickerHelper.populateEditTextWithTimePicker(
            this.binding.fragmentEventEditTextFieldBeginTime, activity as AppCompatActivity, this.eventViewModel.data.value?.data?.beginDate
        )
    }

    private fun updateEndDateTextField() {
        PickerHelper.populateEditTextWithDatePicker(
            this.binding.fragmentEventEditTextFieldEndDate, activity as AppCompatActivity, this.eventViewModel.data.value?.data?.endDate
        )
    }

    private fun updateEndTimeTextField() {
        PickerHelper.populateEditTextWithTimePicker(
            this.binding.fragmentEventEditTextFieldEndTime, activity as AppCompatActivity, this.eventViewModel.data.value?.data?.endDate
        )
    }

    private fun updateAttachedTrailsRecyclerView(){

        /*If the event is not created in the database yet, then sets an offline adapter*/

        if (this.eventViewModel.data.value?.data?.id == null) {
            this.attachedTrailsAdapterOffline=
                TrailHorizontalAdapterOffline(this.eventViewModel.attachedTrails, this, true, this)
            this.binding.fragmentEventEditRecyclerViewAttachedTrails.adapter = this.attachedTrailsAdapterOffline

            /*Else sets an online adapter*/

        } else {
            this.attachedTrailsAdapter =
                TrailHorizontalAdapter(
                    DatabaseFirebaseHelper.generateOptionsForAdapter(
                        Trail::class.java,
                        EventFirebaseQueries.getAttachedTrails(this.eventViewModel.data.value?.data?.id.toString()),
                        activity as AppCompatActivity
                ), this, true, this
            )
            this.binding.fragmentEventEditRecyclerViewAttachedTrails.adapter = this.attachedTrailsAdapter
        }
    }

    @Suppress("UNUSED_PARAMETER")
    fun onAddTrailsButtonClick(view: View){
        if (this.eventViewModel.data.value?.data?.id != null) {
            this.eventViewModel.attachedTrails.addAll(this.attachedTrailsAdapter.snapshots)
        }
        (activity as EventEditActivity).selectTrail(this.eventViewModel.attachedTrails)
    }

    @Suppress("UNUSED_PARAMETER")
    fun onMeetingPointTextFieldClick(view: View){
        (activity as EventEditActivity).searchLocation()
    }

    /*****************************Trails / Events monitoring************************************/

    private fun updateDates(){

        /*If the dates and times fields are not empty, refreshes the event's dates*/

        if(!(this.binding.fragmentEventEditTextFieldBeginDate.text.isNullOrEmpty()
            ||this.binding.fragmentEventEditTextFieldBeginTime.text.isNullOrEmpty()
            ||this.binding.fragmentEventEditTextFieldEndDate.text.isNullOrEmpty()
            ||this.binding.fragmentEventEditTextFieldEndTime.text.isNullOrEmpty()))
        {
            val beginDate = DateUtilities.getDateFromString(this.binding.fragmentEventEditTextFieldBeginDate.text.toString())
            val beginTime = DateUtilities.getTimeFromString(this.binding.fragmentEventEditTextFieldBeginTime.text.toString())
            val endDate = DateUtilities.getDateFromString(this.binding.fragmentEventEditTextFieldEndDate.text.toString())
            val endTime = DateUtilities.getTimeFromString(this.binding.fragmentEventEditTextFieldEndTime.text.toString())
            this.eventViewModel.data.value?.data?.beginDate = DateUtilities.mergeDateAndTime(beginDate!!, beginTime!!)
            this.eventViewModel.data.value?.data?.endDate = DateUtilities.mergeDateAndTime(endDate!!, endTime!!)
        }
    }

    private fun attachTrails(trails:List<Trail>){

        /*Updates some data in the event with the given trail*/

        updateEventPosition(trails.first())
        updateEventMainPhotoUrl(trails.first())

        /*If the event has no id yet, updates the offline adapter*/

        if (this.eventViewModel.data.value?.data?.id == null) {
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

    override fun onTrailClick(trail: Trail) {
        trail.id?.let { id ->
            (activity as EventEditActivity).seeTrail(id)
        }
    }

    override fun onTrailRemoved(trail: Trail) {

        /*If the event has no id yet, updates the offline adapter. Else updates the attached trail in the database*/

        if (this.eventViewModel.data.value?.data?.id == null) {
            this.eventViewModel.attachedTrails.removeAll { it.id==trail.id }
            this.attachedTrailsAdapterOffline.notifyDataSetChanged()
        }
        else{
            this.eventViewModel.detachTrail(trail)
        }
    }

    private fun updateEventPosition(trail: Trail) {
        this.eventViewModel.data.value?.data?.position = trail.position
    }

    private fun updateEventMainPhotoUrl(trail: Trail) {
        this.eventViewModel.data.value?.data?.mainPhotoUrl = trail.getFirstPhotoUrl()
    }
}
