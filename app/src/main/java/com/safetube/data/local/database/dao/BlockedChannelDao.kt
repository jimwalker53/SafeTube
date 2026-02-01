package com.safetube.data.local.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.safetube.data.local.database.entities.BlockedChannelEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BlockedChannelDao {

    @Query("SELECT * FROM blocked_channels ORDER BY createdAt DESC")
    fun getAllBlockedChannels(): Flow<List<BlockedChannelEntity>>

    @Query("SELECT * FROM blocked_channels WHERE isEnabled = 1")
    suspend fun getActiveBlockedChannels(): List<BlockedChannelEntity>

    @Query("SELECT * FROM blocked_channels WHERE id = :id")
    suspend fun getBlockedChannelById(id: Long): BlockedChannelEntity?

    @Query("SELECT * FROM blocked_channels WHERE channelId = :channelId")
    suspend fun getBlockedChannelByChannelId(channelId: String): BlockedChannelEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBlockedChannel(channel: BlockedChannelEntity): Long

    @Update
    suspend fun updateBlockedChannel(channel: BlockedChannelEntity)

    @Delete
    suspend fun deleteBlockedChannel(channel: BlockedChannelEntity)

    @Query("DELETE FROM blocked_channels WHERE id = :id")
    suspend fun deleteBlockedChannelById(id: Long)

    @Query("DELETE FROM blocked_channels WHERE channelId = :channelId")
    suspend fun deleteBlockedChannelByChannelId(channelId: String)

    @Query("UPDATE blocked_channels SET isEnabled = :isEnabled WHERE id = :id")
    suspend fun setBlockedChannelEnabled(id: Long, isEnabled: Boolean)

    @Query("SELECT EXISTS(SELECT 1 FROM blocked_channels WHERE channelId = :channelId AND isEnabled = 1)")
    suspend fun isChannelBlocked(channelId: String): Boolean

    @Query("SELECT COUNT(*) FROM blocked_channels")
    suspend fun getBlockedChannelCount(): Int
}
