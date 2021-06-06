package com.sidiq.sibi.di

import com.sidiq.sibi.data.FunctionRepository
import com.sidiq.sibi.data.LeaderboardRepository
import com.sidiq.sibi.data.UserRepository
import com.sidiq.sibi.data.source.FirebaseService
import com.sidiq.sibi.data.source.FunctionService
import com.sidiq.sibi.data.source.LeaderboardService
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

    @Provides
    @Singleton
    fun provideLeaderboardRepository(service: LeaderboardService) : LeaderboardRepository =
        LeaderboardRepository(service)

    @Provides
    @Singleton
    fun provideFunctionRepository(service: FunctionService) : FunctionRepository =
        FunctionRepository(service)

}