package com.example.hector25.user_interface.property

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hector25.data.PropertyRepository
import com.example.hector25.user_interface.dashBoard.PropertyUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PropertyDetailViewModel(
    private val repository: PropertyRepository
) : ViewModel() {

    private val _state = MutableStateFlow<PropertyUiState>(PropertyUiState.Loading)
    val state: StateFlow<PropertyUiState> = _state

    fun loadProperty(mlsId: Int) {
        viewModelScope.launch {
            _state.value = PropertyUiState.Loading
            try {
                val property = repository.getPropertyById(mlsId)
                _state.value = PropertyUiState.Success(listOf(property))
            } catch (e: Exception) {
                _state.value = PropertyUiState.Error(e.message ?: "Failed to load property")
            }
        }
    }
}