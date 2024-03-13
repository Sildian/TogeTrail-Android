package com.sildian.apps.togetrail.common.network

import android.content.Context
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    fun provideFirebaseStorage(): FirebaseStorage = FirebaseStorage.getInstance()

    @Provides
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()
}

@Module
@InstallIn(ViewModelComponent::class)
object ViewModelScopedNetworkModule {

    @ViewModelScoped
    @Provides
    fun providePlacesClient(@ApplicationContext context: Context): PlacesClient =
        Places.createClient(context)

    @ViewModelScoped
    @Provides
    fun providePlacesAutocompleteSessionToken(): AutocompleteSessionToken =
        AutocompleteSessionToken.newInstance()
}