package com.squad.update.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.squad.update.core.database.dao.TopicDao
import com.squad.update.core.database.dao.TopicFtsDao
import com.squad.update.core.database.model.TopicEntity
import com.squad.update.core.database.model.TopicFtsEntity
import com.squad.update.core.database.util.InstantConverter

@Database(
    entities = [
        TopicEntity::class,
        TopicFtsEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
@TypeConverters( InstantConverter::class )
internal abstract class UpdateDatabase : RoomDatabase() {
    abstract fun topicDao(): TopicDao
    abstract fun topicFtsDao(): TopicFtsDao
}