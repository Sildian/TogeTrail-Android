package com.sildian.apps.togetrail.trail.data.dataRequests

import com.sildian.apps.togetrail.common.baseDataRequests.LoadDataRequest
import com.sildian.apps.togetrail.trail.data.helpers.TrailBuilder
import com.sildian.apps.togetrail.trail.data.models.Trail
import io.ticofab.androidgpxparser.parser.GPXParser
import kotlinx.coroutines.CoroutineDispatcher
import java.io.InputStream

/*************************************************************************************************
 * Loads a trail from a Gpx file
 ************************************************************************************************/

class TrailLoadGpxDataRequest(
    dispatcher: CoroutineDispatcher,
    private val inputStream: InputStream,
    private val parser: GPXParser,
)
    : LoadDataRequest<Trail>(dispatcher)
{

    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun load(): Trail? {
        val gpx = parser.parse(inputStream)
        val trail = TrailBuilder()
            .withGpx(gpx)
            .build()
        inputStream.close()
        return trail
    }
}