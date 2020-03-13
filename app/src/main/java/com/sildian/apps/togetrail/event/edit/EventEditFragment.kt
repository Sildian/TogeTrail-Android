package com.sildian.apps.togetrail.event.edit

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.common.flows.SaveDataFlow
import com.sildian.apps.togetrail.common.model.FineLocation
import com.sildian.apps.togetrail.common.model.Location
import com.sildian.apps.togetrail.common.utils.DateUtilities
import com.sildian.apps.togetrail.common.utils.uiHelpers.DialogHelper
import com.sildian.apps.togetrail.common.utils.uiHelpers.PickerHelper
import com.sildian.apps.togetrail.event.model.core.Event
import kotlinx.android.synthetic.main.fragment_event_edit.view.*

/*************************************************************************************************
 * Allows to edit an event
 * @param event : the related event
 ************************************************************************************************/

class EventEditFragment(val event: Event?=null) :
    Fragment(),
    SaveDataFlow,
    EventDayViewHolder.OnEventDayTrailChanged
{

    /**********************************Static items**********************************************/

    companion object {

        /**Logs**/
        private const val TAG_FRAGMENT = "TAG_FRAGMENT"
        private const val TAG_DATA="TAG_DATA"
    }

    /**********************************UI component**********************************************/

    private lateinit var layout:View
    private val nameTextField by lazy {layout.fragment_event_edit_text_field_name}
    private val beginDateTextFieldDropdown by lazy {layout.fragment_event_edit_text_field_dropdown_begin_date}
    private val beginTimeTextFieldDropdown by lazy {layout.fragment_event_edit_text_field_dropdown_begin_time}
    private val endDateTextFieldDropdown by lazy {layout.fragment_event_edit_text_field_dropdown_end_date}
    private val endTimeTextFieldDropdown by lazy {layout.fragment_event_edit_text_field_dropdown_end_time}
    private val fillDateMessageText by lazy {layout.fragment_event_edit_text_message_fill_dates}
    private val daysRecyclerView by lazy {layout.fragment_event_edit_recycler_view_days}
    private lateinit var daysRecyclerViewAdapter:EventDayAdapter
    private val countryTextField by lazy {layout.fragment_event_edit_text_field_country}
    private val regionTextField by lazy {layout.fragment_event_edit_text_field_region}
    private val townTextField by lazy {layout.fragment_event_edit_text_field_town}
    private val addressTextField by lazy {layout.fragment_event_edit_text_field_address}
    private val descriptionTextField by lazy {layout.fragment_event_edit_text_field_description}

    /************************************Life cycle**********************************************/

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(TAG_FRAGMENT, "Fragment '${javaClass.simpleName}' created")
        this.layout=inflater.inflate(R.layout.fragment_event_edit, container, false)
        initializeAllUIComponents()
        return this.layout
    }

    /*********************************Data monitoring********************************************/

    override fun saveData() {
        this.event?.name=this.nameTextField.text.toString()
        this.event?.location= Location(
            this.countryTextField.text.toString(),
            this.regionTextField.text.toString(),
            this.townTextField.text.toString()
        )
        this.event?.meetingPoint= FineLocation(
            this.countryTextField.text.toString(),
            this.regionTextField.text.toString(),
            this.townTextField.text.toString(),
            this.addressTextField.text.toString()
        )
        this.event?.description=this.descriptionTextField.text.toString()
        (activity as EventEditActivity).updateEventAndSave(this.event!!)
    }

    /***********************************UI monitoring********************************************/

    private fun initializeAllUIComponents(){
        initializeNameTextField()
        initializeBeginDateTextFieldDropdown()
        initializeBeginTimeTextFieldDropdown()
        initializeEndDateTextFieldDropdown()
        initializeEndTimeTextFieldDropdown()
        initializeDaysRecyclerView()
        initializeCountryTextField()
        initializeRegionTextField()
        initializeTownTextField()
        initializeAddressTextField()
        initializeDescriptionTextField()
    }

    private fun initializeNameTextField(){
        this.nameTextField.setText(this.event?.name)
    }

    private fun initializeBeginDateTextFieldDropdown(){
        PickerHelper.populateEditTextWithDatePicker(
            this.beginDateTextFieldDropdown, activity as AppCompatActivity, this.event?.beginDate
        )
        this.beginDateTextFieldDropdown.addTextChangedListener {
            updateNbDays()
        }
    }

    private fun initializeBeginTimeTextFieldDropdown(){
        PickerHelper.populateEditTextWithTimePicker(
            this.beginTimeTextFieldDropdown, activity as AppCompatActivity, this.event?.beginDate
        )
        this.beginTimeTextFieldDropdown.addTextChangedListener {
            updateNbDays()
        }
    }

    private fun initializeEndDateTextFieldDropdown(){
        PickerHelper.populateEditTextWithDatePicker(
            this.endDateTextFieldDropdown, activity as AppCompatActivity, this.event?.endDate
        )
        this.endDateTextFieldDropdown.addTextChangedListener {
            updateNbDays()
        }
    }

    private fun initializeEndTimeTextFieldDropdown(){
        PickerHelper.populateEditTextWithTimePicker(
            this.endTimeTextFieldDropdown, activity as AppCompatActivity, this.event?.endDate
        )
        this.endTimeTextFieldDropdown.addTextChangedListener {
            updateNbDays()
        }
    }

    private fun initializeDaysRecyclerView(){
        if(this.event?.trailsIds!=null) {
            this.daysRecyclerViewAdapter = EventDayAdapter(this.event.trailsIds, this)
            this.daysRecyclerView.adapter=this.daysRecyclerViewAdapter
        }
    }

    private fun initializeCountryTextField(){
        this.countryTextField.setText(this.event?.meetingPoint?.country)
    }

    private fun initializeRegionTextField(){
        this.regionTextField.setText(this.event?.meetingPoint?.region)
    }

    private fun initializeTownTextField(){
        this.townTextField.setText(this.event?.meetingPoint?.town)
    }

    private fun initializeAddressTextField(){
        this.addressTextField.setText(this.event?.meetingPoint?.address)
    }

    private fun initializeDescriptionTextField(){
        this.descriptionTextField.setText(this.event?.description)
    }

    /*********************************Data monitoring********************************************/

    /**Updates the number of days related to the begin dates and times set**/

    private fun updateNbDays(){

        /*If the dates and times fields are empty, clears the event's trailsIds*/

        if(this.beginDateTextFieldDropdown.text.isNullOrEmpty()
            ||this.beginTimeTextFieldDropdown.text.isNullOrEmpty()
            ||this.endDateTextFieldDropdown.text.isNullOrEmpty()
            ||this.endTimeTextFieldDropdown.text.isNullOrEmpty())
        {
            this.event?.trailsIds?.clear()
            this.fillDateMessageText.visibility=View.VISIBLE
        }

        /*Else updates the dates and refreshes the trailsIdsKeys*/

        else{
            val beginDate=DateUtilities.getDateFromString(this.beginDateTextFieldDropdown.text.toString())
            val beginTime=DateUtilities.getTimeFromString(this.beginTimeTextFieldDropdown.text.toString())
            val endDate=DateUtilities.getDateFromString(this.endDateTextFieldDropdown.text.toString())
            val endTime=DateUtilities.getTimeFromString(this.endTimeTextFieldDropdown.text.toString())
            this.event?.beginDate=DateUtilities.mergeDateAndTime(beginDate!!, beginTime!!)
            this.event?.endDate=DateUtilities.mergeDateAndTime(endDate!!, endTime!!)
            this.event?.refreshTrailsIdsKeys()
            this.daysRecyclerViewAdapter.notifyDataSetChanged()
            this.fillDateMessageText.visibility=View.GONE
        }
    }

    /**Runs a dialog allowing to add a trail to the event**/

    override fun onTrailAdd(day: Int, trailId: String?) {
        Log.d(TAG_DATA, "Day $day : adding trail")
        DialogHelper
            .createTrailSelectionDialog(
                activity as AppCompatActivity,
                day,
                this::addTrail)
            .show()
    }

    /**Removes a trail from the event**/

    override fun onTrailRemove(day: Int, trailId: String?) {
        Log.d(TAG_DATA, "Day $day : removed trail")
        if(this.event!=null) {
            this.event.trailsIds[day.toString()] = null
            this.daysRecyclerViewAdapter.notifyDataSetChanged()
        }
    }

    /**Adds a trail to the event**/

    private fun addTrail(day:Int, trailId:String?){
        Log.d(TAG_DATA, "Day $day : added trail '$trailId'")
        if(this.event!=null){
            this.event.trailsIds[day.toString()]=trailId
            this.daysRecyclerViewAdapter.notifyDataSetChanged()
        }
    }
}
