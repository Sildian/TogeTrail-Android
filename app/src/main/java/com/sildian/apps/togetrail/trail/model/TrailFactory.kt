package com.sildian.apps.togetrail.trail.model

import io.ticofab.androidgpxparser.parser.domain.Gpx
import java.util.*

/*************************************************************************************************
 * Provides with functions allowing to build a Trail
 ************************************************************************************************/

object TrailFactory {

    /******************************Exceptions messages*******************************************/

    private const val EXCEPTION_MESSAGE_BUILD_GPX_NO_TRACK="No track is available in the gpx."
    private const val EXCEPTION_MESSAGE_BUILD_GPX_TOO_MANY_TRACKS="Gpx with more than one track are not supported."

    /***********************************Exceptions***********************************************/

    /**
     * This exception is raised when no track is available while trying to build a Trail
     * @param message : the exception message
     */

    class TrailBuildNoTrackException(message:String):Exception(message)

    /**
     * This exception is raised when too many tracks exist while trying to build a Trail
     * @param message : the exception message
     */

    class TrailBuildTooManyTracksException(message:String):Exception(message)

    /**
     * Builds a new Trail from nothing but a name. Sets the source as TogeTrail and the date as today.
     * @param name : the name of the trail
     * @return the resulted trail
     */

    fun buildFromNothing(name:String):Trail{
        val source="TogeTrail"
        val date= Date()
        return Trail(
            name=name,
            source=source,
            creationDate = date,
            lastUpdate = date
        )
    }

    /**
     * Builds a Trail by uploading a gpx file
     * @param gpx : the gpx file
     * @return the resulted Trail
     */

    fun buildFromGpx(gpx:Gpx):Trail {

        /*If no track is available in the gpx, raises a TrailBuildNoTrackException*/

        if (gpx.tracks.isNullOrEmpty()
            || gpx.tracks[0].trackSegments.isNullOrEmpty()
            || gpx.tracks[0].trackSegments.isNullOrEmpty()
        ) {
            throw TrailBuildNoTrackException(EXCEPTION_MESSAGE_BUILD_GPX_NO_TRACK)
        }

        /*If more than 1 track are available in the fpx, raises a TrailBuildTooManyTracksException*/

        if(gpx.tracks.size>1){
            throw TrailBuildTooManyTracksException(EXCEPTION_MESSAGE_BUILD_GPX_TOO_MANY_TRACKS)
        }

        /*Name, source and description are populated by the metadata or by the track.
        * If both are null, then sets ""*/

        val name =
            if (gpx.metadata.name != null) gpx.metadata.name
            else if (gpx.tracks[0].trackName != null) gpx.tracks[0].trackName
            else ""
        val source =
            if (gpx.creator != null) gpx.creator
            else ""
        val description =
            if (gpx.metadata.desc != null) gpx.metadata.desc
            else if (gpx.tracks[0].trackDesc != null) gpx.tracks[0].trackDesc
            else ""

        /*Date as of today*/

        val date=Date()

        val trailTrack = TrailTrack()

        /*The trailPoints are populated by each trackPoint in the gpx*/

        gpx.tracks[0].trackSegments.forEach { segment ->
            segment.trackPoints.forEach { point ->
                trailTrack.trailPoints.add(
                    TrailPoint(
                        point.latitude, point.longitude,
                        point.elevation.toInt(), point.time.toDate()
                    )
                )
            }
        }

        /*The trailPointsOfInterest are populated by each wayPoint in the gpx*/

        gpx.wayPoints.forEach { point ->
            trailTrack.trailPointsOfInterest.add(
                TrailPointOfInterest(
                    point.latitude, point.longitude,
                    point.elevation.toInt(), point.time.toDate(),
                    point.name, point.desc
                )
            )
        }

        /*Then return the Trail*/

        return Trail(
            name = name,
            source = source,
            description = description,
            creationDate = date,
            lastUpdate = date,
            trailTrack = trailTrack
        )
    }
}