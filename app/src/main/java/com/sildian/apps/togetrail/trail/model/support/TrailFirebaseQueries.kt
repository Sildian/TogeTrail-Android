package com.sildian.apps.togetrail.trail.model.support

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import com.sildian.apps.togetrail.location.model.core.Location
import com.sildian.apps.togetrail.common.utils.GeoUtilities
import com.sildian.apps.togetrail.trail.model.core.Trail

/*************************************************************************************************
 * Provides with Firebase queries on Trail
 ************************************************************************************************/

object TrailFirebaseQueries {

    /*********************************Collection references**************************************/

    private const val COLLECTION_NAME="trail"
    private fun getCollection() = FirebaseFirestore.getInstance().collection(COLLECTION_NAME)

    /*************************************Queries************************************************/

    /**
     * Gets the trails from the database
     * @return a query
     */

    fun getTrails():Query =
        getCollection().orderBy("creationDate", Query.Direction.DESCENDING)

    /**
     * Gets the 20 last created trails
     * @return a query
     */

    fun getLastTrails():Query =
        getCollection()
            .orderBy("creationDate", Query.Direction.DESCENDING)
            .limit(20)

    /**
     * Gets the trails created by a specific user
     * @param authorId : the id of the author
     * @return a query
     */

    fun getMyTrails(authorId:String):Query =
        getCollection()
            .whereEqualTo("authorId", authorId)
            .orderBy("creationDate", Query.Direction.DESCENDING)

    /**
     * Gets the trails nearby a location
     * If the region is not null, gets the trails within the region
     * Else if the country is not null, gets the trails within the country
     * @param location : the location
     * @return a query or null if the location is empty
     */

    fun getTrailsNearbyLocation(location: Location):Query? =
        when {
            location.region!=null -> {
                getCollection()
                    .whereEqualTo(FieldPath.of("location", "country", "code"), location.country?.code)
                    .whereEqualTo(FieldPath.of("location", "region", "code"), location.region.code)
                    .orderBy("creationDate", Query.Direction.DESCENDING)
            }
            location.country!=null -> {
                getCollection()
                    .whereEqualTo(FieldPath.of("location", "country", "code"), location.country.code)
                    .orderBy("creationDate", Query.Direction.DESCENDING)
            }
            else -> null
        }

    /**
     * Gets the trails around a point, using bounds to calculate the area limitations
     * @param point : the origin point
     * @return a query
     */

    fun getTrailsAroundPoint(point: LatLng):Query{
        val bounds=GeoUtilities.getBoundsAroundOriginPoint(point)
        val minGeoPoint=GeoPoint(bounds.southwest.latitude, bounds.southwest.longitude)
        val maxGeoPoint=GeoPoint(bounds.northeast.latitude, bounds.northeast.longitude)
        return getCollection()
            .whereGreaterThanOrEqualTo("position", minGeoPoint)
            .whereLessThanOrEqualTo("position", maxGeoPoint)
            .orderBy("position")
            .orderBy("creationDate", Query.Direction.DESCENDING)
    }

    /**
     * Gets a given trail
     * @param id : the id of the trail
     * @return a task result
     */

    fun getTrail(id:String):Task<DocumentSnapshot> =
        getCollection().document(id).get()

    /**
     * Creates a new trail in the database
     * @param trail : the trail
     * @return a task result
     */

    fun createTrail(trail:Trail):Task<DocumentReference> =
        getCollection().add(trail)

    /**
     * Updates an existing trail in the database
     * @param trail : the trail
     * @return a task result
     */

    fun updateTrail(trail:Trail):Task<Void> =
        getCollection().document(trail.id!!).set(trail)
}