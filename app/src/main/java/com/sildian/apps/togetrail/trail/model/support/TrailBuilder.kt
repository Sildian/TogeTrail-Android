package com.sildian.apps.togetrail.trail.model.support

import com.sildian.apps.togetrail.trail.model.core.Trail
import com.sildian.apps.togetrail.trail.model.core.TrailPoint
import com.sildian.apps.togetrail.trail.model.core.TrailPointOfInterest
import com.sildian.apps.togetrail.trail.model.core.TrailTrack
import io.ticofab.androidgpxparser.parser.domain.Gpx

/*************************************************************************************************
 * Provides with functions allowing to build a Trail
 ************************************************************************************************/

object TrailBuilder {

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

    /**********************************Trail fields**********************************************/

    private var name:String?=null
    private var source:String="TogeTrail"
    private var description:String?=null
    private var trailTrack:TrailTrack=TrailTrack()

    /********************************Build steps*************************************************/

    /**
     * Initializes the trail with the default info
     * @return an instance of TrailBuilder
     */

    fun withDefault():TrailBuilder{
        this.name=null
        this.source="TogeTrail"
        this.description=null
        this.trailTrack=TrailTrack()
        return this
    }

    /**
     * Uses a Gpx to build a trail
     * @param gpx : the gpx
     * @return an instance of TrailBuilder
     */

    fun withGpx(gpx:Gpx): TrailBuilder {

        /*If no track is available in the gpx, raises a TrailBuildNoTrackException*/

        if (gpx.tracks.isNullOrEmpty()
            || gpx.tracks[0].trackSegments.isNullOrEmpty()
            || gpx.tracks[0].trackSegments.isNullOrEmpty()
        ) {
            throw TrailBuildNoTrackException(
                EXCEPTION_MESSAGE_BUILD_GPX_NO_TRACK
            )
        }

        /*If more than 1 track are available in the fpx, raises a TrailBuildTooManyTracksException*/

        if(gpx.tracks.size>1){
            throw TrailBuildTooManyTracksException(
                EXCEPTION_MESSAGE_BUILD_GPX_TOO_MANY_TRACKS
            )
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

        /*Sets the fields*/

        this.name = name
        this.source = source
        this.description = description
        this.trailTrack = trailTrack

        return this
    }

    /**
     * Builds a Trail with the provided fields
     * @return a Trail
     */

    fun build():Trail{

        val trail=Trail(
            name = this.name,
            source = this.source,
            description = this.description,
            trailTrack = this.trailTrack
        )

        trail.autoPopulatePosition()
        trail.autoCalculateMetrics()

        return trail
    }
}