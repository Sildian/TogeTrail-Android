package com.sildian.apps.togetrail.trail.map

import android.Manifest
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.common.baseControllers.BaseFragment
import com.sildian.apps.togetrail.common.utils.locationHelpers.UserLocationException
import com.sildian.apps.togetrail.common.utils.MapMarkersUtilities
import com.sildian.apps.togetrail.common.utils.locationHelpers.UserLocationHelper
import com.sildian.apps.togetrail.common.utils.permissionsHelpers.PermissionsHelper
import com.sildian.apps.togetrail.common.utils.uiHelpers.SnackbarHelper
import com.sildian.apps.togetrail.trail.info.BaseInfoFragment
import com.sildian.apps.togetrail.trail.info.TrailInfoFragment
import com.sildian.apps.togetrail.trail.info.TrailPOIInfoFragment
import com.sildian.apps.togetrail.trail.model.dataRequests.TrailSaveDataRequest
import com.sildian.apps.togetrail.trail.model.support.TrailViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/*************************************************************************************************
 * Base for all Trail fragments using a map
 * @param trailViewModel : the trail data
 * @param isEditable : true if the trail is editable
 ************************************************************************************************/

abstract class BaseTrailMapFragment<T: ViewDataBinding> (
    protected var trailViewModel: TrailViewModel? = null,
    protected var isEditable: Boolean = false
) :
    BaseFragment<T>(),
    OnMapReadyCallback,
    GoogleMap.OnMapClickListener,
    GoogleMap.OnMarkerClickListener
{

    /**********************************Static items**********************************************/

    companion object{

        /**Logs**/
        private const val TAG="BaseTrailMapFragment"

        /**Bundles keys**/
        const val KEY_BUNDLE_MAP_VIEW="KEY_BUNDLE_MAP_VIEW"
    }

    /**********************************UI component**********************************************/

    protected lateinit var mapView: MapView                                     //The map view
    protected lateinit var infoBottomSheet: BottomSheetBehavior<View>           //Bottom sheet with additional info
    protected lateinit var infoFragment: BaseInfoFragment<out ViewDataBinding>  //Nested fragment allowing to see info about the trail

    /**************************************Map support*******************************************/

    protected var map: GoogleMap?=null

    /************************************Location support****************************************/

    protected lateinit var userLocationProvider: FusedLocationProviderClient

    /************************************Life cycle**********************************************/

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?{
        super.onCreateView(inflater, container, savedInstanceState)
        initializeInfoBottomSheet()
        initializeMap(savedInstanceState)
        initializeUserLocation()
        return this.layout
    }

    override fun onStart() {
        super.onStart()
        this.mapView.onStart()
    }

    @Suppress("MissingPermission")
    override fun onResume() {
        super.onResume()
        this.mapView.onResume()
        baseActivity?.let { baseActivity ->
            if (PermissionsHelper.isPermissionGranted(baseActivity, Manifest.permission.ACCESS_FINE_LOCATION)) {
                this.map?.isMyLocationEnabled = true
            }
        }
    }

    @Suppress("MissingPermission")
    override fun onPause() {
        baseActivity?.let { baseActivity ->
            if (PermissionsHelper.isPermissionGranted(baseActivity, Manifest.permission.ACCESS_FINE_LOCATION)) {
                this.map?.isMyLocationEnabled = false
            }
        }
        this.mapView.onPause()
        super.onPause()
    }

    override fun onStop() {
        this.mapView.onStop()
        super.onStop()
    }

    override fun onDestroy() {
        this.mapView.onDestroy()
        super.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        this.mapView.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        var mapViewBundle=outState.getBundle(KEY_BUNDLE_MAP_VIEW)
        if(mapViewBundle==null){
            mapViewBundle=Bundle()
            outState.putBundle(KEY_BUNDLE_MAP_VIEW, mapViewBundle)
        }
        this.mapView.onSaveInstanceState(mapViewBundle)
    }

    /**********************************Data monitoring*******************************************/

    override fun loadData() {
        observeTrail()
        observeRequestSuccess()
        observeRequestFailure()
    }

    private fun observeTrail() {
        this.trailViewModel?.data?.observe(this) { trail ->
            if (trail != null) {
                refreshUI()
            }
        }
    }

    private fun observeRequestSuccess() {
        this.trailViewModel?.success?.observe(this) { success ->
            if (success != null && success is TrailSaveDataRequest) {
                onQuerySuccess()
            }
        }
    }

    private fun observeRequestFailure() {
        this.trailViewModel?.error?.observe(this) { e ->
            if (e != null) {
                onQueryError(e)
            }
        }
    }

    override fun saveData(){
        hideInfoBottomSheet()
        if (this.checkDataIsValid()) {
            this.trailViewModel?.data?.value?.autoPopulatePosition()
            this.baseActivity?.showProgressDialog()
            this.trailViewModel?.saveTrail(false)
        }
    }

    override fun checkDataIsValid(): Boolean {
        if (this.trailViewModel?.data?.value != null) {
            if (this.trailViewModel?.data?.value!!.isDataValid()) {
                for (i in this.trailViewModel?.data?.value!!.trailTrack.trailPointsOfInterest.indices) {
                    if (!this.trailViewModel?.data?.value!!.trailTrack.trailPointsOfInterest[i].isDataValid()) {
                        showTrailPOIMissingInfoMessage(i)
                        return false
                    }
                }
                return true
            }else{
                showTrailMissingInfoMessage()
            }
        }
        return false
    }

    /************************************UI monitoring*******************************************/

    abstract fun getMapViewId():Int

    abstract fun getInfoBottomSheetId():Int

    abstract fun getInfoFragmentId():Int

    abstract fun enableUI()

    abstract fun disableUI()

    abstract fun getMessageView(): View

    abstract fun getMessageAnchorView(): View?

    override fun refreshUI() {
        this.map?.clear()
        showTrailTrackOnMap()
        showTrailInfoFragment()
    }

    private fun initializeInfoBottomSheet(){
        this.infoBottomSheet=
            BottomSheetBehavior.from(this.layout.findViewById(getInfoBottomSheetId()))
        val peekHeight=resources.getDimension(R.dimen.bottom_sheet_peek_height).toInt()
        this.infoBottomSheet.peekHeight=peekHeight
        this.infoBottomSheet.addBottomSheetCallback(InfoBottomSheetCallback())
        hideInfoBottomSheet()
    }

    private fun showTrailMissingInfoMessage() {
        SnackbarHelper
            .createSnackbarWithAction(getMessageView(), getMessageAnchorView(), R.string.message_trail_info_empty,
                R.string.button_common_inform, this::editTrailInfo)
            .show()
    }

    private fun showTrailPOIMissingInfoMessage(poiPosition: Int) {
        SnackbarHelper
            .createSnackbarWithAction(getMessageView(), getMessageAnchorView(), R.string.message_trail_poi_info_empty,
                R.string.button_common_inform) {
                    editTrailPoiInfo(poiPosition)
            }
            .show()
    }

    /***********************************Map monitoring*******************************************/

    private fun initializeMap(savedInstanceState: Bundle?){
        this.mapView=this.layout.findViewById(getMapViewId())
        var mapViewBundle: Bundle? = null
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(KEY_BUNDLE_MAP_VIEW)
        }
        this.mapView.onCreate(mapViewBundle)
        this.mapView.getMapAsync(this)
    }

    @Suppress("MissingPermission")
    override fun onMapReady(map: GoogleMap) {
        Log.d(TAG, "Map is ready in '${this.javaClass.simpleName}'")
        this.map = map
        this.map?.mapType = GoogleMap.MAP_TYPE_TERRAIN
        this.map?.setOnMapClickListener(this)
        this.map?.setOnMarkerClickListener(this)
        baseActivity?.let { baseActivity ->
            if (PermissionsHelper.isPermissionGranted(
                    baseActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                this.map?.isMyLocationEnabled = true
            }
        }
        this.map?.uiSettings?.isMyLocationButtonEnabled = false
        onMapReadyActionsFinished()
    }

    abstract fun onMapReadyActionsFinished()

    /********************************Location monitoring*****************************************/

    private fun initializeUserLocation(){
        context?.let { context ->
            this.userLocationProvider = LocationServices.getFusedLocationProviderClient(context)
        }
    }

    protected fun zoomToUserLocation() {
        lifecycleScope.launch {
            withContext(Dispatchers.Main) {
                context?.let { context ->

                    if (PermissionsHelper.isPermissionGranted(context, Manifest.permission.ACCESS_FINE_LOCATION)) {

                        val userLocation = async {
                            try {
                                UserLocationHelper.getLastUserLocation(userLocationProvider)
                            }
                            catch (e: UserLocationException) {
                                e.printStackTrace()
                                Log.w(TAG, "User location cannot be reached : ${e.message}")
                                null
                            }
                        }.await()

                        if (userLocation != null) {
                            map?.animateCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    LatLng(userLocation.latitude, userLocation.longitude), 15f))
                        }
                    }
                    else {
                        Log.w(TAG, "User location cannot be reached : permission is not granted")
                    }
                }
            }
        }
    }

    /***********************************Trail monitoring*****************************************/

    protected open fun showTrailTrackOnMap(){

        context?.let { context ->

            if (this.trailViewModel?.data?.value != null) {

                /*Creates and shows the polyline from the trailPoints*/

                val polylineOption = PolylineOptions()
                this.trailViewModel?.data?.value?.trailTrack?.trailPoints?.forEach { trailPoint->
                    polylineOption.add(LatLng(trailPoint.latitude, trailPoint.longitude))
                }
                polylineOption.color(ContextCompat.getColor(context, R.color.colorSecondaryDark))
                this.map?.addPolyline(polylineOption)

                /*Gets the first and the last trailPoints*/

                val firstPoint = this.trailViewModel?.data?.value?.trailTrack?.getFirstTrailPoint()
                val lastPoint = this.trailViewModel?.data?.value?.trailTrack?.getLastTrailPoint()

                /*Adds markers on the first and the last trailPoints*/

                if (firstPoint!=null) {
                    this.map?.addMarker(
                        MarkerOptions()
                            .position(LatLng(firstPoint.latitude, firstPoint.longitude))
                            .icon(
                                MapMarkersUtilities.createMapMarkerFromVector(
                                    context, R.drawable.ic_location_trail_map)))
                        ?.tag = firstPoint
                }

                if (lastPoint!=null && this.trailViewModel?.data?.value?.loop==false) {
                    this.map?.addMarker(
                        MarkerOptions()
                            .position(LatLng(lastPoint.latitude, lastPoint.longitude))
                            .icon(
                                MapMarkersUtilities.createMapMarkerFromVector(
                                    context, R.drawable.ic_flag_map)))
                        ?.tag = lastPoint
                }

                /*Adds a marker for each trailPointOfInterest including its number*/

                for (i in this.trailViewModel?.data?.value?.trailTrack?.trailPointsOfInterest!!.indices) {
                    val trailPointOfInterest = this.trailViewModel?.data?.value?.trailTrack?.trailPointsOfInterest!![i]
                    this.map?.addMarker(MarkerOptions()
                        .position(LatLng(trailPointOfInterest.latitude, trailPointOfInterest.longitude))
                        .icon(MapMarkersUtilities.createMapMarkerFromVector(
                            context, R.drawable.ic_location_trail_poi_map, (i+1).toString()))
                        .snippet(i.toString()))
                        ?.tag=trailPointOfInterest
                }
            }
        }
    }

    fun editTrailInfo(){
        this.trailViewModel?.data?.value?.let { trail ->
            hideInfoBottomSheet()
            (activity as TrailActivity).updateTrailAndEditInfo(trail)
        }
    }

    fun editTrailPoiInfo(trailPoiPosition:Int){
        this.trailViewModel?.data?.value?.let { trail ->
            hideInfoBottomSheet()
            (activity as TrailActivity).updateTrailAndEditPoiInfo(trail, trailPoiPosition)
        }
    }

    /*****************************Bottom sheet monitoring****************************************/

    fun getInfoBottomSheetState():Int = this.infoBottomSheet.state

    fun hideInfoBottomSheet(){
        this.infoBottomSheet.state=BottomSheetBehavior.STATE_HIDDEN
    }

    fun collapseInfoBottomSheet(){
        this.infoBottomSheet.state=BottomSheetBehavior.STATE_COLLAPSED
    }

    fun expandInfoBottomSheet(){
        this.infoBottomSheet.state=BottomSheetBehavior.STATE_EXPANDED
    }

    inner class InfoBottomSheetCallback:BottomSheetBehavior.BottomSheetCallback(){

        override fun onSlide(bottomSheet: View, slideOffset: Float) {

            /*If the offset is above 0, drags the view, else hides it*/

            if(slideOffset>0){
                infoFragment.dragView(slideOffset)
            }else{
                infoFragment.hideView()
            }
        }

        override fun onStateChanged(bottomSheet: View, newState: Int) {
            //Nothing
        }
    }

    /**************************Nested Fragments monitoring***************************************/

    fun showTrailInfoFragment(){
        this.infoFragment =
            TrailInfoFragment(this.trailViewModel, this.isEditable)
        childFragmentManager.beginTransaction()
            .replace(getInfoFragmentId(), this.infoFragment).commit()
        collapseInfoBottomSheet()
    }

    fun showTrailPOIInfoFragment(trailPointOfInterestPosition:Int){
        val poiIsEditable = this.isEditable && this.trailViewModel?.data?.value?.name != null
        this.infoFragment=
            TrailPOIInfoFragment(
                this.trailViewModel, trailPointOfInterestPosition, poiIsEditable
            )
        childFragmentManager.beginTransaction()
            .replace(getInfoFragmentId(), this.infoFragment).commit()
        collapseInfoBottomSheet()
    }
}
