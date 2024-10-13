package com.squad.update.core.database.di

import android.content.Context
import androidx.room.Room
import com.squad.update.core.database.UpdateDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn( SingletonComponent::class )
internal object DatabaseModule {

    @Provides
    @Singleton
    fun providesUpdateDatabase(
        @ApplicationContext context: Context,
    ): UpdateDatabase = Room.databaseBuilder(
        context,
        UpdateDatabase::class.java,
        "update-database"
    ).build()
}