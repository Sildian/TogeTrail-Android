package com.sildian.apps.togetrail.event.edit

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity

import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.common.flows.SaveDataFlow
import com.sildian.apps.togetrail.common.utils.uiHelpers.DropdownMenuHelper
import com.sildian.apps.togetrail.event.model.core.Event
import kotlinx.android.synthetic.main.fragment_event_edit.view.*

/*************************************************************************************************
 * Allows to edit an event
 * @param event : the related event
 ************************************************************************************************/

class EventEditFragment(val event: Event?=null) :
    Fragment(),
    SaveDataFlow
{

    /**********************************Static items**********************************************/

    companion object {

        /**Logs**/
        private const val TAG_FRAGMENT = "TAG_FRAGMENT"
    }

    /**********************************UI component**********************************************/

    private lateinit var layout:View
    private val nameTextField by lazy {layout.fragment_event_edit_text_field_name}
    private val beginDateTextFieldDropdown by lazy {layout.fragment_event_edit_text_field_dropdown_begin_date}
    private val beginTimeTextFieldDropdown by lazy {layout.fragment_event_edit_text_field_dropdown_begin_time}
    private val endDateTextFieldDropdown by lazy {layout.fragment_event_edit_text_field_dropdown_end_date}
    private val endTimeTextFieldDropdown by lazy {layout.fragment_event_edit_text_field_dropdown_end_time}
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

    }

    /***********************************UI monitoring********************************************/

    private fun initializeAllUIComponents(){
        initializeNameTextField()
        initializeBeginDateTextFieldDropdown()
        initializeBeginTimeTextFieldDropdown()
        initializeEndDateTextFieldDropdown()
        initializeEndTimeTextFieldDropdown()
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
        DropdownMenuHelper.populateDropdownMenuWithDatePicker(
            this.beginDateTextFieldDropdown, activity as AppCompatActivity, this.event?.beginDate
        )
    }

    private fun initializeBeginTimeTextFieldDropdown(){
        DropdownMenuHelper.populateDropdownMenuWithTimePicker(
            this.beginTimeTextFieldDropdown, activity as AppCompatActivity, this.event?.beginDate
        )
    }

    private fun initializeEndDateTextFieldDropdown(){
        DropdownMenuHelper.populateDropdownMenuWithDatePicker(
            this.endDateTextFieldDropdown, activity as AppCompatActivity, this.event?.endDate
        )
    }

    private fun initializeEndTimeTextFieldDropdown(){
        DropdownMenuHelper.populateDropdownMenuWithTimePicker(
            this.endTimeTextFieldDropdown, activity as AppCompatActivity, this.event?.endDate
        )
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
}
