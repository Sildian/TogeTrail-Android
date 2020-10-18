package com.sildian.apps.togetrail.common.exceptions

/*************************************************************************************************
 * Exception related to a location
 * @param errorCode : gives more info about the reason of the exception
 ************************************************************************************************/

class UserLocationException(val errorCode: ErrorCode): Exception(errorCode.message) {

    /***********************************Messages*************************************************/

    companion object {
        private const val MESSAGE_ACCESS_NOT_GRANTED = "The user location is unreachable because the access permission is not granted."
        private const val MESSAGE_GPS_UNAVAILABLE = "The user location is unreachable because the Gps is unavailable."
        private const val MESSAGE_ERROR_UNKNOWN = "The user location is unreachable for a unknown reason."
    }

    /*****************************Error codes and related messages*******************************/

    enum class ErrorCode(val message: String) {
        ACCESS_NOT_GRANTED(MESSAGE_ACCESS_NOT_GRANTED),
        GPS_UNAVAILABLE(MESSAGE_GPS_UNAVAILABLE),
        ERROR_UNKNOWN(MESSAGE_ERROR_UNKNOWN);
    }
}