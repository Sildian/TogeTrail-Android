package com.sildian.apps.togetrail.hiker.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.common.flows.BaseDataFlowFragment
import com.sildian.apps.togetrail.common.utils.cloudHelpers.UserFirebaseHelper
import com.sildian.apps.togetrail.hiker.model.core.Hiker
import kotlinx.android.synthetic.main.fragment_profile.view.*
import java.util.*

/*************************************************************************************************
 * Allows to see a hiker's profile
 * @param hiker : the hiker
 ************************************************************************************************/

class ProfileFragment (var hiker: Hiker?) : BaseDataFlowFragment() {

    /**********************************Static items**********************************************/

    companion object {

        /**Logs**/
        private const val TAG_FRAGMENT = "TAG_FRAGMENT"
    }

    /**********************************UI component**********************************************/

    private lateinit var layout:View
    private val photoImageView by lazy {layout.fragment_profile_image_view_photo}
    private val nameText by lazy {layout.fragment_profile_text_name}
    private val liveLocationText by lazy {layout.fragment_profile_text_live_location}
    private val ageText by lazy {layout.fragment_profile_text_age}
    private val descriptionText by lazy {layout.fragment_profile_text_description}
    private val editButton by lazy {layout.fragment_profile_button_edit}

    /************************************Life cycle**********************************************/

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(TAG_FRAGMENT, "Fragment '${javaClass.simpleName}' created")
        this.layout=inflater.inflate(R.layout.fragment_profile, container, false)
        updateUI()
        return this.layout
    }

    /***********************************Data monitoring******************************************/

    fun updateHiker(hiker:Hiker){
        this.hiker=hiker
        updateUI()
    }

    /***********************************UI monitoring********************************************/

    private fun updateUI(){
        updatePhotoImageView()
        updateNameText()
        updateLiveLocationText()
        updateAgeText()
        updateDescriptionText()
        updateEditButton()
    }

    private fun updatePhotoImageView(){
        Glide.with(this)
            .load(this.hiker?.photoUrl)
            .apply(RequestOptions.circleCropTransform())
            .placeholder(R.drawable.ic_person_black)
            .into(this.photoImageView)
    }

    private fun updateNameText(){
        this.nameText.text=this.hiker?.name
    }

    private fun updateLiveLocationText(){
        val location=this.hiker?.liveLocation?.getFullLocation()
        if(location!=null) {
            this.liveLocationText.text = location
        }
        else{
            this.liveLocationText.visibility=View.GONE
        }
    }

    private fun updateAgeText(){
        val age=this.hiker?.getAge(Date())
        if(age!=null) {
            val ageToDisplay="$age "+resources.getString(R.string.label_hiker_age)
            this.ageText.text = ageToDisplay
        }
        else{
            this.ageText.visibility=View.GONE
        }
    }

    private fun updateDescriptionText(){
        val description=this.hiker?.description
        if(description!=null) {
            this.descriptionText.text=description
        }
        else{
            this.descriptionText.visibility=View.GONE
        }
    }

    private fun updateEditButton(){
        if(this.hiker?.id==UserFirebaseHelper.getCurrentUser()?.uid){
            this.editButton.visibility=View.VISIBLE
            this.editButton.setOnClickListener {
                (activity as ProfileActivity).editProfile()
            }
        }
        else{
            this.editButton.visibility=View.GONE
        }
    }
}
