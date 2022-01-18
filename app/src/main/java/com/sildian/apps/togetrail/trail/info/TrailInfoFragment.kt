package com.sildian.apps.togetrail.trail.info

import android.content.Intent
import android.view.View
import androidx.core.net.toUri
import androidx.databinding.ViewDataBinding
import com.google.android.gms.maps.model.LatLng
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.common.utils.GoogleMapUrlHelper
import com.sildian.apps.togetrail.databinding.FragmentTrailInfoBinding
import com.sildian.apps.togetrail.trail.map.BaseTrailMapFragment
import com.sildian.apps.togetrail.trail.map.TrailActivity
import com.sildian.apps.togetrail.trail.model.support.ElevationChartGenerator
import com.sildian.apps.togetrail.trail.model.support.TrailViewModel

/*************************************************************************************************
 * Shows information about a trail
 * This fragment should be used as a nested fragment within a BottomSheet
 * @param trailViewModel : the trail data
 * @param isEditable : true if the info can be edited
 ************************************************************************************************/

class TrailInfoFragment(
    private val trailViewModel: TrailViewModel? = null,
    private val isEditable:Boolean = false
)
    : BaseInfoFragment<FragmentTrailInfoBinding>()
{

    /**********************************Support items*********************************************/

    private lateinit var elevationChartGenerator: ElevationChartGenerator

    /*********************************Data monitoring********************************************/

    override fun loadData() {
        initializeData()
        observeTrail()
        observeRequestFailure()
    }

    private fun initializeData() {
        this.binding.trailInfoFragment = this
        this.binding.trailViewModel = this.trailViewModel
        this.binding.isEditable = this.isEditable
        context?.let { context ->
            this.elevationChartGenerator = ElevationChartGenerator(context, this.trailViewModel?.data?.value)
        }
    }

    private fun observeTrail() {
        this.trailViewModel?.data?.observe(this) { trail ->
            refreshUI()
        }
    }

    private fun observeRequestFailure() {
        this.trailViewModel?.error?.observe(this) { e ->
            if (e != null) {
                onQueryError(e)
            }
        }
    }

    /***********************************UI monitoring********************************************/

    override fun getLayoutId(): Int = R.layout.fragment_trail_info

    override fun getTopViewId(): Int = R.id.fragment_trail_info_image_view_photo

    override fun getBottomViewId(): Int = R.id.fragment_trail_info_layout_info

    override fun initializeUI() {
        initializeElevationChart()
        refreshUI()
    }

    override fun refreshUI() {
        updateElevationChart()
    }

    private fun initializeElevationChart() {
        context?.let { context ->
            this.binding.fragmentTrailInfoChartElevation.setTouchEnabled(false)
            this.binding.fragmentTrailInfoChartElevation.description = null
            this.binding.fragmentTrailInfoChartElevation.legend.isEnabled = false
            this.binding.fragmentTrailInfoChartElevation.xAxis.setDrawLabels(false)
            this.binding.fragmentTrailInfoChartElevation.axisRight.setDrawLabels(false)
            this.binding.fragmentTrailInfoChartElevation.axisLeft.valueFormatter = ElevationChartGenerator.ElevationValueFormatter(context)
        }
    }

    private fun updateElevationChart() {
        this.elevationChartGenerator.generateChartData()
        if (this.elevationChartGenerator.chartData != null) {
            this.binding.fragmentTrailInfoLayoutChartElevation.visibility = View.VISIBLE
            this.binding.fragmentTrailInfoChartElevation.data = this.elevationChartGenerator.chartData
            this.binding.fragmentTrailInfoChartElevation.invalidate()
        } else {
            this.binding.fragmentTrailInfoLayoutChartElevation.visibility = View.GONE
        }
    }

    @Suppress("UNUSED_PARAMETER")
    fun onSeeButtonClick(view:View) {
        (parentFragment as BaseTrailMapFragment<out ViewDataBinding>).expandInfoBottomSheet()
    }

    @Suppress("UNUSED_PARAMETER")
    fun onEditButtonClick(view:View){
        (parentFragment as BaseTrailMapFragment<out ViewDataBinding>).editTrailInfo()
    }

    @Suppress("UNUSED_PARAMETER")
    fun onAuthorPhotoClick(view: View) {
        this.trailViewModel?.data?.value?.authorId?.let { authorId ->
            (activity as TrailActivity).seeHiker(authorId)
        }
    }

    @Suppress("UNUSED_PARAMETER")
    fun onGoToDepartureButtonClick(view: View) {
        this.trailViewModel?.data?.value?.position?.let { position ->
            val url = GoogleMapUrlHelper.generateWithLatLng(LatLng(position.latitude, position.longitude))
            val googleMapIntent = Intent(Intent.ACTION_VIEW, url.toUri())
            startActivity(googleMapIntent)
        }
    }
}
