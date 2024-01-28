package com.sildian.apps.togetrail.uiLayer.hikerProfile.edit

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.sildian.apps.togetrail.common.coroutines.CoroutineTestRule
import com.sildian.apps.togetrail.common.network.AuthException
import com.sildian.apps.togetrail.common.utils.nextLocalDate
import com.sildian.apps.togetrail.common.utils.nextString
import com.sildian.apps.togetrail.common.utils.nextUrlString
import com.sildian.apps.togetrail.domainLayer.hiker.UpdateHikerProfileUseCase
import com.sildian.apps.togetrail.domainLayer.hiker.UpdateHikerProfileUseCaseFake
import com.sildian.apps.togetrail.uiLayer.entities.hiker.nextHikerUI
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import kotlin.random.Random

class HikerProfileEditViewModelTest {

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private fun initViewModel(
        hikerEditState: HikerEditState = HikerEditState.Initializing,
        updateHikerProfileUseCase: UpdateHikerProfileUseCase = UpdateHikerProfileUseCaseFake(),
    ): HikerProfileEditViewModel =
        HikerProfileEditViewModel(
            savedStateHandle = SavedStateHandle().apply {
                set(HikerProfileEditViewModel.KEY_SAVED_STATE_HIKER_EDIT, hikerEditState)
            },
            coroutineDispatcher = coroutineTestRule.dispatcher,
            updateHikerProfileUseCase = updateHikerProfileUseCase,
        )

    @Test
    fun `GIVEN hiker WHEN onInit THEN HikerEditState is NotEdited`() = runTest {
        // Given
        val hiker = Random.nextHikerUI()
        val viewModel = initViewModel()

        // When
        viewModel.onInit(hiker = hiker)

        // Then
        viewModel.hikerEditState.test {
            val expectedState = HikerEditState.Initialized.NotEdited(hiker = hiker)
            assertEquals(expectedState, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GIVEN nothing WHEN onAddPictureButtonClick THEN trigger ShowImagePicker event`() = runTest {
        // Given
        val viewModel = initViewModel()

        viewModel.hikerEditEvent.test {
            // When
            viewModel.onAddPictureButtonClick()

            // Then
            val expectedEvent = HikerProfileEditEvent.ShowImagePicker
            assertEquals(expectedEvent, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GIVEN uri WHEN onPictureSelected THEN update state with UpdatePhoto action`() = runTest {
        // Given
        val startingState = HikerEditState.Initialized.NotEdited(hiker = Random.nextHikerUI())
        val viewModel = initViewModel(hikerEditState = startingState)
        val uri = Random.nextUrlString()

        // When
        viewModel.onPictureSelected(uri = uri)

        // Then
        val expectedState = startingState.reduce(
            action = HikerProfileEditAction.UpdatePhoto(uri = uri)
        )
        viewModel.hikerEditState.test {
            assertEquals(expectedState, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GIVEN text WHEN onNameFieldUpdated THEN update state with UpdateTextField action`() = runTest {
        // Given
        val startingState = HikerEditState.Initialized.NotEdited(hiker = Random.nextHikerUI())
        val viewModel = initViewModel(hikerEditState = startingState)
        val name = Random.nextString()

        // When
        viewModel.onNameFieldUpdated(text = name)

        // Then
        val expectedState = startingState.reduce(
            action = HikerProfileEditAction.UpdateTextField(
                field = HikerProfileEditTextField.Name,
                value = name,
            )
        )
        viewModel.hikerEditState.test {
            assertEquals(expectedState, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GIVEN nothing WHEN onBirthdayFieldClick THEN trigger ShowDatePicker event`() = runTest {
        // Given
        val hiker = Random.nextHikerUI()
        val viewModel = initViewModel().apply {
            onInit(hiker = hiker)
        }

        viewModel.hikerEditEvent.test {
            // When
            viewModel.onBirthdayFieldClick()

            // Then
            val expectedEvent = HikerProfileEditEvent.ShowDatePicker(selectedDate = hiker.birthday)
            assertEquals(expectedEvent, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GIVEN date WHEN onDateSelected THEN update state with UpdateBirthday action`() = runTest {
        // Given
        val startingState = HikerEditState.Initialized.NotEdited(hiker = Random.nextHikerUI())
        val viewModel = initViewModel(hikerEditState = startingState)
        val date = Random.nextLocalDate()

        // When
        viewModel.onDateSelected(date = date)

        // Then
        val expectedState = startingState.reduce(
            action = HikerProfileEditAction.UpdateBirthday(date = date)
        )
        viewModel.hikerEditState.test {
            assertEquals(expectedState, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GIVEN nothing WHEN onHomeFieldClick THEN trigger ShowLocationPicker event`() = runTest {
        // Given
        val viewModel = initViewModel()

        viewModel.hikerEditEvent.test {
            // When
            viewModel.onHomeFieldClick()

            // Then
            val expectedEvent = HikerProfileEditEvent.ShowLocationPicker
            assertEquals(expectedEvent, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GIVEN text WHEN onDescriptionFieldUpdated THEN update state with UpdateTextField action`() = runTest {
        // Given
        val startingState = HikerEditState.Initialized.NotEdited(hiker = Random.nextHikerUI())
        val viewModel = initViewModel(hikerEditState = startingState)
        val description = Random.nextString()

        // When
        viewModel.onDescriptionFieldUpdated(text = description)

        // Then
        val expectedState = startingState.reduce(
            action = HikerProfileEditAction.UpdateTextField(
                field = HikerProfileEditTextField.Description,
                value = description,
            )
        )
        viewModel.hikerEditState.test {
            assertEquals(expectedState, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GIVEN NotEdited HikerEditState WHEN onSaveMenuButtonClick THEN trigger NotifyProfileUpdateEmpty event`() = runTest {
        // Given
        val hikerEditState = HikerEditState.Initialized.NotEdited(hiker = Random.nextHikerUI())
        val viewModel = initViewModel(hikerEditState = hikerEditState)

        viewModel.hikerEditEvent.test {
            // When
            viewModel.onSaveMenuButtonClick()

            // Then
            val expectedEvent = HikerProfileEditEvent.NotifyProfileUpdateEmpty
            assertEquals(expectedEvent, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GIVEN Edited HikerEditState with fields errors WHEN onSaveMenuButtonClick THEN trigger NotifyProfileUpdateFieldsErrors event`() = runTest {
        // Given
        val hikerEditState = HikerEditState.Initialized.Edited(
            hiker = Random.nextHikerUI(),
            oldPhotoUrl = null,
            newPhotoUri = null,
            errorFields = listOf(HikerProfileEditTextField.Name),
        )
        val viewModel = initViewModel(hikerEditState = hikerEditState)

        viewModel.hikerEditEvent.test {
            // When
            viewModel.onSaveMenuButtonClick()

            // Then
            val expectedEvent = HikerProfileEditEvent.NotifyProfileUpdateFieldsError
            assertEquals(expectedEvent, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GIVEN Edited HikerEditState and useCase error WHEN onSaveMenuButtonClick THEN trigger NotifyProfileUpdateError event`() = runTest {
        // Given
        val hikerEditState = HikerEditState.Initialized.Edited(
            hiker = Random.nextHikerUI(),
            oldPhotoUrl = null,
            newPhotoUri = null,
            errorFields = emptyList(),
        )
        val error = AuthException.UnknownException()
        val viewModel = initViewModel(
            hikerEditState = hikerEditState,
            updateHikerProfileUseCase = UpdateHikerProfileUseCaseFake(error = error),
        )

        viewModel.hikerEditEvent.test {
            // Then
            viewModel.onSaveMenuButtonClick()

            // Then
            val expectedEvent = HikerProfileEditEvent.NotifyProfileUpdateError(e = error)
            assertEquals(expectedEvent, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GIVEN Edited HikerEditState and useCase success WHEN onSaveMenuButtonClick THEN trigger NotifyProfileUpdateSuccess event`() = runTest {
        // Given
        val hikerEditState = HikerEditState.Initialized.Edited(
            hiker = Random.nextHikerUI(),
            oldPhotoUrl = null,
            newPhotoUri = null,
            errorFields = emptyList(),
        )
        val viewModel = initViewModel(hikerEditState = hikerEditState)

        viewModel.hikerEditEvent.test {
            // Then
            viewModel.onSaveMenuButtonClick()

            // Then
            val expectedEvent = HikerProfileEditEvent.NotifyProfileUpdateSuccess
            assertEquals(expectedEvent, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }
}