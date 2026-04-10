package com.example.hector25.user_interface.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hector25.data.PropertyRepository
import com.example.hector25.user_interface.dashBoard.PropertyUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SearchViewModel(
    private val repository: PropertyRepository
) : ViewModel() {

    private val _state = MutableStateFlow<PropertyUiState>(PropertyUiState.Success(emptyList()))
    val state: StateFlow<PropertyUiState> = _state

    fun search(minBeds: Int? = null, minPrice: Int? = null, maxPrice: Int? = null) {
        viewModelScope.launch {
            _state.value = PropertyUiState.Loading
            try {
                val results = repository.searchProperties(minBeds, minPrice, maxPrice)
                _state.value = PropertyUiState.Success(results)
            } catch (e: Exception) {
                _state.value = PropertyUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    // Load all on open
    fun loadAll() {
        viewModelScope.launch {
            _state.value = PropertyUiState.Loading
            try {
                _state.value = PropertyUiState.Success(repository.getAllProperties())
            } catch (e: Exception) {
                _state.value = PropertyUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}