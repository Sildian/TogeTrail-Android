package com.sildian.apps.togetrail.location.search

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
import com.sildian.apps.togetrail.location.model.core.Location
import com.sildian.apps.togetrail.location.model.support.LocationBuilder
import kotlinx.android.synthetic.main.activity_location_search.*

/*************************************************************************************************
 * Lets the user search for a location using Google Places API
 ************************************************************************************************/

class LocationSearchActivity : AppCompatActivity(), LocationPredictionViewHolder.OnLocationClickListener {

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

    private var fineResearch=false  //True if the research's precision level is an address. Otherwise it is locality's level.
    private val locationsPredictions= arrayListOf<AutocompletePrediction>()     //The list of predictions given by Google Places API

    /**********************************UI component**********************************************/

    private val toolbar by lazy {activity_location_search_toolbar}
    private val researchTextField by lazy {activity_location_search_text_field_research}
    private val locationsPredictionsRecyclerView by lazy {activity_location_search_recycler_view_locations_predictions}
    private lateinit var locationsPredictionsAdapter: LocationPredictionAdapter

    /************************************Life cycle**********************************************/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_search)
        this.placesClient= Places.createClient(this)
        this.autocompleteSessionToken= AutocompleteSessionToken.newInstance()
        readDataFromIntent()
        initializeToolbar()
        initializeResearchTextField()
        initializeLocationsPredictionsRecyclerView()
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

    private fun readDataFromIntent(){
        if(intent!=null){
            if(intent.hasExtra(KEY_BUNDLE_FINE_RESEARCH)){
                this.fineResearch=intent.getBooleanExtra(KEY_BUNDLE_FINE_RESEARCH, false)
            }
        }
    }

    /******************************UI monitoring**************************************************/

    private fun initializeToolbar(){
        setSupportActionBar(this.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title=""
    }

    private fun initializeResearchTextField(){
        this.researchTextField.doOnTextChanged { text, start, count, after ->
            if(count>=1) {
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
        this.locationsPredictionsRecyclerView.adapter=this.locationsPredictionsAdapter
    }

    /**********************************Location research*****************************************/

    /**Searches locations matching the research text with Google Places Autocomplete**/

    private fun searchLocations(){

        val typeFilter=if(this.fineResearch)
            TypeFilter.ADDRESS
        else
            TypeFilter.REGIONS

        val request=FindAutocompletePredictionsRequest.builder()
            .setSessionToken(this.autocompleteSessionToken)
            .setTypeFilter(typeFilter)
            .setQuery(this.researchTextField.text.toString())
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

    /**Handles location click**/

    override fun onLocationClick(placeId: String) {
        Log.d(TAG, "Selected location $placeId")
        searchLocationDetail(placeId)
    }

    /**Searches a location's detail with Google Places**/

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
                val location=LocationBuilder
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

    private fun finishCancel(){
        setResult(Activity.RESULT_CANCELED)
        finish()
    }
}