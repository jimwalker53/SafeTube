package com.safetube.di

import android.content.Context
import androidx.room.Room
import com.safetube.data.local.database.SafeTubeDatabase
import com.safetube.data.local.database.dao.BlockedChannelDao
import com.safetube.data.local.database.dao.BlockedKeywordDao
import com.safetube.data.local.database.dao.BlockedTermDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): SafeTubeDatabase {
        return Room.databaseBuilder(
            context,
            SafeTubeDatabase::class.java,
            "safetube_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideBlockedTermDao(database: SafeTubeDatabase): BlockedTermDao {
        return database.blockedTermDao()
    }

    @Provides
    @Singleton
    fun provideBlockedKeywordDao(database: SafeTubeDatabase): BlockedKeywordDao {
        return database.blockedKeywordDao()
    }

    @Provides
    @Singleton
    fun provideBlockedChannelDao(database: SafeTubeDatabase): BlockedChannelDao {
        return database.blockedChannelDao()
    }
}
