package com.squad.update.core.common.network.di

import com.squad.update.core.common.network.Dispatcher
import com.squad.update.core.common.network.UpdateDispatchers
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

@Module
@InstallIn( SingletonComponent::class )
object DispatchersModule {

    @Provides
    @Dispatcher( UpdateDispatchers.IO )
    fun providesIODispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    @Dispatcher( UpdateDispatchers.Default )
    fun providesDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default

}