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
import com.sildian.apps.togetrail.common.utils.MapMarkersUtilities
import com.sildian.apps.togetrail.trail.info.TrailInfoFragment
import com.sildian.apps.togetrail.trail.info.TrailPOIInfoFragment
import com.sildian.apps.togetrail.trail.model.Trail
import com.sildian.apps.togetrail.trail.model.TrailPointOfInterest

/*************************************************************************************************
 * Base for all Trail fragments using a map
 ************************************************************************************************/

abstract class BaseTrailMapFragment :
    Fragment(),
    OnMapReadyCallback,
    GoogleMap.OnMapClickListener,
    GoogleMap.OnMarkerClickListener,
    GoogleMap.OnPolylineClickListener
{

    /**********************************Static items**********************************************/

    companion object{

        /**Logs**/
        const val TAG_FRAGMENT="TAG_FRAGMENT"
        const val TAG_MAP="TAG_MAP"
        const val TAG_LOCATION="TAG_LOCATION"

        /**Bundles keys**/
        const val KEY_BUNDLE_MAP_VIEW="KEY_BUNDLE_MAP_VIEW"
    }

    /***************************************Data*************************************************/

    protected var trail: Trail?=null                    //The current trail shown on the map

    /**********************************UI component**********************************************/

    protected lateinit var layout:View                  //The fragment's layout
    protected lateinit var mapView:MapView              //The map view
    protected lateinit var infoBottomSheet:BottomSheetBehavior<View>    //Bottom sheet with additional info
    protected lateinit var infoFragment:Fragment        //Nested fragment allowing to see info about the trail

    /**************************************Map support*******************************************/

    protected var map: GoogleMap?=null                                      //Map

    /************************************Location support****************************************/

    protected lateinit var userLocation: FusedLocationProviderClient        //User location

    /************************************Life cycle**********************************************/

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?{
        this.layout=inflater.inflate(getLayoutId(), container, false)
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
        super.onPause()
        this.map?.isMyLocationEnabled=false
        this.mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        this.mapView.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        this.mapView.onDestroy()
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

    /**
     * Updates the current trail
     * @param trail : the trail
     */

    fun updateTrail(trail:Trail?){
        this.trail=trail
        this.map?.clear()
        showTrailTrackOnMap()
    }

    /**
     * Updates the current trail then shows it on the map and shows its related info
     * @param trail : the trail to be shown
     */

    fun updateTrailAndShowInfo(trail:Trail?){
        this.trail=trail
        this.map?.clear()
        showTrailTrackOnMap()
        showTrailInfoFragment()
    }

    /************************************UI monitoring*******************************************/

    abstract fun getLayoutId():Int

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
            Log.d(TAG_MAP, "Map is ready in '${this.javaClass.simpleName}'")
            this.map = map
            this.map?.mapType= GoogleMap.MAP_TYPE_TERRAIN
            this.map?.setOnMapClickListener(this)
            this.map?.setOnMarkerClickListener(this)
            this.map?.setOnPolylineClickListener(this)
            this.map?.isMyLocationEnabled=true
            proceedAdditionalOnMapReadyActions()
        }
        else{
            //TODO handle exception
            Log.w(TAG_MAP, "Map couldn't be loaded in '${this.javaClass.simpleName}'")
        }
    }

    abstract fun proceedAdditionalOnMapReadyActions()

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

            val firstPoint=this.trail?.trailTrack!!.trailPoints.first()
            val lastPoint=this.trail?.trailTrack!!.trailPoints.last()

            /*Adds markers on the first and the last trailPoints*/

            this.map?.addMarker(MarkerOptions()
                .position(LatLng(firstPoint.latitude, firstPoint.longitude))
                .icon(MapMarkersUtilities.createMapMarkerFromVector(
                    context, R.drawable.ic_location_trail_map)))
                ?.tag=firstPoint

            this.map?.addMarker(MarkerOptions()
                .position(LatLng(lastPoint.latitude, lastPoint.longitude))
                .icon(MapMarkersUtilities.createMapMarkerFromVector(
                    context, R.drawable.ic_flag_map)))
                ?.tag=lastPoint

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
