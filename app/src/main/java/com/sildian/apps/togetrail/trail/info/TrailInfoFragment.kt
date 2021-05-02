package com.sildian.apps.togetrail.trail.info

import android.view.View
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.databinding.FragmentTrailInfoBinding
import com.sildian.apps.togetrail.trail.map.BaseTrailMapFragment
import com.sildian.apps.togetrail.trail.map.TrailActivity
import com.sildian.apps.togetrail.trail.model.support.ElevationChartGenerator
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

    private val elevationChartLayout by lazy {layout.fragment_trail_info_layout_chart_elevation}
    private val elevationChart by lazy { layout.fragment_trail_info_chart_elevation }

    /**********************************Support items*********************************************/

    private lateinit var elevationChartGenerator: ElevationChartGenerator

    /*********************************Data monitoring********************************************/

    override fun loadData() {
        initializeData()
        observeTrail()
        observeRequestFailure()
    }

    private fun initializeData() {
        (this.binding as FragmentTrailInfoBinding).trailInfoFragment = this
        (this.binding as FragmentTrailInfoBinding).trailViewModel = this.trailViewModel
        (this.binding as FragmentTrailInfoBinding).isEditable = this.isEditable
        this.elevationChartGenerator = ElevationChartGenerator(context!!, this.trailViewModel?.trail?.value)
    }

    private fun observeTrail() {
        this.trailViewModel?.trail?.observe(this) { trail ->
            refreshUI()
        }
    }

    private fun observeRequestFailure() {
        this.trailViewModel?.requestFailure?.observe(this) { e ->
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
        this.elevationChart.setTouchEnabled(false)
        this.elevationChart.description = null
        this.elevationChart.legend.isEnabled = false
        this.elevationChart.xAxis.setDrawLabels(false)
        this.elevationChart.axisRight.setDrawLabels(false)
        this.elevationChart.axisLeft.valueFormatter = ElevationChartGenerator.ElevationValueFormatter(context!!)
    }

    private fun updateElevationChart() {
        this.elevationChartGenerator.generateChartData()
        if (this.elevationChartGenerator.chartData != null) {
            this.elevationChartLayout.visibility = View.VISIBLE
            this.elevationChart.data = this.elevationChartGenerator.chartData
            this.elevationChart.invalidate()
        } else {
            this.elevationChartLayout.visibility = View.GONE
        }
    }

    @Suppress("UNUSED_PARAMETER")
    fun onSeeButtonClick(view:View) {
        (parentFragment as BaseTrailMapFragment).expandInfoBottomSheet()
    }

    @Suppress("UNUSED_PARAMETER")
    fun onEditButtonClick(view:View){
        (parentFragment as BaseTrailMapFragment).editTrailInfo()
    }

    @Suppress("UNUSED_PARAMETER")
    fun onAuthorPhotoClick(view: View) {
        this.trailViewModel?.trail?.value?.authorId?.let { authorId ->
            (activity as TrailActivity).seeHiker(authorId)
        }
    }
}
