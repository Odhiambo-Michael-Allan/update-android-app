package com.squad.update.core.data.di

import com.squad.update.core.data.repository.UserNewsResourceRepository
import com.squad.update.core.data.repository.impl.CompositeUserNewsResourceRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn( SingletonComponent::class )
interface UserNewsResourceRepositoryModule {
    @Binds
    fun bindsUserNewsResourceRepository(
        userDataRepository: CompositeUserNewsResourceRepository
    ): UserNewsResourceRepository
}