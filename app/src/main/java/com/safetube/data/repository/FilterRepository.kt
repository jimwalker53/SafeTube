package com.safetube.data.repository

import com.safetube.data.local.database.dao.BlockedChannelDao
import com.safetube.data.local.database.dao.BlockedKeywordDao
import com.safetube.data.local.database.dao.BlockedTermDao
import com.safetube.data.local.database.entities.BlockedChannelEntity
import com.safetube.data.local.database.entities.BlockedKeywordEntity
import com.safetube.data.local.database.entities.BlockedTermEntity
import com.safetube.data.local.database.entities.MatchType
import com.safetube.domain.model.BlockedChannel
import com.safetube.domain.model.BlockedKeyword
import com.safetube.domain.model.BlockedTerm
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FilterRepository @Inject constructor(
    private val blockedTermDao: BlockedTermDao,
    private val blockedKeywordDao: BlockedKeywordDao,
    private val blockedChannelDao: BlockedChannelDao
) {

    // Blocked Terms
    fun getAllBlockedTerms(): Flow<List<BlockedTerm>> {
        return blockedTermDao.getAllBlockedTerms().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    suspend fun getActiveBlockedTerms(): List<BlockedTerm> {
        return blockedTermDao.getActiveBlockedTerms().map { it.toDomain() }
    }

    suspend fun addBlockedTerm(term: String): Long {
        return blockedTermDao.insertBlockedTerm(
            BlockedTermEntity(term = term)
        )
    }

    suspend fun updateBlockedTerm(blockedTerm: BlockedTerm) {
        blockedTermDao.updateBlockedTerm(blockedTerm.toEntity())
    }

    suspend fun deleteBlockedTerm(id: Long) {
        blockedTermDao.deleteBlockedTermById(id)
    }

    suspend fun setBlockedTermEnabled(id: Long, isEnabled: Boolean) {
        blockedTermDao.setBlockedTermEnabled(id, isEnabled)
    }

    // Blocked Keywords
    fun getAllBlockedKeywords(): Flow<List<BlockedKeyword>> {
        return blockedKeywordDao.getAllBlockedKeywords().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    suspend fun getActiveBlockedKeywords(): List<BlockedKeyword> {
        return blockedKeywordDao.getActiveBlockedKeywords().map { it.toDomain() }
    }

    suspend fun addBlockedKeyword(keyword: String, matchType: MatchType): Long {
        return blockedKeywordDao.insertBlockedKeyword(
            BlockedKeywordEntity(
                keyword = keyword,
                matchType = matchType.name
            )
        )
    }

    suspend fun updateBlockedKeyword(blockedKeyword: BlockedKeyword) {
        blockedKeywordDao.updateBlockedKeyword(blockedKeyword.toEntity())
    }

    suspend fun deleteBlockedKeyword(id: Long) {
        blockedKeywordDao.deleteBlockedKeywordById(id)
    }

    suspend fun setBlockedKeywordEnabled(id: Long, isEnabled: Boolean) {
        blockedKeywordDao.setBlockedKeywordEnabled(id, isEnabled)
    }

    // Blocked Channels
    fun getAllBlockedChannels(): Flow<List<BlockedChannel>> {
        return blockedChannelDao.getAllBlockedChannels().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    suspend fun getActiveBlockedChannels(): List<BlockedChannel> {
        return blockedChannelDao.getActiveBlockedChannels().map { it.toDomain() }
    }

    suspend fun addBlockedChannel(
        channelId: String,
        channelName: String,
        channelThumbnail: String? = null
    ): Long {
        return blockedChannelDao.insertBlockedChannel(
            BlockedChannelEntity(
                channelId = channelId,
                channelName = channelName,
                channelThumbnail = channelThumbnail
            )
        )
    }

    suspend fun deleteBlockedChannel(id: Long) {
        blockedChannelDao.deleteBlockedChannelById(id)
    }

    suspend fun deleteBlockedChannelByChannelId(channelId: String) {
        blockedChannelDao.deleteBlockedChannelByChannelId(channelId)
    }

    suspend fun setBlockedChannelEnabled(id: Long, isEnabled: Boolean) {
        blockedChannelDao.setBlockedChannelEnabled(id, isEnabled)
    }

    suspend fun isChannelBlocked(channelId: String): Boolean {
        return blockedChannelDao.isChannelBlocked(channelId)
    }

    // Mapper extensions
    private fun BlockedTermEntity.toDomain() = BlockedTerm(
        id = id,
        term = term,
        isEnabled = isEnabled,
        createdAt = createdAt
    )

    private fun BlockedTerm.toEntity() = BlockedTermEntity(
        id = id,
        term = term,
        isEnabled = isEnabled,
        createdAt = createdAt
    )

    private fun BlockedKeywordEntity.toDomain() = BlockedKeyword(
        id = id,
        keyword = keyword,
        matchType = try {
            MatchType.valueOf(matchType)
        } catch (e: Exception) {
            MatchType.CONTAINS
        },
        isEnabled = isEnabled,
        createdAt = createdAt
    )

    private fun BlockedKeyword.toEntity() = BlockedKeywordEntity(
        id = id,
        keyword = keyword,
        matchType = matchType.name,
        isEnabled = isEnabled,
        createdAt = createdAt
    )

    private fun BlockedChannelEntity.toDomain() = BlockedChannel(
        id = id,
        channelId = channelId,
        channelName = channelName,
        channelThumbnail = channelThumbnail,
        isEnabled = isEnabled,
        createdAt = createdAt
    )
}
