package com.sidiq.sibi.di

import com.sidiq.sibi.data.UserRepository
import com.sidiq.sibi.data.source.FirebaseService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideUserRepository(service: FirebaseService) : UserRepository =
        UserRepository(service)


}