package com.safetube.data.local.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.safetube.data.local.database.entities.BlockedTermEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BlockedTermDao {

    @Query("SELECT * FROM blocked_terms ORDER BY createdAt DESC")
    fun getAllBlockedTerms(): Flow<List<BlockedTermEntity>>

    @Query("SELECT * FROM blocked_terms WHERE isEnabled = 1")
    suspend fun getActiveBlockedTerms(): List<BlockedTermEntity>

    @Query("SELECT * FROM blocked_terms WHERE id = :id")
    suspend fun getBlockedTermById(id: Long): BlockedTermEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBlockedTerm(term: BlockedTermEntity): Long

    @Update
    suspend fun updateBlockedTerm(term: BlockedTermEntity)

    @Delete
    suspend fun deleteBlockedTerm(term: BlockedTermEntity)

    @Query("DELETE FROM blocked_terms WHERE id = :id")
    suspend fun deleteBlockedTermById(id: Long)

    @Query("UPDATE blocked_terms SET isEnabled = :isEnabled WHERE id = :id")
    suspend fun setBlockedTermEnabled(id: Long, isEnabled: Boolean)

    @Query("SELECT COUNT(*) FROM blocked_terms")
    suspend fun getBlockedTermCount(): Int
}
