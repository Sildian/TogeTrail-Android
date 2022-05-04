package com.sildian.apps.togetrail.trail.data.helpers

/*************************************************************************************************
 * Exception related to a Trail build issue
 * @param errorCode : gives more info about the reason of the exception
 ************************************************************************************************/

class TrailBuildException(val errorCode: ErrorCode): Exception(errorCode.message) {

    /***********************************Messages*************************************************/

    companion object {
        private const val MESSAGE_BUILD_TRAIL_NO_TRACK = "No track is available in the provided file."
        private const val MESSAGE_BUILD_TRAIL_TOO_MANY_TRACKS = "Files with more than one track are not supported."
    }

    /*****************************Error codes and related messages*******************************/

    enum class ErrorCode(val message: String) {
        NO_TRACK(MESSAGE_BUILD_TRAIL_NO_TRACK),
        TOO_MANY_TRACKS(MESSAGE_BUILD_TRAIL_TOO_MANY_TRACKS);
    }
}