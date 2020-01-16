package com.sildian.apps.togetrail.common.model

/*************************************************************************************************
 * Location
 ************************************************************************************************/

data class Location (
    val country:String?=null,
    val region:String?=null,
    val town:String?=null
)
{

    /**
     * Gets the full location to display on the screen.
     * The country at least must be known.
     * @return the full location to display, null if the country is unknown
     */

    fun getFullLocation():String?{
        val fullLocation=StringBuilder()
        if(country==null){
            return null
        }
        fullLocation.append(if(town!=null)"$town, " else "")
        fullLocation.append(if(region!=null)"$region, " else "")
        fullLocation.append(country)

        return fullLocation.toString()
    }
}