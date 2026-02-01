package com.safetube.data.local.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.safetube.data.local.database.entities.BlockedKeywordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BlockedKeywordDao {

    @Query("SELECT * FROM blocked_keywords ORDER BY createdAt DESC")
    fun getAllBlockedKeywords(): Flow<List<BlockedKeywordEntity>>

    @Query("SELECT * FROM blocked_keywords WHERE isEnabled = 1")
    suspend fun getActiveBlockedKeywords(): List<BlockedKeywordEntity>

    @Query("SELECT * FROM blocked_keywords WHERE id = :id")
    suspend fun getBlockedKeywordById(id: Long): BlockedKeywordEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBlockedKeyword(keyword: BlockedKeywordEntity): Long

    @Update
    suspend fun updateBlockedKeyword(keyword: BlockedKeywordEntity)

    @Delete
    suspend fun deleteBlockedKeyword(keyword: BlockedKeywordEntity)

    @Query("DELETE FROM blocked_keywords WHERE id = :id")
    suspend fun deleteBlockedKeywordById(id: Long)

    @Query("UPDATE blocked_keywords SET isEnabled = :isEnabled WHERE id = :id")
    suspend fun setBlockedKeywordEnabled(id: Long, isEnabled: Boolean)

    @Query("SELECT COUNT(*) FROM blocked_keywords")
    suspend fun getBlockedKeywordCount(): Int
}
