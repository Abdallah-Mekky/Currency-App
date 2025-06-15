package com.example.currency.di.network

import android.util.Log
import com.example.currency.data.source.network.WebService
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {

    private const val BASE_URL = "https://data.fixer.io/api/"

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        val logging = HttpLoggingInterceptor { message -> Log.e("Api", message) }
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        return logging
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient {

        return OkHttpClient.Builder()
            .connectTimeout(100, TimeUnit.SECONDS)
            .readTimeout(100, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideNetworkJson() = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }

    @Provides
    @Singleton
    fun provideRetrofit(networkJson: Json, client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(
                networkJson.asConverterFactory("application/json".toMediaType())
            )
            .build()

    @Provides
    @Singleton
    fun provideWebServices(retrofit: Retrofit): WebService {
        return retrofit.create(WebService::class.java)
    }
}