package com.squad.update.core.data.di

import com.squad.update.core.data.repository.NewsRepository
import com.squad.update.core.data.repository.UserDataRepository
import com.squad.update.core.data.repository.impl.OfflineFirstNewsRepository
import com.squad.update.core.data.repository.impl.OfflineFirstUserDataRepository
import com.squad.update.core.data.util.NetworkMonitor
import com.squad.update.core.data.util.TimeZoneMonitor
import com.squad.update.core.data.util.impl.ConnectivityManagerNetworkMonitor
import com.squad.update.core.data.util.impl.TimeZoneMonitorImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn( SingletonComponent::class )
abstract class DataModule {

    @Binds
    internal abstract fun bindsUserDataRepository(
        userDataRepository: OfflineFirstUserDataRepository
    ): UserDataRepository

    @Binds
    internal abstract fun bindsNetworkMonitor(
        networkMonitor: ConnectivityManagerNetworkMonitor
    ): NetworkMonitor

    @Binds
    internal abstract fun binds( impl: TimeZoneMonitorImpl ): TimeZoneMonitor

    @Binds
    internal abstract fun bindsNewsResourceRepository(
        newsRepository: OfflineFirstNewsRepository
    ): NewsRepository
}