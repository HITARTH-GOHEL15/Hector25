package com.example.hector25.user_interface.dashBoard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hector25.data.PropertyRepository
import com.example.hector25.data.SimplyRetsProperty
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class PropertyUiState {
    object Loading : PropertyUiState()
    data class Success(val properties: List<SimplyRetsProperty>) : PropertyUiState()
    data class Error(val message: String) : PropertyUiState()
}

class DashboardViewModel(
    private val repository: PropertyRepository
) : ViewModel() {

    private val _buyState = MutableStateFlow<PropertyUiState>(PropertyUiState.Loading)
    val buyState: StateFlow<PropertyUiState> = _buyState

    private val _rentState = MutableStateFlow<PropertyUiState>(PropertyUiState.Loading)
    val rentState: StateFlow<PropertyUiState> = _rentState

    init {
        loadProperties()
    }

    fun loadProperties() {
        viewModelScope.launch {
            _buyState.value = PropertyUiState.Loading
            try {
                _buyState.value = PropertyUiState.Success(repository.getBuyProperties())
            } catch (e: Exception) {
                _buyState.value = PropertyUiState.Error(e.message ?: "Unknown error")
            }
        }

        viewModelScope.launch {
            _rentState.value = PropertyUiState.Loading
            try {
                _rentState.value = PropertyUiState.Success(repository.getRentProperties())
            } catch (e: Exception) {
                _rentState.value = PropertyUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}