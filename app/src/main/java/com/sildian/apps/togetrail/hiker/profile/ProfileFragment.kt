package com.sildian.apps.togetrail.hiker.profile

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.common.flows.BaseDataFlowFragment
import com.sildian.apps.togetrail.common.utils.cloudHelpers.DatabaseFirebaseHelper
import com.sildian.apps.togetrail.hiker.model.core.Hiker
import com.sildian.apps.togetrail.hiker.model.core.HikerHistoryItem
import com.sildian.apps.togetrail.hiker.model.core.HikerHistoryType
import com.sildian.apps.togetrail.hiker.model.support.HikerFirebaseQueries
import kotlinx.android.synthetic.main.fragment_profile.view.*
import java.util.*

/*************************************************************************************************
 * Shows a hiker's profile
 * @param hiker : the hiker
 ************************************************************************************************/

class ProfileFragment (private var hiker: Hiker?)
    : BaseDataFlowFragment(),
    HikerHistoryViewHolder.OnHikerHistoryItemClick
{

    /**********************************UI component**********************************************/

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

    /***********************************Data monitoring******************************************/

    override fun updateData(data:Any?) {
        if(data is Hiker) {
            this.hiker=data
            refreshUI()
        }
    }

    /***********************************UI monitoring********************************************/

    override fun getLayoutId(): Int = R.layout.fragment_profile

    override fun initializeUI(){
        initializeItemsRecyclerView()
        refreshUI()
    }

    override fun refreshUI(){
        updatePhotoImageView()
        updateNameText()
        updateLiveLocationText()
        updateAgeText()
        updateNbTrailsCreatedText()
        updateNbEventsCreatedText()
        updateNbEventsRegisteredText()
        updateDescriptionText()
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
        this.hiker?.let { hiker ->
            if(hiker.nbTrailsCreated>1){
                this.nbTrailsCreatedLabel.setText(R.string.label_hiker_trails_created_plur)
            }else{
                this.nbTrailsCreatedLabel.setText(R.string.label_hiker_trails_created_sing)
            }
        }
    }

    private fun updateNbEventsCreatedText(){
        this.nbEventsCreatedText.text=this.hiker?.nbEventsCreated.toString()
        this.hiker?.let { hiker ->
            if(hiker.nbEventsCreated>1){
                this.nbEventsCreatedLabel.setText(R.string.label_hiker_events_created_plur)
            }else{
                this.nbEventsCreatedLabel.setText(R.string.label_hiker_events_created_sing)
            }
        }
    }

    private fun updateNbEventsRegisteredText(){
        this.nbEventsRegisteredText.text=this.hiker?.nbEventsAttended.toString()
        this.hiker?.let { hiker ->
            if(hiker.nbEventsAttended>1){
                this.nbEventsRegisteredLabel.setText(R.string.label_hiker_events_registered_plur)
            }else{
                this.nbEventsRegisteredLabel.setText(R.string.label_hiker_events_registered_sing)
            }
        }
    }

    private fun updateDescriptionText(){
        this.descriptionText.text=this.hiker?.description
    }

    private fun initializeItemsRecyclerView(){
        this.hiker?.let { hiker ->
            this.historyItemAdapter = HikerHistoryAdapter(
                DatabaseFirebaseHelper.generateOptionsForAdapter(
                    HikerHistoryItem::class.java,
                    HikerFirebaseQueries.getLastHistoryItems(hiker.id),
                    activity as AppCompatActivity
                ),
                hiker.name.toString(),
                this
            )
            this.historyItemsRecyclerView.adapter = this.historyItemAdapter
        }
    }

    /********************************History monitoring******************************************/

    override fun onHistoryItemClick(historyItem: HikerHistoryItem) {
        historyItem.itemId?.let { itemId ->
            when (historyItem.type) {
                HikerHistoryType.TRAIL_CREATED -> (activity as ProfileActivity).seeTrail(itemId)
                HikerHistoryType.EVENT_CREATED -> (activity as ProfileActivity).seeEvent(itemId)
                HikerHistoryType.EVENT_ATTENDED -> (activity as ProfileActivity).seeEvent(itemId)
                else -> { }
            }
        }
    }
}
