package com.example.currency.di.local.preferences

import android.content.Context
import com.example.currency.data.source.local.preferences.SharedPrefs
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SharedPrefsModule {

    @Provides
    @Singleton
    fun provideSharedPrefs(@ApplicationContext context: Context) : SharedPrefs {
        return SharedPrefs(context)
    }
}