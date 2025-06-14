package com.example.currency.di.local.db

import android.content.Context
import androidx.room.Room
import com.example.currency.data.source.local.CurrenciesDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object DatabaseModule {
    @Provides
    @Singleton
    fun providesCurrenciesDatabase(
        @ApplicationContext context: Context,
    ): CurrenciesDatabase = Room.databaseBuilder(
        context,
        CurrenciesDatabase::class.java,
        "currencies-database",
    ).build()
}
