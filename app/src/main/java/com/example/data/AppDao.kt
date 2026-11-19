package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    // --- App Config ---
    @Query("SELECT * FROM app_config WHERE id = 1")
    fun observeConfig(): Flow<AppConfigEntity?>

    @Query("SELECT * FROM app_config WHERE id = 1")
    suspend fun getConfig(): AppConfigEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertConfig(config: AppConfigEntity)

    // --- Workers ---
    @Query("SELECT * FROM workers ORDER BY createdAt DESC")
    fun observeAllWorkers(): Flow<List<WorkerEntity>>

    @Query("SELECT * FROM workers WHERE id = :workerId")
    fun observeWorkerById(workerId: Long): Flow<WorkerEntity?>

    @Query("SELECT * FROM workers WHERE phone = :phone LIMIT 1")
    suspend fun getWorkerByPhone(phone: String): WorkerEntity?

    @Query("SELECT * FROM workers WHERE id = :workerId LIMIT 1")
    suspend fun getWorkerById(workerId: Long): WorkerEntity?

    @Query("SELECT * FROM workers WHERE referralCode = :code LIMIT 1")
    suspend fun getWorkerByReferralCode(code: String): WorkerEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertWorker(worker: WorkerEntity): Long

    @Update
    suspend fun updateWorker(worker: WorkerEntity)

    @Query("UPDATE workers SET isUnlocked = :unlocked WHERE id = :workerId")
    suspend fun setWorkerUnlocked(workerId: Long, unlocked: Boolean)

    // --- Tasks ---
    @Query("SELECT * FROM tasks ORDER BY createdAt DESC")
    fun observeAllTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE isActive = 1 ORDER BY createdAt DESC")
    fun observeActiveTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE id = :taskId LIMIT 1")
    suspend fun getTaskById(taskId: Long): TaskEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity): Long

    @Query("UPDATE tasks SET isActive = :isActive WHERE id = :taskId")
    suspend fun setTaskActive(taskId: Long, isActive: Boolean)

    @Query("DELETE FROM tasks WHERE id = :taskId")
    suspend fun deleteTaskById(taskId: Long)

    // --- Task Completions ---
    @Query("SELECT taskId FROM task_completions WHERE workerId = :workerId")
    fun observeCompletedTaskIds(workerId: Long): Flow<List<Long>>

    @Query("SELECT * FROM task_completions WHERE workerId = :workerId AND taskId = :taskId LIMIT 1")
    suspend fun getTaskCompletion(workerId: Long, taskId: Long): TaskCompletionEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertTaskCompletion(completion: TaskCompletionEntity)

    // --- Referrals ---
    @Query("SELECT * FROM referrals ORDER BY createdAt DESC")
    fun observeAllReferrals(): Flow<List<ReferralEntity>>

    @Query("SELECT * FROM referrals WHERE referrerWorkerId = :workerId ORDER BY createdAt DESC")
    fun observeReferralsForWorker(workerId: Long): Flow<List<ReferralEntity>>

    @Query("SELECT * FROM referrals WHERE id = :referralId LIMIT 1")
    suspend fun getReferralById(referralId: Long): ReferralEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReferral(referral: ReferralEntity): Long

    @Update
    suspend fun updateReferral(referral: ReferralEntity)

    // --- Withdrawals ---
    @Query("SELECT * FROM withdrawals ORDER BY createdAt DESC")
    fun observeAllWithdrawals(): Flow<List<WithdrawalEntity>>

    @Query("SELECT * FROM withdrawals WHERE workerId = :workerId ORDER BY createdAt DESC")
    fun observeWithdrawalsForWorker(workerId: Long): Flow<List<WithdrawalEntity>>

    @Query("SELECT * FROM withdrawals WHERE id = :withdrawalId LIMIT 1")
    suspend fun getWithdrawalById(withdrawalId: Long): WithdrawalEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWithdrawal(withdrawal: WithdrawalEntity): Long

    @Update
    suspend fun updateWithdrawal(withdrawal: WithdrawalEntity)

    // --- Atomic Transactions ---
    @Transaction
    suspend fun completeTaskTransaction(workerId: Long, taskId: Long): Double {
        val worker = getWorkerById(workerId) ?: error("Worker not found")
        require(worker.isUnlocked) { "অ্যাকাউন্ট আনলক করা নেই" }
        val task = getTaskById(taskId) ?: error("টাস্ক পাওয়া যায়নি")
        require(task.isActive) { "টাস্কটি বর্তমানে বন্ধ আছে" }
        val existing = getTaskCompletion(workerId, taskId)
        require(existing == null) { "আপনি ইতিমধ্যে এই কাজটি সম্পন্ন করেছেন" }

        val reward = task.rewardAmount.coerceIn(0.0, 10.0)
        insertTaskCompletion(
            TaskCompletionEntity(
                workerId = workerId,
                taskId = taskId,
                rewardEarned = reward
            )
        )
        updateWorker(
            worker.copy(
                balance = worker.balance + reward,
                totalEarned = worker.totalEarned + reward
            )
        )
        return reward
    }

    @Transaction
    suspend fun verifyReferralTransaction(referralId: Long) {
        val referral = getReferralById(referralId) ?: error("Referral not found")
        if (referral.status == ReferralStatus.VERIFIED.name) return
        val referrer = getWorkerById(referral.referrerWorkerId) ?: error("Referrer not found")

        updateReferral(referral.copy(status = ReferralStatus.VERIFIED.name))
        updateWorker(
            referrer.copy(
                verifiedReferralsCount = referrer.verifiedReferralsCount + 1,
                balance = referrer.balance + referral.bonusAmount,
                totalEarned = referrer.totalEarned + referral.bonusAmount
            )
        )
    }

    @Transaction
    suspend fun rejectReferralTransaction(referralId: Long) {
        val referral = getReferralById(referralId) ?: error("Referral not found")
        if (referral.status != ReferralStatus.PENDING.name) return
        updateReferral(referral.copy(status = ReferralStatus.REJECTED.name))
    }

    @Transaction
    suspend fun requestWithdrawalTransaction(
        workerId: Long,
        amount: Double,
        method: String,
        accountNumber: String
    ) {
        val worker = getWorkerById(workerId) ?: error("Worker not found")
        require(worker.isUnlocked) { "অ্যাকাউন্ট লক করা আছে" }
        require(worker.verifiedReferralsCount >= 3) {
            "টাকা তোলার জন্য কমপক্ষে ৩টি ভেরিফায়েড রেফার প্রয়োজন"
        }
        require(amount >= 100.0) { "সর্বনিম্ন উত্তোলন ১০০ টাকা" }
        require(worker.balance >= amount) { "আপনার অ্যাকাউন্টে পর্যাপ্ত ব্যালেন্স নেই" }

        updateWorker(worker.copy(balance = worker.balance - amount))
        insertWithdrawal(
            WithdrawalEntity(
                workerId = worker.id,
                workerName = worker.name,
                workerPhone = worker.phone,
                amount = amount,
                method = method,
                accountNumber = accountNumber,
                verifiedReferralsAtRequest = worker.verifiedReferralsCount,
                status = WithdrawalStatus.PENDING.name
            )
        )
    }

    @Transaction
    suspend fun approveWithdrawalTransaction(withdrawalId: Long) {
        val withdrawal = getWithdrawalById(withdrawalId) ?: error("Withdrawal not found")
        if (withdrawal.status != WithdrawalStatus.PENDING.name) return
        updateWithdrawal(
            withdrawal.copy(
                status = WithdrawalStatus.APPROVED.name,
                adminNote = "পেমেন্ট অনুমোদিত হয়েছে",
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    @Transaction
    suspend fun rejectWithdrawalTransaction(withdrawalId: Long, reason: String) {
        val withdrawal = getWithdrawalById(withdrawalId) ?: error("Withdrawal not found")
        if (withdrawal.status != WithdrawalStatus.PENDING.name) return
        val worker = getWorkerById(withdrawal.workerId)
        if (worker != null) {
            updateWorker(worker.copy(balance = worker.balance + withdrawal.amount))
        }
        updateWithdrawal(
            withdrawal.copy(
                status = WithdrawalStatus.REJECTED.name,
                adminNote = reason.ifBlank { "অ্যাডমিন আবেদনটি বাতিল করেছেন" },
                updatedAt = System.currentTimeMillis()
            )
        )
    }
}
