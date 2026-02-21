package com.example.hector25.user_interface.Auth

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class LogUpViewModel : ViewModel() {
    private val _state = MutableStateFlow<LogUpState>(LogUpState.Nothing)
    val state = _state.asStateFlow()

    fun logUp(email: String,name: String,password: String) {
        _state.value = LogUpState.Loading
        //Firebase LogUp
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email , password)
            .addOnCompleteListener { task ->
                if(task.isSuccessful) {
                    task.result.user?.let {
                        it.updateProfile(
                            UserProfileChangeRequest.Builder()
                                .setDisplayName(name)
                                .build()
                        )?.addOnCompleteListener {
                            _state.value = LogUpState.Success
                        }
                        return@addOnCompleteListener
                    }
                    _state.value = LogUpState.Error
                } else {
                    _state.value = LogUpState.Error
                }
            }
    }
}

sealed class LogUpState {
    object Nothing : LogUpState()
    object Loading : LogUpState()
    object Success : LogUpState()
    object Error : LogUpState()
}
