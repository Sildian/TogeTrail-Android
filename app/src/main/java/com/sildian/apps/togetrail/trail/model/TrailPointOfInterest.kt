package com.sildian.apps.togetrail.trail.model

import java.util.*

/*************************************************************************************************
 * A TrailPointOfInterest is a specific TrailPoint including
 * some bonus information about the place around the point
 * @param latitude : the latitude
 * @param longitude : the longitude
 * @param elevation : the elevation (in meters)
 * @param time : the registered time of the point
 * @param name : the name of the point
 * @param description : the description of the place
 * @param photoUrl : the url of the photo representing the place
 ************************************************************************************************/

class TrailPointOfInterest(
    latitude:Double=0.0,
    longitude:Double=0.0,
    elevation:Int?=null,
    time:Date?=null,
    var name:String?=null,
    var description:String?=null,
    var photoUrl:String?=null
)
    :TrailPoint(latitude, longitude, elevation, time)