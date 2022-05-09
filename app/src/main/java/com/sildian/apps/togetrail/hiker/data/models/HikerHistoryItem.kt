package com.sildian.apps.togetrail.hiker.data.models

import android.content.Context
import android.os.Parcelable
import com.sildian.apps.togetrail.R
import kotlinx.android.parcel.Parcelize
import java.util.*

/*************************************************************************************************
 * An history item is an information about what an hiker did since he / she registered on TogeTrail
 * @param type : the type of history item
 * @param date : the date of the history item
 * @param itemId : if the item is related to a trail or an event, this is its id
 * @param itemName : if the item is related to a trail or an event, this is its name
 * @param locationName : the name of the location is a location is attached to the item
 * @param photoUrl : the photo url if a photo is attached to the item
 ************************************************************************************************/

@Parcelize
data class HikerHistoryItem (
    val type: HikerHistoryType=HikerHistoryType.UNKNOWN,
    val date: Date=Date(),
    val itemId:String?=null,
    val itemName: String?=null,
    val locationName:String?=null,
    val photoUrl:String?=null
)
    :Parcelable
{

    /**
     * Writes the item within the hiker's history
     * @param context : the context allowing to get app resources
     * @param hikerName : the updated hiker's name
     * @return a string
     */

    fun writeItemHistory(context:Context, hikerName:String):String{
        val phrase=when(this.type){
            HikerHistoryType.HIKER_REGISTERED -> context.getString(R.string.label_hiker_history_hiker_registered)
            HikerHistoryType.TRAIL_CREATED -> context.getString(R.string.label_hiker_history_trail_created)
            HikerHistoryType.EVENT_CREATED -> context.getString(R.string.label_hiker_history_event_created)
            HikerHistoryType.EVENT_ATTENDED -> context.getString(R.string.label_hiker_history_event_attended)
            HikerHistoryType.UNKNOWN -> ""
        }
        return if(this.locationName==null)
            "$hikerName $phrase"
        else
            "$hikerName $phrase ${this.locationName}"
    }
}