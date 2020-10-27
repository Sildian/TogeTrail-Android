package com.sildian.apps.togetrail.trail.map

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.checkSelfPermission
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
import com.sildian.apps.togetrail.common.exceptions.UserLocationException
import com.sildian.apps.togetrail.common.utils.MapMarkersUtilities
import com.sildian.apps.togetrail.common.utils.locationHelpers.UserLocationHelper
import com.sildian.apps.togetrail.common.utils.uiHelpers.DialogHelper
import com.sildian.apps.togetrail.common.utils.uiHelpers.SnackbarHelper
import com.sildian.apps.togetrail.trail.info.BaseInfoFragment
import com.sildian.apps.togetrail.trail.info.TrailInfoFragment
import com.sildian.apps.togetrail.trail.info.TrailPOIInfoFragment
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

abstract class BaseTrailMapFragment (
    protected var trailViewModel: TrailViewModel?=null,
    protected var isEditable:Boolean=false
) :
    BaseFragment(),
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

    protected lateinit var mapView:MapView                              //The map view
    protected lateinit var infoBottomSheet:BottomSheetBehavior<View>    //Bottom sheet with additional info
    protected lateinit var infoFragment:BaseInfoFragment                //Nested fragment allowing to see info about the trail

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

    override fun onResume() {
        super.onResume()
        this.mapView.onResume()
        if(Build.VERSION.SDK_INT<23
            &&checkSelfPermission(context!!, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED) {
            this.map?.isMyLocationEnabled = true
        }
    }

    override fun onPause() {
        if(Build.VERSION.SDK_INT<23
            &&checkSelfPermission(context!!, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED) {
            this.map?.isMyLocationEnabled = false
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
        observeSaveRequestSuccess()
        observeRequestFailure()
    }

    private fun observeTrail() {
        this.trailViewModel?.trail?.observe(this) { trail ->
            if (trail != null) {
                refreshUI()
            }
        }
    }

    private fun observeSaveRequestSuccess() {
        this.trailViewModel?.saveRequestSuccess?.observe(this) { success ->
            if (success) {
                onSaveSuccess()
            }
        }
    }

    private fun observeRequestFailure() {
        this.trailViewModel?.requestFailure?.observe(this) { e ->
            if (e != null) {
                onQueryError(e)
            }
        }
    }

    override fun saveData(){
        hideInfoBottomSheet()
        if(this.checkDataIsValid()) {
            this.trailViewModel?.trail?.value?.autoPopulatePosition()
            this.baseActivity?.showProgressDialog()
            this.trailViewModel?.saveTrailInDatabase(false)
        }
    }

    override fun checkDataIsValid(): Boolean {
        if(this.trailViewModel?.trail?.value != null) {
            if (this.trailViewModel?.trail?.value!!.isDataValid()) {
                for(i in this.trailViewModel?.trail?.value!!.trailTrack.trailPointsOfInterest.indices){
                    if(!this.trailViewModel?.trail?.value!!.trailTrack.trailPointsOfInterest[i].isDataValid()) {
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

    override fun onMapReady(map: GoogleMap?) {
        if(map!=null) {
            Log.d(TAG, "Map is ready in '${this.javaClass.simpleName}'")
            this.map = map
            this.map?.mapType= GoogleMap.MAP_TYPE_TERRAIN
            this.map?.setOnMapClickListener(this)
            this.map?.setOnMarkerClickListener(this)
            if(Build.VERSION.SDK_INT<23
                &&checkSelfPermission(context!!, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED) {
                this.map?.isMyLocationEnabled = true
            }
            this.map?.uiSettings?.isMyLocationButtonEnabled=false
            onMapReadyActionsFinished()
        }
        else{
            Log.w(TAG, "Map couldn't be loaded in '${this.javaClass.simpleName}'")
            DialogHelper.createInfoDialog(
                context!!,
                R.string.message_map_failure_title,
                R.string.message_map_failure_message
            ).show()
        }
    }

    abstract fun onMapReadyActionsFinished()

    /********************************Location monitoring*****************************************/

    private fun initializeUserLocation(){
        this.userLocationProvider=LocationServices.getFusedLocationProviderClient(context!!)
    }

    protected fun zoomToUserLocation() {
        lifecycleScope.launch {
            withContext(Dispatchers.Main) {
                context?.let { context ->

                    if (Build.VERSION.SDK_INT < 23 || checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

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

    /**
     * Shows the current trail's track on the map
     */

    protected open fun showTrailTrackOnMap(){

        if(this.trailViewModel?.trail?.value != null){

            /*Creates and shows the polyline from the trailPoints*/

            val polylineOption=PolylineOptions()
            this.trailViewModel?.trail?.value?.trailTrack?.trailPoints?.forEach { trailPoint->
                polylineOption.add(LatLng(trailPoint.latitude, trailPoint.longitude))
            }
            polylineOption.color(ContextCompat.getColor(context!!, R.color.colorSecondaryDark))
            this.map?.addPolyline(polylineOption)

            /*Gets the first and the last trailPoints*/

            val firstPoint=this.trailViewModel?.trail?.value?.trailTrack?.getFirstTrailPoint()
            val lastPoint=this.trailViewModel?.trail?.value?.trailTrack?.getLastTrailPoint()

            /*Adds markers on the first and the last trailPoints*/

            if(firstPoint!=null) {
                this.map?.addMarker(
                    MarkerOptions()
                        .position(LatLng(firstPoint.latitude, firstPoint.longitude))
                        .icon(
                            MapMarkersUtilities.createMapMarkerFromVector(
                                context, R.drawable.ic_location_trail_map)))
                    ?.tag = firstPoint
            }

            if(lastPoint!=null && this.trailViewModel?.trail?.value?.loop==false) {
                this.map?.addMarker(
                    MarkerOptions()
                        .position(LatLng(lastPoint.latitude, lastPoint.longitude))
                        .icon(
                            MapMarkersUtilities.createMapMarkerFromVector(
                                context, R.drawable.ic_flag_map)))
                    ?.tag = lastPoint
            }

            /*Adds a marker for each trailPointOfInterest including its number*/

            for(i in this.trailViewModel?.trail?.value?.trailTrack?.trailPointsOfInterest!!.indices){
                val trailPointOfInterest=this.trailViewModel?.trail?.value?.trailTrack?.trailPointsOfInterest!![i]
                this.map?.addMarker(MarkerOptions()
                    .position(LatLng(trailPointOfInterest.latitude, trailPointOfInterest.longitude))
                    .icon(MapMarkersUtilities.createMapMarkerFromVector(
                        context, R.drawable.ic_location_trail_poi_map, (i+1).toString()))
                    .snippet(i.toString()))
                    ?.tag=trailPointOfInterest
            }
        }
    }

    /**Edits the trail's info**/

    fun editTrailInfo(){
        this.trailViewModel?.trail?.value?.let { trail ->
            hideInfoBottomSheet()
            (activity as TrailActivity).updateTrailAndEditInfo(trail)
        }
    }

    /**
     * Edits a trailPointOfInterest's info
     * @param trailPoiPosition : the trailPointOfInterest's position in the trailTrack
     */

    fun editTrailPoiInfo(trailPoiPosition:Int){
        this.trailViewModel?.trail?.value?.let { trail ->
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
        this.infoFragment=
            TrailInfoFragment(this.trailViewModel, this.isEditable)
        childFragmentManager.beginTransaction()
            .replace(getInfoFragmentId(), this.infoFragment).commit()
        collapseInfoBottomSheet()
    }

    fun showTrailPOIInfoFragment(trailPointOfInterestPosition:Int){
        val poiIsEditable=this.isEditable && this.trailViewModel?.trail?.value?.name != null
        this.infoFragment=
            TrailPOIInfoFragment(
                this.trailViewModel, trailPointOfInterestPosition, poiIsEditable
            )
        childFragmentManager.beginTransaction()
            .replace(getInfoFragmentId(), this.infoFragment).commit()
        collapseInfoBottomSheet()
    }
}
