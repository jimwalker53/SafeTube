package com.safetube.data.local.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity representing a blocked YouTube channel.
 * All videos from blocked channels will be filtered out from results.
 */
@Entity(tableName = "blocked_channels")
data class BlockedChannelEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val channelId: String,
    val channelName: String,
    val channelThumbnail: String? = null,
    val isEnabled: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
