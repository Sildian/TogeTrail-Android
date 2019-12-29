package com.sildian.apps.togetrail.trail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback

/*************************************************************************************************
 * Base for all Trail fragments using a map
 ************************************************************************************************/

abstract class BaseTrailMapFragment : Fragment(), OnMapReadyCallback {

    /**********************************Static items**********************************************/

    companion object{

        /**Logs**/
        const val TAG_FRAGMENT="TAG_FRAGMENT"

        /**Bundles keys**/
        const val KEY_BUNDLE_MAP_VIEW="KEY_BUNDLE_MAP_VIEW"
    }

    /**********************************Abstract methods******************************************/

    abstract fun getLayoutId():Int
    abstract fun getMapViewId():Int

    /**********************************UI component**********************************************/

    protected lateinit var layout:View
    protected lateinit var mapView:MapView

    /************************************Map support*********************************************/

    protected lateinit var map: GoogleMap

    /************************************Life cycle**********************************************/

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?{
        this.layout=inflater.inflate(getLayoutId(), container, false)
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

    /**********************************Maps monitoring*******************************************/

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
            this.map = map
            this.map.mapType= GoogleMap.MAP_TYPE_TERRAIN
        }
        else{
            //TODO handle exception
        }
    }
}
