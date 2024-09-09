package com.amorgens.di

import com.amorgens.BuildConfig
import com.amorgens.general.data.APIService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)   // Set connection timeout
            .readTimeout(60, TimeUnit.SECONDS)      // Set read timeout
            .writeTimeout(60, TimeUnit.SECONDS)     // Set write timeout
            .build()

        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_URL+"/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): APIService {
        return retrofit.create(APIService::class.java)
    }


}