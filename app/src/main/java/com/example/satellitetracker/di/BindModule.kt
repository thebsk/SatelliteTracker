package com.example.satellitetracker.di

import com.example.satellitetracker.domain.repository.SatelliteRepository
import com.example.satellitetracker.data.repository.SatelliteRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class BindModule {

    @Binds
    @Singleton
    abstract fun bindSatelliteRepository(impl: SatelliteRepositoryImpl): SatelliteRepository
}


