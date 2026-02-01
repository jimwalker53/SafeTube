package com.safetube.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.safetube.data.local.database.dao.BlockedChannelDao
import com.safetube.data.local.database.dao.BlockedKeywordDao
import com.safetube.data.local.database.dao.BlockedTermDao
import com.safetube.data.local.database.entities.BlockedChannelEntity
import com.safetube.data.local.database.entities.BlockedKeywordEntity
import com.safetube.data.local.database.entities.BlockedTermEntity

@Database(
    entities = [
        BlockedTermEntity::class,
        BlockedKeywordEntity::class,
        BlockedChannelEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class SafeTubeDatabase : RoomDatabase() {

    abstract fun blockedTermDao(): BlockedTermDao

    abstract fun blockedKeywordDao(): BlockedKeywordDao

    abstract fun blockedChannelDao(): BlockedChannelDao
}
