package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.AppConfigEntity
import com.example.data.EarningRepository
import com.example.data.SessionState
import com.example.data.WorkerEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface AuthUiState {
    data object Idle : AuthUiState
    data object Loading : AuthUiState
    data class Error(val message: String) : AuthUiState
    data class Success(val message: String) : AuthUiState
}

@OptIn(ExperimentalCoroutinesApi::class)
class PortalViewModel(private val repository: EarningRepository) : ViewModel() {

    val config: StateFlow<AppConfigEntity> = repository.configFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppConfigEntity())

    val session: StateFlow<SessionState> = repository.sessionFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SessionState())

    val activeWorker: StateFlow<WorkerEntity?> = session
        .flatMapLatest { state ->
            val id = state.activeWorkerId
            if (id != null) repository.observeWorker(id) else flowOf(null)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _authState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val authState: StateFlow<AuthUiState> = _authState.asStateFlow()

    fun clearAuthState() {
        _authState.value = AuthUiState.Idle
    }

    fun setupAdmin(
        name: String,
        phone: String,
        password: String,
        whatsappNumber: String,
        facebookUrl: String
    ) {
        viewModelScope.launch {
            _authState.value = AuthUiState.Loading
            repository.setupAdmin(name, phone, password, whatsappNumber, facebookUrl)
                .onSuccess {
                    _authState.value = AuthUiState.Success("অ্যাডমিন প্যানেল সফলভাবে সেটআপ হয়েছে")
                }
                .onFailure {
                    _authState.value = AuthUiState.Error(it.message ?: "সেটআপ ব্যর্থ হয়েছে")
                }
        }
    }

    fun loginAdmin(phone: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthUiState.Loading
            repository.loginAdmin(phone, password)
                .onSuccess {
                    _authState.value = AuthUiState.Success("অ্যাডমিন লগইন সফল হয়েছে")
                }
                .onFailure {
                    _authState.value = AuthUiState.Error(it.message ?: "লগইন ব্যর্থ হয়েছে")
                }
        }
    }

    fun logoutAdmin() {
        viewModelScope.launch {
            repository.logoutAdmin()
            _authState.value = AuthUiState.Idle
        }
    }

    fun registerWorker(
        name: String,
        phone: String,
        password: String,
        referralCode: String
    ) {
        viewModelScope.launch {
            _authState.value = AuthUiState.Loading
            repository.registerWorker(name, phone, password, referralCode)
                .onSuccess {
                    _authState.value = AuthUiState.Success("রেজিস্ট্রেশন সম্পন্ন! অ্যাডমিন আনলক করলে কাজ শুরু হবে।")
                }
                .onFailure {
                    _authState.value = AuthUiState.Error(it.message ?: "রেজিস্ট্রেশন ব্যর্থ হয়েছে")
                }
        }
    }

    fun loginWorker(phone: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthUiState.Loading
            repository.loginWorker(phone, password)
                .onSuccess {
                    _authState.value = AuthUiState.Success("লগইন সফল হয়েছে")
                }
                .onFailure {
                    _authState.value = AuthUiState.Error(it.message ?: "লগইন ব্যর্থ হয়েছে")
                }
        }
    }

    fun logoutWorker() {
        viewModelScope.launch {
            repository.logoutWorker()
            _authState.value = AuthUiState.Idle
        }
    }

    class Factory(private val repository: EarningRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return PortalViewModel(repository) as T
        }
    }
}
