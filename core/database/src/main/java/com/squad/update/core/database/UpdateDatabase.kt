package com.squad.update.core.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.squad.update.core.database.dao.NewsResourceDao
import com.squad.update.core.database.dao.NewsResourceFtsDao
import com.squad.update.core.database.dao.TopicDao
import com.squad.update.core.database.dao.TopicFtsDao
import com.squad.update.core.database.model.NewsResourceEntity
import com.squad.update.core.database.model.NewsResourceFtsEntity
import com.squad.update.core.database.model.NewsResourceTopicCrossRef
import com.squad.update.core.database.model.TopicEntity
import com.squad.update.core.database.model.TopicFtsEntity
import com.squad.update.core.database.util.InstantConverter

@Database(
    entities = [
        TopicEntity::class,
        TopicFtsEntity::class,
        NewsResourceEntity::class,
        NewsResourceTopicCrossRef::class,
        NewsResourceFtsEntity::class
    ],
    version = 2,
    autoMigrations = [
        AutoMigration( from = 1, to = 2 )
    ],
    exportSchema = true,
)
@TypeConverters( InstantConverter::class )
internal abstract class UpdateDatabase : RoomDatabase() {
    abstract fun topicDao(): TopicDao
    abstract fun topicFtsDao(): TopicFtsDao
    abstract fun newsResourceDao(): NewsResourceDao
    abstract fun newsResourceFtsDao(): NewsResourceFtsDao
}