package com.sildian.apps.togetrail.uiLayer.hikerProfile.edit

import com.sildian.apps.togetrail.common.core.location.nextLocation
import com.sildian.apps.togetrail.common.utils.nextLocalDate
import com.sildian.apps.togetrail.common.utils.nextString
import com.sildian.apps.togetrail.common.utils.nextUrlString
import com.sildian.apps.togetrail.uiLayer.entities.hiker.nextHikerUI
import org.junit.Assert.*
import org.junit.Test
import kotlin.random.Random

class HikerEditStateReducerTest {

    @Test
    fun `GIVEN Initializing state and any action WHEN reduce THEN do not update`() {
        // Given
        val state = HikerEditState.Initializing
        val action = HikerProfileEditAction.UpdatePhoto(uri = Random.nextUrlString())

        // When
        val newState = state.reduce(action = action)

        // Then
        val expectedState = HikerEditState.Initializing
        assertEquals(expectedState, newState)
    }

    @Test
    fun `GIVEN UpdateTextField Name WHEN reduce THEN update name`() {
        // Given
        val hiker = Random.nextHikerUI()
        val state = HikerEditState.Initialized.NotEdited(hiker = hiker)
        val field = HikerProfileEditTextField.Name
        val newValue = Random.nextString()
        val action = HikerProfileEditAction.UpdateTextField(field = field, value = newValue)

        // When
        val newState = state.reduce(action = action)

        // Then
        val expectedState = HikerEditState.Initialized.Edited(
            hiker = hiker.copy(name = newValue),
            oldPhotoUrl = null,
            newPhotoUri = null,
            errorFields = emptyList(),
        )
        assertEquals(expectedState, newState)
    }

    @Test
    fun `GIVEN UpdateTextField Description WHEN reduce THEN update description`() {
        // Given
        val hiker = Random.nextHikerUI()
        val state = HikerEditState.Initialized.NotEdited(hiker = hiker)
        val field = HikerProfileEditTextField.Description
        val newValue = Random.nextString()
        val action = HikerProfileEditAction.UpdateTextField(field = field, value = newValue)

        // When
        val newState = state.reduce(action = action)

        // Then
        val expectedState = HikerEditState.Initialized.Edited(
            hiker = hiker.copy(description = newValue),
            oldPhotoUrl = null,
            newPhotoUri = null,
            errorFields = emptyList(),
        )
        assertEquals(expectedState, newState)
    }

    @Test
    fun `GIVEN UpdateTextField with error value WHEN reduce THEN update errorFields`() {
        // Given
        val hiker = Random.nextHikerUI()
        val state = HikerEditState.Initialized.NotEdited(hiker = hiker)
        val field = HikerProfileEditTextField.entries.filter { it.isEmptyFieldAllowed.not() }.random()
        val newValue = " "
        val action = HikerProfileEditAction.UpdateTextField(field = field, value = newValue)

        // When
        val newState = state.reduce(action = action)

        // Then
        val expectedErrorFields = listOf(field)
        assertEquals(expectedErrorFields, (newState as? HikerEditState.Initialized.Edited)?.errorFields)
    }

    @Test
    fun `GIVEN UpdateTextField with valid value WHEN reduce THEN update errorFields`() {
        // Given
        val hiker = Random.nextHikerUI()
        val state = HikerEditState.Initialized.Edited(
            hiker = hiker,
            oldPhotoUrl = null,
            newPhotoUri = null,
            errorFields = HikerProfileEditTextField.entries.toList(),
        )
        val field = HikerProfileEditTextField.entries.filter { it.isEmptyFieldAllowed.not() }.random()
        val newValue = Random.nextString()
        val action = HikerProfileEditAction.UpdateTextField(field = field, value = newValue)

        // When
        val newState = state.reduce(action = action)

        // Then
        val expectedErrorFields = state.errorFields.minus(field)
        assertEquals(expectedErrorFields, (newState as? HikerEditState.Initialized.Edited)?.errorFields)
    }

    @Test
    fun `GIVEN UpdatePhoto WHEN reduce THEN update oldPhotoUrl and newPhotoUri`() {
        // Given
        val hiker = Random.nextHikerUI()
        val state = HikerEditState.Initialized.NotEdited(hiker = hiker)
        val newPhotoUri = Random.nextUrlString()
        val action = HikerProfileEditAction.UpdatePhoto(uri = newPhotoUri)

        // When
        val newState = state.reduce(action = action)

        // Then
        val expectedState = HikerEditState.Initialized.Edited(
            hiker = hiker,
            oldPhotoUrl = hiker.photoUrl,
            newPhotoUri = newPhotoUri,
            errorFields = emptyList(),
        )
        assertEquals(expectedState, newState)
    }

    @Test
    fun `GIVEN UpdateBirthday WHEN reduce THEN update hiker birthday`() {
        // Given
        val hiker = Random.nextHikerUI()
        val state = HikerEditState.Initialized.NotEdited(hiker = hiker)
        val newBirthday = Random.nextLocalDate()
        val action = HikerProfileEditAction.UpdateBirthday(date = newBirthday)

        // When
        val newState = state.reduce(action = action)

        // Then
        val expectedState = HikerEditState.Initialized.Edited(
            hiker = hiker.copy(birthday = newBirthday),
            oldPhotoUrl = null,
            newPhotoUri = null,
            errorFields = emptyList(),
        )
        assertEquals(expectedState, newState)
    }

    @Test
    fun `GIVEN UpdateHome WHEN reduce THEN update hiker home`() {
        // Given
        val hiker = Random.nextHikerUI()
        val state = HikerEditState.Initialized.NotEdited(hiker = hiker)
        val newHome = Random.nextLocation()
        val action = HikerProfileEditAction.UpdateHome(location = newHome)

        // When
        val newState = state.reduce(action = action)

        // Then
        val expectedState = HikerEditState.Initialized.Edited(
            hiker = hiker.copy(home = newHome),
            oldPhotoUrl = null,
            newPhotoUri = null,
            errorFields = emptyList(),
        )
        assertEquals(expectedState, newState)
    }
}