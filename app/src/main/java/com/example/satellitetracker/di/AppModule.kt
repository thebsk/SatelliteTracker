package com.example.satellitetracker.di

import android.content.Context
import androidx.room.Room
import com.example.satellitetracker.core.domain.repository.SatelliteRepository
import com.example.satellitetracker.core.use_cases.GetSatellitesUseCase
import com.example.satellitetracker.data.local.AppDatabase
import com.example.satellitetracker.data.local.SatelliteDao
import com.example.satellitetracker.core.domain.repository.SatelliteRepositoryImpl
import com.example.satellitetracker.di.dispatchers.DispatcherProvider
import com.example.satellitetracker.di.dispatchers.DispatcherProviderImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideJson(): Json {
        return Json { ignoreUnknownKeys = true }
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context = context,
            klass = AppDatabase::class.java,
            name = "satellite_tracker.db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideSatelliteDao(appDatabase: AppDatabase): SatelliteDao {
        return appDatabase.satelliteDao()
    }

    @Provides
    @Singleton
    fun provideDispatcherProvider(): DispatcherProvider {
        return DispatcherProviderImpl()
    }

    @Provides
    @Singleton
    fun provideSatelliteRepository(
        @ApplicationContext context: Context,
        json: Json
    ): SatelliteRepository {
        return SatelliteRepositoryImpl(context, json)
    }

    @Provides
    @Singleton
    fun provideGetSatellitesUseCase(repository: SatelliteRepository): GetSatellitesUseCase {
        return GetSatellitesUseCase(repository)
    }
}
