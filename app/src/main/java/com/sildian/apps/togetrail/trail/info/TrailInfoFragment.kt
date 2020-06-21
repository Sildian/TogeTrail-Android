package com.sildian.apps.togetrail.trail.info

import android.view.View
import androidx.databinding.Observable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.databinding.FragmentTrailInfoBinding
import com.sildian.apps.togetrail.trail.map.BaseTrailMapFragment
import com.sildian.apps.togetrail.trail.model.support.TrailViewModel
import kotlinx.android.synthetic.main.fragment_trail_info.view.*

/*************************************************************************************************
 * Shows information about a trail
 * This fragment should be used as a nested fragment within a BottomSheet
 * @param trailViewModel : the trail data
 * @param isEditable : true if the info can be edited
 ************************************************************************************************/

class TrailInfoFragment(
    private val trailViewModel: TrailViewModel?=null,
    private val isEditable:Boolean=false
)
    : BaseInfoFragment()
{

    /**********************************UI component**********************************************/

    private val photoImageView by lazy {layout.fragment_trail_info_image_view_photo}

    /*********************************Data monitoring********************************************/

    override fun loadData() {
        (this.binding as FragmentTrailInfoBinding).trailInfoFragment = this
        (this.binding as FragmentTrailInfoBinding).trailViewModel = this.trailViewModel
        (this.binding as FragmentTrailInfoBinding).isEditable = this.isEditable
        this.trailViewModel?.addOnPropertyChangedCallback(object:Observable.OnPropertyChangedCallback(){
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                refreshUI()
            }
        })
    }

    /***********************************UI monitoring********************************************/

    override fun getLayoutId(): Int = R.layout.fragment_trail_info

    override fun useDataBinding(): Boolean = true

    override fun getTopViewId(): Int = R.id.fragment_trail_info_image_view_photo

    override fun getBottomViewId(): Int = R.id.fragment_trail_info_layout_info

    override fun initializeUI() {
        updatePhoto()
    }

    override fun refreshUI() {
        updatePhoto()
    }

    private fun updatePhoto(){
        Glide.with(context!!)
            .load(this.trailViewModel?.trail?.mainPhotoUrl)
            .apply(RequestOptions.centerCropTransform())
            .placeholder(R.drawable.ic_trail_black)
            .into(this.photoImageView)
    }

    fun onEditButtonClick(view:View){
        (parentFragment as BaseTrailMapFragment).editTrailInfo()
    }
}
