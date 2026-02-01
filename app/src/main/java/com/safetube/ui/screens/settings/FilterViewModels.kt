package com.safetube.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.safetube.data.local.database.entities.MatchType
import com.safetube.domain.model.BlockedChannel
import com.safetube.domain.model.BlockedKeyword
import com.safetube.domain.model.BlockedTerm
import com.safetube.domain.usecase.ManageFiltersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// Blocked Terms ViewModel
@HiltViewModel
class BlockedTermsViewModel @Inject constructor(
    private val manageFiltersUseCase: ManageFiltersUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(BlockedTermsUiState())
    val uiState: StateFlow<BlockedTermsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            manageFiltersUseCase.getAllBlockedTerms().collect { terms ->
                _uiState.value = _uiState.value.copy(blockedTerms = terms)
            }
        }
    }

    fun addTerm(term: String) {
        viewModelScope.launch {
            manageFiltersUseCase.addBlockedTerm(term)
        }
    }

    fun toggleTerm(id: Long, isEnabled: Boolean) {
        viewModelScope.launch {
            manageFiltersUseCase.setBlockedTermEnabled(id, isEnabled)
        }
    }

    fun deleteTerm(id: Long) {
        viewModelScope.launch {
            manageFiltersUseCase.deleteBlockedTerm(id)
        }
    }
}

data class BlockedTermsUiState(
    val blockedTerms: List<BlockedTerm> = emptyList()
)

// Blocked Keywords ViewModel
@HiltViewModel
class BlockedKeywordsViewModel @Inject constructor(
    private val manageFiltersUseCase: ManageFiltersUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(BlockedKeywordsUiState())
    val uiState: StateFlow<BlockedKeywordsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            manageFiltersUseCase.getAllBlockedKeywords().collect { keywords ->
                _uiState.value = _uiState.value.copy(blockedKeywords = keywords)
            }
        }
    }

    fun addKeyword(keyword: String, matchType: MatchType) {
        viewModelScope.launch {
            manageFiltersUseCase.addBlockedKeyword(keyword, matchType)
        }
    }

    fun toggleKeyword(id: Long, isEnabled: Boolean) {
        viewModelScope.launch {
            manageFiltersUseCase.setBlockedKeywordEnabled(id, isEnabled)
        }
    }

    fun deleteKeyword(id: Long) {
        viewModelScope.launch {
            manageFiltersUseCase.deleteBlockedKeyword(id)
        }
    }
}

data class BlockedKeywordsUiState(
    val blockedKeywords: List<BlockedKeyword> = emptyList()
)

// Blocked Channels ViewModel
@HiltViewModel
class BlockedChannelsViewModel @Inject constructor(
    private val manageFiltersUseCase: ManageFiltersUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(BlockedChannelsUiState())
    val uiState: StateFlow<BlockedChannelsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            manageFiltersUseCase.getAllBlockedChannels().collect { channels ->
                _uiState.value = _uiState.value.copy(blockedChannels = channels)
            }
        }
    }

    fun toggleChannel(id: Long, isEnabled: Boolean) {
        viewModelScope.launch {
            manageFiltersUseCase.setBlockedChannelEnabled(id, isEnabled)
        }
    }

    fun deleteChannel(id: Long) {
        viewModelScope.launch {
            manageFiltersUseCase.deleteBlockedChannel(id)
        }
    }
}

data class BlockedChannelsUiState(
    val blockedChannels: List<BlockedChannel> = emptyList()
)
