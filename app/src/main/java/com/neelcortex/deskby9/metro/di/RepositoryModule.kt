package com.neelcortex.deskby9.metro.di

import com.neelcortex.deskby9.metro.data.repository.MetroRepositoryImpl
import com.neelcortex.deskby9.metro.domain.repository.MetroRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for providing repository implementations
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    
    @Binds
    @Singleton
    abstract fun bindMetroRepository(
        metroRepositoryImpl: MetroRepositoryImpl
    ): MetroRepository
}
