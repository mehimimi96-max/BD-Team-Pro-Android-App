package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.AppConfigEntity
import com.example.data.EarningRepository
import com.example.data.ReferralEntity
import com.example.data.TaskEntity
import com.example.data.WithdrawalEntity
import com.example.data.WorkerEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WorkerViewModel(
    private val workerId: Long,
    private val repository: EarningRepository
) : ViewModel() {

    val config: StateFlow<AppConfigEntity> = repository.configFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppConfigEntity())

    val worker: StateFlow<WorkerEntity?> = repository.observeWorker(workerId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val activeTasks: StateFlow<List<TaskEntity>> = repository.activeTasksFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val completedTaskIds: StateFlow<List<Long>> = repository.observeCompletedTaskIds(workerId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val referrals: StateFlow<List<ReferralEntity>> = repository.observeWorkerReferrals(workerId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val withdrawals: StateFlow<List<WithdrawalEntity>> = repository.observeWorkerWithdrawals(workerId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _feedbackMessage = MutableStateFlow<String?>(null)
    val feedbackMessage: StateFlow<String?> = _feedbackMessage.asStateFlow()

    fun consumeFeedback() {
        _feedbackMessage.value = null
    }

    fun completeTask(taskId: Long) {
        viewModelScope.launch {
            repository.completeTask(workerId, taskId)
                .onSuccess { reward ->
                    _feedbackMessage.value = "অভিনন্দন! আপনার অ্যাকাউন্টে ৳${reward.toInt()} যোগ হয়েছে"
                }
                .onFailure {
                    _feedbackMessage.value = it.message ?: "কাজ সম্পন্ন করা যায়নি"
                }
        }
    }

    fun requestWithdrawal(amount: Double, method: String, accountNumber: String) {
        viewModelScope.launch {
            repository.requestWithdrawal(workerId, amount, method, accountNumber)
                .onSuccess {
                    _feedbackMessage.value = "উত্তোলন রিকোয়েস্ট অ্যাডমিনের কাছে পাঠানো হয়েছে!"
                }
                .onFailure {
                    _feedbackMessage.value = it.message ?: "রিকোয়েস্ট পাঠানো যায়নি"
                }
        }
    }

    class Factory(
        private val workerId: Long,
        private val repository: EarningRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return WorkerViewModel(workerId, repository) as T
        }
    }
}
