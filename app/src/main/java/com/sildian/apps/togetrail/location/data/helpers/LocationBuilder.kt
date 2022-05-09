package com.sildian.apps.togetrail.location.data.helpers

import com.google.android.libraries.places.api.model.Place
import com.sildian.apps.togetrail.location.data.models.Country
import com.sildian.apps.togetrail.location.data.models.Location
import com.sildian.apps.togetrail.location.data.models.Region
import java.util.*

/*************************************************************************************************
 * Provides with functions allowing to build a Location
 ************************************************************************************************/

class LocationBuilder {

    /*******************Address components types (matching Google Places API)********************/

    companion object {
        private const val ADDRESS_COMPONENT_COUNTRY = "country"
        private const val ADDRESS_COMPONENT_REGION = "administrative_area_level_1"
    }

    /*******************************Location fields**********************************************/

    private var country:Country?=null
    private var region:Region?=null
    private var fullAddress:String?=null

    /********************************Build steps*************************************************/

    /**
     * Uses a Place from Google Places API to build a Location
     * @param place : a place from Google Places API
     * @return an instance of LocationBuilder
     */

    fun withPlace(place: Place):LocationBuilder{
        this.country= buildCountry(place)
        this.region= buildRegion(place)
        this.fullAddress= buildFullAddress(place)
        return this
    }

    /**
     * Builds the location with the provided fields
     * @return the Location
     */

    fun build():Location{
        return Location(
            this.country,
            this.region,
            this.fullAddress
        )
    }

    /********************************Build items*************************************************/

    /**
     * Builds a country
     * @param place : a Place from Google Places API
     * @return the resulted Country
     */

    private fun buildCountry(place: Place):Country?{

        /*Searches an address component containing country type*/

        val countryComponent= place.addressComponents?.asList()?.firstOrNull { addressComponent ->
            addressComponent.types.contains(ADDRESS_COMPONENT_COUNTRY)
        }

        /*Then return a country with the ISO code and the name*/

        return if(countryComponent!=null&&countryComponent.shortName!=null){
            Country(
                countryComponent.shortName!!,
                countryComponent.name
            )
        } else{
            null
        }
    }

    /**
     * Builds a region
     * @param place : a Place from Google Places API
     * @return the resulted Region
     */

    private fun buildRegion(place:Place):Region?{

        /*Searches an address component containing region type*/

        val regionComponent= place.addressComponents?.asList()?.firstOrNull { addressComponent ->
            addressComponent.types.contains(ADDRESS_COMPONENT_REGION)
        }

        /*Then return a region. Building the region code is quite complex :*/

        if(regionComponent!=null&&regionComponent.shortName!=null){

            /*If the short name is equal to the name, it means that no ISO code is provided by the API
            * We need to create a fake ISO code*/

            if(regionComponent.shortName==regionComponent.name){

                /*If the name contains ' ' or '-', creates the code with each character after these characters*/

                if(regionComponent.name.contains(" ")||regionComponent.name.contains("-")){
                    val regionCode=StringBuilder()
                    val regionName=regionComponent.name
                    regionCode.append(regionName.take(1))
                    regionName.forEachIndexed { index, c ->
                        if(c ==' ' || c =='-'){
                            if(index<regionName.count()){
                                regionCode.append(regionName[index+1])
                            }
                        }
                    }
                    return Region(
                        regionCode.toString().toUpperCase(Locale.ROOT),
                        regionName
                    )

                    /*Else, takes the 2 first characters of the name and assumes it is the code*/

                }else{
                    val regionCode=regionComponent.name.take(2).toUpperCase(Locale.ROOT)
                    val regionName=regionComponent.name
                    return Region(
                        regionCode,
                        regionName
                    )
                }

                /*If the short name is different that the name, assumes this is the code*/

            }else{
                return Region(
                    regionComponent.shortName!!,
                    regionComponent.name
                )
            }
        } else{
            return null
        }
    }

    /**
     * Builds the full address
     * @param place : a Place from Google Places API
     * @return a string
     */

    private fun buildFullAddress(place: Place):String?{
        return place.address
    }
}