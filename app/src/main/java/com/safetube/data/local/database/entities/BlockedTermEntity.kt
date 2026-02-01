package com.safetube.data.local.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity representing a blocked search term.
 * When a user searches for a term that matches any blocked term,
 * the search will return "No videos found" instead of actual results.
 */
@Entity(tableName = "blocked_terms")
data class BlockedTermEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val term: String,
    val isEnabled: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
