package com.sildian.apps.togetrail.trail.map

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.common.utils.MapMarkersUtilities
import com.sildian.apps.togetrail.trail.model.Trail

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

        /**Bundles keys**/
        const val KEY_BUNDLE_MAP_VIEW="KEY_BUNDLE_MAP_VIEW"
    }

    /***************************************Data*************************************************/

    protected var trail: Trail?=null                    //The current trail shown on the map

    /**********************************UI component**********************************************/

    protected lateinit var layout:View                  //The fragment's layout
    protected lateinit var mapView:MapView              //The map view
    protected lateinit var infoBottomSheet:BottomSheetBehavior<View>    //Bottom sheet with additional info
    protected lateinit var infoFragment:Fragment        //Nested fragment within the bottomSheet

    /************************************Map support*********************************************/

    protected lateinit var map: GoogleMap               //Map

    /************************************Life cycle**********************************************/

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?{
        this.layout=inflater.inflate(getLayoutId(), container, false)
        initializeInfoBottomSheet()
        initializeMap(savedInstanceState)
        return this.layout
    }

    override fun onStart() {
        super.onStart()
        this.mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        this.mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
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
     * Updates the current trail then shows it on the map and shows its related info
     * @param trail : the trail to be shown
     */

    fun updateTrail(trail:Trail?){
        this.trail=trail
        showTrailOnMap()
        showDefaultInfoFragment()
    }

    /************************************UI monitoring*******************************************/

    abstract fun getLayoutId():Int

    abstract fun getMapViewId():Int

    abstract fun getInfoBottomSheetId():Int

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
            this.map.mapType= GoogleMap.MAP_TYPE_TERRAIN
            this.map.setOnMapClickListener(this)
            this.map.setOnMarkerClickListener(this)
            this.map.setOnPolylineClickListener(this)
            proceedAdditionalOnMapReadyActions()
        }
        else{
            //TODO handle exception
            Log.w(TAG_MAP, "Map couldn't be loaded in '${this.javaClass.simpleName}'")
        }
    }

    abstract fun proceedAdditionalOnMapReadyActions()

    /***********************************Trail monitoring*****************************************/

    /**
     * Shows the current trail on the map
     */

    protected open fun showTrailOnMap(){

        if(this.trail!=null){

            /*Creates and shows the polyline from the trailPoints*/

            val polylineOption=PolylineOptions()
            this.trail?.trailTrack?.trailPoints?.forEach { trailPoint->
                polylineOption.add(LatLng(trailPoint.latitude, trailPoint.longitude))
            }
            polylineOption.color(resources.getColor(R.color.colorSecondaryDark))
            this.map.addPolyline(polylineOption)

            /*Gets the first and the last trailPoints*/

            val firstPoint=this.trail?.trailTrack!!.trailPoints.first()
            val lastPoint=this.trail?.trailTrack!!.trailPoints.last()

            /*Adds markers on the first and the last trailPoints*/

            this.map.addMarker(MarkerOptions()
                .position(LatLng(firstPoint.latitude, firstPoint.longitude))
                .icon(MapMarkersUtilities.createMapMarkerFromVector(
                    context, R.drawable.ic_location_trail_map)))
                .tag=firstPoint

            this.map.addMarker(MarkerOptions()
                .position(LatLng(lastPoint.latitude, lastPoint.longitude))
                .icon(MapMarkersUtilities.createMapMarkerFromVector(
                    context, R.drawable.ic_flag_map)))
                .tag=lastPoint

            /*Adds a marker for each trailPointOfInterest including its number*/

            for(i in this.trail?.trailTrack?.trailPointsOfInterest!!.indices){
                val trailPointOfInterest=this.trail?.trailTrack?.trailPointsOfInterest!![i]
                this.map.addMarker(MarkerOptions()
                    .position(LatLng(trailPointOfInterest.latitude, trailPointOfInterest.longitude))
                    .icon(MapMarkersUtilities.createMapMarkerFromVector(
                        context, R.drawable.ic_location_trail_poi_map, (i+1).toString())))
                    .tag=trailPointOfInterest
            }
        }
    }

    /**************************Nested Fragments monitoring***************************************/

    abstract fun showDefaultInfoFragment()

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
}
