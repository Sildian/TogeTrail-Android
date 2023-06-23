package com.sildian.apps.togetrail.dataLayer.database.trail

import com.sildian.apps.togetrail.dataLayer.database.trail.main.TrailRepository
import com.sildian.apps.togetrail.dataLayer.database.trail.main.TrailRepositoryImpl
import com.sildian.apps.togetrail.dataLayer.database.trail.trailPoint.TrailPointRepository
import com.sildian.apps.togetrail.dataLayer.database.trail.trailPoint.TrailPointRepositoryImpl
import com.sildian.apps.togetrail.dataLayer.database.trail.trailPointOfInterest.TrailPointOfInterestRepository
import com.sildian.apps.togetrail.dataLayer.database.trail.trailPointOfInterest.TrailPointOfInterestRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface TrailDatabaseModule {

    @Binds
    fun bindTrailRepository(trailRepository: TrailRepositoryImpl): TrailRepository

    @Binds
    fun bindTrailPointRepository(
        trailPointRepository: TrailPointRepositoryImpl
    ): TrailPointRepository

    @Binds
    fun bindTrailPointOfInterestRepository(
        trailPointOfInterestRepository: TrailPointOfInterestRepositoryImpl
    ): TrailPointOfInterestRepository
}