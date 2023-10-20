package com.sildian.apps.togetrail.uiLayer.hikerProfile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sildian.apps.togetrail.common.coroutines.CoroutineIODispatcher
import com.sildian.apps.togetrail.domainLayer.hiker.GetHikerHistoryItemsUseCase
import com.sildian.apps.togetrail.domainLayer.hiker.GetSingleHikerUseCase
import com.sildian.apps.togetrail.uiLayer.entities.hiker.HikerHistoryItemUI
import com.sildian.apps.togetrail.uiLayer.entities.hiker.HikerHistoryItemUI.HikerRegistered as HikerRegistered
import com.sildian.apps.togetrail.uiLayer.entities.hiker.HikerHistoryItemUI.TrailCreated as TrailCreated
import com.sildian.apps.togetrail.uiLayer.entities.hiker.HikerHistoryItemUI.EventCreated as EventCreated
import com.sildian.apps.togetrail.uiLayer.entities.hiker.HikerHistoryItemUI.EventAttended as EventAttended
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HikerProfileViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    @CoroutineIODispatcher private val coroutineDispatcher: CoroutineDispatcher,
    private val getSingleHikerUseCase: GetSingleHikerUseCase,
    private val getHikerHistoryItemsUseCase: GetHikerHistoryItemsUseCase,
) : ViewModel() {

    private val hikerId: String = requireNotNull(
        savedStateHandle[HikerProfileActivity.KEY_BUNDLE_HIKER_ID]
    )

    private val _hikerState = MutableStateFlow<HikerState>(
        value = HikerState.Loading
    )
    val hikerState: StateFlow<HikerState> get() = _hikerState

    private val _historyItemsState = MutableStateFlow<HikerHistoryItemsState>(
        value = HikerHistoryItemsState.Loading
    )
    val historyItemsState: StateFlow<HikerHistoryItemsState> get() = _historyItemsState

    private val _navigationEvent = MutableSharedFlow<HikerProfileNavigationEvent>()
    val navigationEvent: SharedFlow<HikerProfileNavigationEvent> get() = _navigationEvent

    fun loadAll() {
        loadHiker()
        loadHikerHistoryItems()
    }

    fun loadHiker() {
        viewModelScope.launch(context = coroutineDispatcher) {
            _hikerState.value =
                try {
                    val hiker = getSingleHikerUseCase(id = hikerId)
                    HikerState.Result(hiker = hiker)
                } catch (e: Throwable) {
                    HikerState.Error(e = e)
                }
        }
    }

    fun loadHikerHistoryItems() {
        viewModelScope.launch(context = coroutineDispatcher) {
            _historyItemsState.value =
                try {
                    val historyItems = getHikerHistoryItemsUseCase(hikerId = hikerId)
                    HikerHistoryItemsState.Result(historyItems = historyItems)
                } catch (e: Throwable) {
                    HikerHistoryItemsState.Error(e = e)
                }
        }
    }

    fun onEditMenuButtonClick() {
        viewModelScope.launch {
            _navigationEvent.emit(HikerProfileNavigationEvent.NavigateToHikerProfileEdit)
        }
    }

    fun onConversationMenuButtonClick() {
        viewModelScope.launch {
            _navigationEvent.emit(HikerProfileNavigationEvent.NavigateToConversation(interlocutorId = hikerId))
        }
    }

    fun onHikerHistoryItemClick(historyItem: HikerHistoryItemUI) {
        viewModelScope.launch {
            when (historyItem) {
                is HikerRegistered ->
                    Unit
                is TrailCreated ->
                    _navigationEvent.emit(
                        value = HikerProfileNavigationEvent.NavigateToTrail(trailId = historyItem.itemInfo.id)
                    )
                is EventCreated ->
                    _navigationEvent.emit(
                        value = HikerProfileNavigationEvent.NavigateToEvent(eventId = historyItem.itemInfo.id)
                    )
                is EventAttended ->
                    _navigationEvent.emit(
                        value = HikerProfileNavigationEvent.NavigateToEvent(eventId = historyItem.itemInfo.id)
                    )
            }
        }
    }
}