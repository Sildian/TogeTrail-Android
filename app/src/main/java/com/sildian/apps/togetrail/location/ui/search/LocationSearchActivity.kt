package com.sildian.apps.togetrail.location.ui.search

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.core.widget.doOnTextChanged
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.sildian.apps.togetrail.R
import com.sildian.apps.togetrail.common.baseControllers.BaseActivity
import com.sildian.apps.togetrail.databinding.ActivityLocationSearchBinding
import com.sildian.apps.togetrail.location.data.core.Location
import com.sildian.apps.togetrail.location.data.helpers.LocationBuilder

/*************************************************************************************************
 * Lets the user search for a location using Google Places API
 ************************************************************************************************/

class LocationSearchActivity :
    BaseActivity<ActivityLocationSearchBinding>(),
    LocationPredictionAdapter.OnLocationClickListener {

    /**********************************Static items**********************************************/

    companion object {

        /**Logs**/
        private const val TAG = "LocationSearchActivity"

        /**Bundle keys for Intents**/
        const val KEY_BUNDLE_FINE_RESEARCH="KEY_BUNDLE_FINE_RESEARCH"
        const val KEY_BUNDLE_LOCATION="KEY_BUNDLE_LOCATION"
    }

    /********************************Places support**********************************************/

    private lateinit var placesClient: PlacesClient
    private lateinit var autocompleteSessionToken:AutocompleteSessionToken

    /*************************************Data***************************************************/

    private var fineResearch = false    //True if the research's precision level is an address. Otherwise it is locality's level.
    private val locationsPredictions = arrayListOf<AutocompletePrediction>()     //The list of predictions given by Google Places API

    /**********************************UI component**********************************************/

    private lateinit var locationsPredictionsAdapter: LocationPredictionAdapter

    /*********************************Life cycle*************************************************/

    override fun onResume() {
        super.onResume()
        this.binding.activityLocationSearchTextFieldResearch.requestFocus()
    }

    /********************************Navigation control******************************************/

    override fun onBackPressed() {
        finishCancel()
    }

    override fun onSupportNavigateUp(): Boolean {
        finishCancel()
        return true
    }

    /******************************Data monitoring************************************************/

    override fun loadData() {
        this.placesClient= Places.createClient(this)
        this.autocompleteSessionToken= AutocompleteSessionToken.newInstance()
        readDataFromIntent()
    }

    private fun readDataFromIntent(){
        if(intent!=null){
            if(intent.hasExtra(KEY_BUNDLE_FINE_RESEARCH)){
                this.fineResearch=intent.getBooleanExtra(KEY_BUNDLE_FINE_RESEARCH, false)
            }
        }
    }

    /******************************UI monitoring**************************************************/

    override fun getLayoutId(): Int = R.layout.activity_location_search

    override fun initializeUI() {
        initializeToolbar()
        initializeResearchTextField()
        initializeLocationsPredictionsRecyclerView()
    }

    private fun initializeToolbar(){
        setSupportActionBar(this.binding.activityLocationSearchToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title=""
    }

    @Suppress("UNUSED_ANONYMOUS_PARAMETER")
    private fun initializeResearchTextField(){
        this.binding.activityLocationSearchTextFieldResearch.doOnTextChanged { text, start, count, after ->
            if (count >= 1) {
                searchLocations()
            }
        }
    }

    private fun initializeLocationsPredictionsRecyclerView(){
        this.locationsPredictionsAdapter=
            LocationPredictionAdapter(
                this.locationsPredictions,
                this
            )
        this.binding.activityLocationSearchRecyclerViewLocationsPredictions.adapter = this.locationsPredictionsAdapter
    }

    /**********************************Location research*****************************************/

    private fun searchLocations(){

        val typeFilter=if(this.fineResearch)
            TypeFilter.ADDRESS
        else
            TypeFilter.REGIONS

        val request=FindAutocompletePredictionsRequest.builder()
            .setSessionToken(this.autocompleteSessionToken)
            .setTypeFilter(typeFilter)
            .setQuery(this.binding.activityLocationSearchTextFieldResearch.text.toString())
            .build()

        this.placesClient.findAutocompletePredictions(request)
            .addOnSuccessListener { response ->
                this.locationsPredictions.clear()
                this.locationsPredictions.addAll(response.autocompletePredictions)
                this.locationsPredictionsAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Log.w(TAG, e.message.toString())
            }
    }

    override fun onLocationClick(placeId: String) {
        Log.d(TAG, "Selected location $placeId")
        searchLocationDetail(placeId)
    }

    private fun searchLocationDetail(placeId: String){
        val request=FetchPlaceRequest
            .builder(
                placeId,
                listOf(Place.Field.NAME, Place.Field.ADDRESS, Place.Field.ADDRESS_COMPONENTS)
            )
            .setSessionToken(this.autocompleteSessionToken)
            .build()

        this.placesClient.fetchPlace(request)
            .addOnSuccessListener { response ->
                val location=LocationBuilder()
                    .withPlace(response.place)
                    .build()
                finishOk(location)
            }
            .addOnFailureListener { e->
                Log.w(TAG, e.message.toString())
            }
    }

    /*************************************Navigation*********************************************/

    private fun finishOk(location:Location){
        val resultIntent= Intent()
        resultIntent.putExtra(KEY_BUNDLE_LOCATION, location)
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }
}
