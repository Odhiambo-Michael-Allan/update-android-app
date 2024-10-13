package com.squad.update.core.network.di

import com.squad.update.core.network.UpdateNetworkDataSource
import com.squad.update.core.network.demo.DemoUpdateNetworkDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn( SingletonComponent::class )
interface FlavoredNetworkModule {

    @Binds
    fun binds( impl: DemoUpdateNetworkDataSource ): UpdateNetworkDataSource
}