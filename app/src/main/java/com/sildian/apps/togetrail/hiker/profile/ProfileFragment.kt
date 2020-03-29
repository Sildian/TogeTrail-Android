package com.sildian.apps.togetrail.hiker.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.common.flows.BaseDataFlowFragment
import com.sildian.apps.togetrail.common.utils.cloudHelpers.RecyclerViewFirebaseHelper
import com.sildian.apps.togetrail.hiker.model.core.Hiker
import com.sildian.apps.togetrail.hiker.model.core.HikerHistoryItem
import com.sildian.apps.togetrail.hiker.model.support.HikerFirebaseQueries
import kotlinx.android.synthetic.main.fragment_profile.view.*
import java.util.*

/*************************************************************************************************
 * Allows to see a hiker's profile
 * @param hiker : the hiker
 ************************************************************************************************/

class ProfileFragment (var hiker: Hiker?)
    : BaseDataFlowFragment(),
    HikerHistoryViewHolder.OnHikerHistoryItemClick
{

    /**********************************Static items**********************************************/

    companion object {

        /**Logs**/
        private const val TAG = "ProfileFragment"
    }

    /**********************************UI component**********************************************/

    private lateinit var layout:View
    private val photoImageView by lazy {layout.fragment_profile_image_view_photo}
    private val nameText by lazy {layout.fragment_profile_text_name}
    private val liveLocationText by lazy {layout.fragment_profile_text_live_location}
    private val ageText by lazy {layout.fragment_profile_text_age}
    private val nbTrailsCreatedText by lazy {layout.fragment_profile_text_nb_trails_created}
    private val nbTrailsCreatedLabel by lazy {layout.fragment_profile_label_nb_trails_created}
    private val nbEventsCreatedText by lazy {layout.fragment_profile_text_nb_events_created}
    private val nbEventsCreatedLabel by lazy {layout.fragment_profile_label_nb_events_created}
    private val nbEventsRegisteredText by lazy {layout.fragment_profile_text_nb_events_registered}
    private val nbEventsRegisteredLabel by lazy {layout.fragment_profile_label_nb_events_registered}
    private val descriptionText by lazy {layout.fragment_profile_text_description}
    private val historyItemsRecyclerView by lazy {layout.fragment_profile_recycler_view_history_items}
    private lateinit var historyItemAdapter: HikerHistoryAdapter

    /************************************Life cycle**********************************************/

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(TAG, "Fragment '${javaClass.simpleName}' created")
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
        updateNbTrailsCreatedText()
        updateNbEventsCreatedText()
        updateNbEventsRegisteredText()
        updateDescriptionText()
        updateHistoryItemsRecyclerView()
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
        val location=this.hiker?.liveLocation?.fullAddress
        if(location!=null) {
            this.liveLocationText.visibility=View.VISIBLE
            this.liveLocationText.text = location
        }
        else{
            this.liveLocationText.visibility=View.GONE
        }
    }

    private fun updateAgeText(){
        val age=this.hiker?.getAge(Date())
        if(age!=null) {
            this.ageText.visibility=View.VISIBLE
            val ageToDisplay="$age "+resources.getString(R.string.label_hiker_age)
            this.ageText.text = ageToDisplay
        }
        else{
            this.ageText.visibility=View.GONE
        }
    }

    private fun updateNbTrailsCreatedText(){
        this.nbTrailsCreatedText.text=this.hiker?.nbTrailsCreated.toString()
        if(this.hiker!=null){
            if(this.hiker!!.nbTrailsCreated>1){
                this.nbTrailsCreatedLabel.setText(R.string.label_hiker_trails_created_plur)
            }else{
                this.nbTrailsCreatedLabel.setText(R.string.label_hiker_trails_created_sing)
            }
        }
    }

    private fun updateNbEventsCreatedText(){
        this.nbEventsCreatedText.text=this.hiker?.nbEventsCreated.toString()
        if(this.hiker!=null){
            if(this.hiker!!.nbEventsCreated>1){
                this.nbEventsCreatedLabel.setText(R.string.label_hiker_events_created_plur)
            }else{
                this.nbEventsCreatedLabel.setText(R.string.label_hiker_events_created_sing)
            }
        }
    }

    private fun updateNbEventsRegisteredText(){
        this.nbEventsRegisteredText.text=this.hiker?.nbEventsAttended.toString()
        if(this.hiker!=null){
            if(this.hiker!!.nbEventsAttended>1){
                this.nbEventsRegisteredLabel.setText(R.string.label_hiker_events_registered_plur)
            }else{
                this.nbEventsRegisteredLabel.setText(R.string.label_hiker_events_registered_sing)
            }
        }
    }

    private fun updateDescriptionText(){
        val description=this.hiker?.description
        if(description!=null) {
            this.descriptionText.visibility=View.VISIBLE
            this.descriptionText.text=description
        }
        else{
            this.descriptionText.visibility=View.GONE
        }
    }

    private fun updateHistoryItemsRecyclerView(){
        this.historyItemAdapter= HikerHistoryAdapter(
            RecyclerViewFirebaseHelper.generateOptionsForAdapter(
                HikerHistoryItem::class.java,
                HikerFirebaseQueries.getLastHistoryItems(this.hiker?.id!!),
                activity as AppCompatActivity
            ),
            this.hiker?.name!!,
            this
        )
        this.historyItemsRecyclerView.adapter=this.historyItemAdapter
    }

    /********************************History monitoring******************************************/

    override fun onHistoryItemClick(historyItem: HikerHistoryItem) {
        //TODO handle
    }
}
