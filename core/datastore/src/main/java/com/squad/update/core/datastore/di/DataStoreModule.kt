package com.squad.update.core.datastore.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import com.squad.update.core.common.network.Dispatcher
import com.squad.update.core.common.network.UpdateDispatchers
import com.squad.update.core.common.network.di.ApplicationScope
import com.squad.update.core.datastore.IntToStringIdsMigration
import com.squad.update.core.datastore.UserPreferences
import com.squad.update.core.datastore.UserPreferencesSerializer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton

@Module
@InstallIn( SingletonComponent::class )
object DataStoreModule {

    @Provides
    @Singleton
    internal fun providesUserPreferencesDataStore(
        @ApplicationContext context: Context,
        @Dispatcher( UpdateDispatchers.IO ) ioDispatcher: CoroutineDispatcher,
        @ApplicationScope scope: CoroutineScope,
        userPreferencesSerializer: UserPreferencesSerializer
    ): DataStore<UserPreferences> =
        DataStoreFactory.create(
            serializer = userPreferencesSerializer,
            scope = CoroutineScope( scope.coroutineContext + ioDispatcher ),
            migrations = listOf(
                IntToStringIdsMigration
            ),
        ) {
            context.dataStoreFile( "user_preferences.pb" )
        }
}