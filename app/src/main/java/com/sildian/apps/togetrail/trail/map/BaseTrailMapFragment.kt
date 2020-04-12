package com.sildian.apps.togetrail.trail.map

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.common.baseControllers.BaseDataFlowFragment
import com.sildian.apps.togetrail.common.utils.MapMarkersUtilities
import com.sildian.apps.togetrail.trail.info.TrailInfoFragment
import com.sildian.apps.togetrail.trail.info.TrailPOIInfoFragment
import com.sildian.apps.togetrail.trail.model.core.Trail
import com.sildian.apps.togetrail.trail.model.core.TrailPointOfInterest

/*************************************************************************************************
 * Base for all Trail fragments using a map
 * @param trail : the trail to show
 ************************************************************************************************/

abstract class BaseTrailMapFragment (protected var trail:Trail?=null) :
    BaseDataFlowFragment(),
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

    protected lateinit var mapView:MapView              //The map view
    protected lateinit var infoBottomSheet:BottomSheetBehavior<View>    //Bottom sheet with additional info
    protected lateinit var infoFragment:Fragment        //Nested fragment allowing to see info about the trail

    /**************************************Map support*******************************************/

    protected var map: GoogleMap?=null                                      //Map

    /************************************Location support****************************************/

    protected lateinit var userLocation: FusedLocationProviderClient        //User location

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
        this.map?.isMyLocationEnabled = true
    }

    override fun onPause() {
        this.map?.isMyLocationEnabled=false
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

    override fun saveData(){
        if(this.checkDataIsValid()) {
            this.trail?.autoPopulatePosition()
            (activity as TrailActivity).saveTrailInDatabase(this.trail)
        }
    }

    override fun updateData(data: Any?) {
        if(data is Trail){
            this.trail=data
            this.map?.clear()
            showTrailTrackOnMap()
            showTrailInfoFragment()
        }
    }

    override fun checkDataIsValid(): Boolean {
        if(this.trail!=null) {
            if (this.trail!!.isDataValid()) {
                for(i in this.trail!!.trailTrack.trailPointsOfInterest.indices){
                    if(!this.trail!!.trailTrack.trailPointsOfInterest[i].isDataValid()) {
                        //TODO handle
                        return false
                    }
                }
                return true
            }else{
                //TODO handle
            }
        }else{
            //TODO handle
        }
        return false
    }

    /************************************UI monitoring*******************************************/

    abstract fun getMapViewId():Int

    abstract fun getInfoBottomSheetId():Int

    abstract fun getInfoFragmentId():Int

    abstract fun enableUI()

    abstract fun disableUI()

    private fun initializeInfoBottomSheet(){
        this.infoBottomSheet=
            BottomSheetBehavior.from(this.layout.findViewById(getInfoBottomSheetId()))
        val peekHeight=resources.getDimension(R.dimen.bottom_sheet_peek_height).toInt()
        this.infoBottomSheet.peekHeight=peekHeight
        this.infoBottomSheet.addBottomSheetCallback(InfoBottomSheetCallback())
        hideInfoBottomSheet()
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
            this.map?.isMyLocationEnabled=true
            onMapReadyActionsFinished()
        }
        else{
            //TODO handle exception
            Log.w(TAG, "Map couldn't be loaded in '${this.javaClass.simpleName}'")
        }
    }

    abstract fun onMapReadyActionsFinished()

    /********************************Location monitoring*****************************************/

    private fun initializeUserLocation(){
        this.userLocation=LocationServices.getFusedLocationProviderClient(context!!)
    }

    /***********************************Trail monitoring*****************************************/

    /**
     * Shows the current trail's track on the map
     */

    protected open fun showTrailTrackOnMap(){

        if(this.trail!=null){

            /*Creates and shows the polyline from the trailPoints*/

            val polylineOption=PolylineOptions()
            this.trail?.trailTrack?.trailPoints?.forEach { trailPoint->
                polylineOption.add(LatLng(trailPoint.latitude, trailPoint.longitude))
            }
            polylineOption.color(ContextCompat.getColor(context!!, R.color.colorSecondaryDark))
            this.map?.addPolyline(polylineOption)

            /*Gets the first and the last trailPoints*/

            val firstPoint=this.trail?.trailTrack?.getFirstTrailPoint()
            val lastPoint=this.trail?.trailTrack?.getLastTrailPoint()

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

            if(lastPoint!=null) {
                this.map?.addMarker(
                    MarkerOptions()
                        .position(LatLng(lastPoint.latitude, lastPoint.longitude))
                        .icon(
                            MapMarkersUtilities.createMapMarkerFromVector(
                                context, R.drawable.ic_flag_map)))
                    ?.tag = lastPoint
            }

            /*Adds a marker for each trailPointOfInterest including its number*/

            for(i in this.trail?.trailTrack?.trailPointsOfInterest!!.indices){
                val trailPointOfInterest=this.trail?.trailTrack?.trailPointsOfInterest!![i]
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
        hideInfoBottomSheet()
        (activity as TrailActivity).updateTrailAndEditInfo(this.trail!!)
    }

    /**
     * Edits a trailPointOfInterest's info
     * @param trailPoiPosition : the trailPointOfInterest's position in the trailTrack
     */

    fun editTrailPoiInfo(trailPoiPosition:Int){
        hideInfoBottomSheet()
        (activity as TrailActivity).updateTrailAndEditPoiInfo(this.trail!!, trailPoiPosition)
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
            //TODO implement
        }

        override fun onStateChanged(bottomSheet: View, newState: Int) {
            //TODO implement
        }
    }

    /**************************Nested Fragments monitoring***************************************/

    fun showTrailInfoFragment(){
        this.infoFragment=
            TrailInfoFragment(this.trail)
        childFragmentManager.beginTransaction()
            .replace(getInfoFragmentId(), this.infoFragment).commit()
        collapseInfoBottomSheet()
    }

    fun showTrailPOIInfoFragment(trailPointOfInterest: TrailPointOfInterest, trailPointOfInterestPosition:Int){
        this.infoFragment=
            TrailPOIInfoFragment(
                trailPointOfInterest, trailPointOfInterestPosition
            )
        childFragmentManager.beginTransaction()
            .replace(getInfoFragmentId(), this.infoFragment).commit()
        collapseInfoBottomSheet()
    }
}
