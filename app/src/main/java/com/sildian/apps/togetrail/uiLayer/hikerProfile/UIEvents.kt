package com.sildian.apps.togetrail.uiLayer.hikerProfile

sealed interface HikerProfileNavigationEvent {
    data object NavigateToHikerProfileEdit : HikerProfileNavigationEvent
    data class NavigateToTrail(val trailId: String) : HikerProfileNavigationEvent
    data class NavigateToEvent(val eventId: String) : HikerProfileNavigationEvent
    data class NavigateToConversation(val interlocutorId: String) : HikerProfileNavigationEvent
}