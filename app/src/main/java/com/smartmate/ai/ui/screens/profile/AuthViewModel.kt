package com.smartmate.ai.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.smartmate.ai.data.AppContainer
import com.smartmate.ai.data.auth.AppUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val user: AppUser? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isCloudAuthAvailable: Boolean = false
)

class AuthViewModel(private val container: AppContainer) : ViewModel() {

    private val _uiState = MutableStateFlow(
        AuthUiState(
            user = container.authRepository.currentUser(),
            isCloudAuthAvailable = container.authRepository.isCloudAuthAvailable
        )
    )
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun signUp(name: String, email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Email and password are required")
            return
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            container.authRepository.signUp(name, email, password)
                .onSuccess { user -> _uiState.value = _uiState.value.copy(user = user, isLoading = false) }
                .onFailure { e -> _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = e.message) }
        }
    }

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Email and password are required")
            return
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            container.authRepository.login(email, password)
                .onSuccess { user -> _uiState.value = _uiState.value.copy(user = user, isLoading = false) }
                .onFailure { e -> _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = e.message) }
        }
    }

    fun loginWithGoogle(idToken: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            container.authRepository.loginWithGoogleIdToken(idToken)
                .onSuccess { user -> _uiState.value = _uiState.value.copy(user = user, isLoading = false) }
                .onFailure { e -> _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = e.message) }
        }
    }

    fun logout() {
        container.authRepository.logout()
        _uiState.value = _uiState.value.copy(user = null)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    companion object {
        fun factory(container: AppContainer) = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T = AuthViewModel(container) as T
        }
    }
}
