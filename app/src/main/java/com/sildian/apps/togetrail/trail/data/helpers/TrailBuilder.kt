package com.sildian.apps.togetrail.trail.data.helpers

import com.sildian.apps.togetrail.trail.data.core.Trail
import com.sildian.apps.togetrail.trail.data.core.TrailPoint
import com.sildian.apps.togetrail.trail.data.core.TrailPointOfInterest
import com.sildian.apps.togetrail.trail.data.core.TrailTrack
import io.ticofab.androidgpxparser.parser.domain.Gpx

/*************************************************************************************************
 * Provides with functions allowing to build a Trail
 ************************************************************************************************/

class TrailBuilder {

    /**********************************Trail fields**********************************************/

    private var name: String? = null
    private var source: String = "TogeTrail"
    private var description: String? = null
    private var trailTrack: TrailTrack = TrailTrack()

    /********************************Build steps*************************************************/

    /**
     * Initializes the trail with the default info
     * @return an instance of TrailBuilder
     */

    fun withDefault(): TrailBuilder {
        this.name = null
        this.source = "TogeTrail"
        this.description = null
        this.trailTrack = TrailTrack()
        return this
    }

    /**
     * Uses a Gpx to build a trail
     * @param gpx : the gpx
     * @return an instance of TrailBuilder
     * @throws TrailBuildException when the provided gpx is invalid
     */

    @Throws(TrailBuildException::class)
    fun withGpx(gpx: Gpx): TrailBuilder {

        /*If no track is available in the gpx, raises an exception*/

        if (gpx.tracks.isNullOrEmpty()
            || gpx.tracks[0].trackSegments.isNullOrEmpty()
            || gpx.tracks[0].trackSegments[0].trackPoints.isNullOrEmpty()
        ) {
            throw TrailBuildException(TrailBuildException.ErrorCode.NO_TRACK)
        }

        /*If more than 1 track are available in the fpx, raises an exception*/

        if (gpx.tracks.size > 1) {
            throw TrailBuildException(TrailBuildException.ErrorCode.TOO_MANY_TRACKS)
        }

        /*Name, source and description are populated by the metadata or by the track.
        * If both are null, then sets ""*/

        val name = gpx.metadata?.name ?: gpx.tracks[0]?.trackName ?: ""
        val source = gpx.creator ?: ""
        val description = gpx.metadata?.desc?: gpx.tracks[0]?.trackDesc ?: ""
        val trailTrack = TrailTrack()

        /*The trailPoints are populated by each trackPoint in the gpx*/

        gpx.tracks[0]?.trackSegments?.forEach { segment ->
            segment?.trackPoints?.forEach { point ->
                if (point.latitude != null && point.longitude != null) {
                    trailTrack.trailPoints.add(
                        TrailPoint(
                            point.latitude, point.longitude,
                            point.elevation?.toInt(), point.time?.toDate()
                        )
                    )
                }
            }
        }

        /*The trailPointsOfInterest are populated by each wayPoint in the gpx*/

        gpx.wayPoints?.forEach { point ->
            if (point.latitude != null && point.longitude != null) {
                trailTrack.trailPointsOfInterest.add(
                    TrailPointOfInterest(
                        point.latitude, point.longitude,
                        point.elevation?.toInt(), point.time?.toDate(),
                        point.name, point.desc
                    )
                )
            }
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

    fun build(): Trail {

        val trail = Trail(
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