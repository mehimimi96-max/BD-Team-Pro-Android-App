package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.AppConfigEntity
import com.example.data.EarningRepository
import com.example.data.ReferralEntity
import com.example.data.TaskCategory
import com.example.data.TaskEntity
import com.example.data.WithdrawalEntity
import com.example.data.WorkerEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AdminViewModel(private val repository: EarningRepository) : ViewModel() {

    val config: StateFlow<AppConfigEntity> = repository.configFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppConfigEntity())

    val tasks: StateFlow<List<TaskEntity>> = repository.allTasksFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val workers: StateFlow<List<WorkerEntity>> = repository.allWorkersFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val referrals: StateFlow<List<ReferralEntity>> = repository.allReferralsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val withdrawals: StateFlow<List<WithdrawalEntity>> = repository.allWithdrawalsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _feedbackMessage = MutableStateFlow<String?>(null)
    val feedbackMessage: StateFlow<String?> = _feedbackMessage.asStateFlow()

    fun consumeFeedback() {
        _feedbackMessage.value = null
    }

    fun addTask(
        category: TaskCategory,
        title: String,
        description: String,
        linkUrl: String,
        rewardAmount: Double,
        durationSeconds: Int
    ) {
        viewModelScope.launch {
            repository.addTask(category, title, description, linkUrl, rewardAmount, durationSeconds)
                .onSuccess { _feedbackMessage.value = "নতুন কাজ সফলভাবে যোগ হয়েছে" }
                .onFailure { _feedbackMessage.value = it.message ?: "কাজ যোগ করা যায়নি" }
        }
    }

    fun toggleTaskActive(taskId: Long, isActive: Boolean) {
        viewModelScope.launch {
            repository.toggleTaskActive(taskId, isActive)
                .onFailure { _feedbackMessage.value = it.message ?: "পরিবর্তন ব্যর্থ হয়েছে" }
        }
    }

    fun deleteTask(taskId: Long) {
        viewModelScope.launch {
            repository.deleteTask(taskId)
                .onSuccess { _feedbackMessage.value = "কাজ মুছে ফেলা হয়েছে" }
                .onFailure { _feedbackMessage.value = it.message ?: "মুছে ফেলা যায়নি" }
        }
    }

    fun setWorkerUnlocked(workerId: Long, unlocked: Boolean) {
        viewModelScope.launch {
            repository.setWorkerUnlocked(workerId, unlocked)
                .onSuccess {
                    _feedbackMessage.value = if (unlocked) "কর্মীর অ্যাকাউন্ট আনলক করা হয়েছে" else "কর্মীর অ্যাকাউন্ট লক করা হয়েছে"
                }
                .onFailure { _feedbackMessage.value = it.message ?: "আপডেট ব্যর্থ হয়েছে" }
        }
    }

    fun verifyReferral(referralId: Long) {
        viewModelScope.launch {
            repository.verifyReferral(referralId)
                .onSuccess { _feedbackMessage.value = "রেফার ভেরিফায়েড ও ৳৫০ বোনাস যোগ হয়েছে" }
                .onFailure { _feedbackMessage.value = it.message ?: "ভেরিফিকেশন ব্যর্থ হয়েছে" }
        }
    }

    fun rejectReferral(referralId: Long) {
        viewModelScope.launch {
            repository.rejectReferral(referralId)
                .onSuccess { _feedbackMessage.value = "রেফার আবেদন বাতিল করা হয়েছে" }
                .onFailure { _feedbackMessage.value = it.message ?: "বাতিল করা যায়নি" }
        }
    }

    fun approveWithdrawal(withdrawalId: Long) {
        viewModelScope.launch {
            repository.approveWithdrawal(withdrawalId)
                .onSuccess { _feedbackMessage.value = "উত্তোলন আবেদন অনুমোদন করা হয়েছে" }
                .onFailure { _feedbackMessage.value = it.message ?: "অনুমোদন ব্যর্থ হয়েছে" }
        }
    }

    fun rejectWithdrawal(withdrawalId: Long, reason: String) {
        viewModelScope.launch {
            repository.rejectWithdrawal(withdrawalId, reason)
                .onSuccess { _feedbackMessage.value = "আবেদন বাতিল করে ব্যালেন্স ফেরত দেওয়া হয়েছে" }
                .onFailure { _feedbackMessage.value = it.message ?: "বাতিল করা যায়নি" }
        }
    }

    fun updateContactAndPassword(
        whatsappNumber: String,
        facebookUrl: String,
        newPassword: String?
    ) {
        viewModelScope.launch {
            repository.updateContactAndPassword(whatsappNumber, facebookUrl, newPassword)
                .onSuccess { _feedbackMessage.value = "যোগাযোগ ও সেটিংস আপডেট হয়েছে" }
                .onFailure { _feedbackMessage.value = it.message ?: "আপডেট ব্যর্থ হয়েছে" }
        }
    }

    class Factory(private val repository: EarningRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AdminViewModel(repository) as T
        }
    }
}
