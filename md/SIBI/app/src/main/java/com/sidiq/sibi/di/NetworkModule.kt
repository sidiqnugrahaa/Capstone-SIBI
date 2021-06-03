package com.sidiq.sibi.di

import com.sidiq.sibi.data.source.FunctionService
import com.sidiq.sibi.data.source.function.FunctionAPI
import com.sidiq.sibi.utils.BASE_URL_FUNCTION
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideApiService(): FunctionAPI {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL_FUNCTION)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(FunctionAPI::class.java)
    }

    @Provides
    @Singleton
    fun provideFunctionService(functionAPI: FunctionAPI) : FunctionService =
        FunctionService(functionAPI)

}