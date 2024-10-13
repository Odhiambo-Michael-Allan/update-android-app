package com.squad.update.core.database.di

import com.squad.update.core.database.UpdateDatabase
import com.squad.update.core.database.dao.TopicDao
import com.squad.update.core.database.dao.TopicFtsDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn( SingletonComponent::class )
internal object DaosModule {

    @Provides
    fun providesTopicsDao(
        database: UpdateDatabase
    ): TopicDao = database.topicDao()

    @Provides
    fun providesTopicFtsDao(
        database: UpdateDatabase
    ): TopicFtsDao = database.topicFtsDao()
}