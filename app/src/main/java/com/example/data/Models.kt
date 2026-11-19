package com.example.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

enum class TaskCategory(
    val labelBn: String,
    val labelEn: String
) {
    VIDEO("ভিডিও দেখা", "Watch Video"),
    AD("বিজ্ঞাপন দেখা", "Watch Ad"),
    GAME("গেম খেলা", "Play Game"),
    SOCIAL("লাইক / ফলো / শেয়ার", "Like / Follow / Share")
}

enum class ReferralStatus(val labelBn: String) {
    PENDING("অপেক্ষমাণ"),
    VERIFIED("ভেরিফায়েড"),
    REJECTED("বাতিল")
}

enum class WithdrawalStatus(val labelBn: String) {
    PENDING("অপেক্ষমাণ"),
    APPROVED("অনুমোদিত"),
    REJECTED("বাতিল")
}

@Entity(tableName = "app_config")
data class AppConfigEntity(
    @PrimaryKey val id: Int = 1,
    val adminName: String = "",
    val adminPhone: String = "",
    val adminPassword: String = "",
    val whatsappNumber: String = "",
    val facebookUrl: String = "",
    val adminSetupCompleted: Boolean = false
)

@Entity(
    tableName = "workers",
    indices = [
        Index(value = ["phone"], unique = true),
        Index(value = ["referralCode"], unique = true)
    ]
)
data class WorkerEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val phone: String,
    val name: String,
    val password: String,
    val referralCode: String,
    val referredByCode: String = "",
    val isUnlocked: Boolean = false,
    val balance: Double = 0.0,
    val totalEarned: Double = 0.0,
    val verifiedReferralsCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val category: String,
    val title: String,
    val description: String = "",
    val linkUrl: String,
    val rewardAmount: Double,
    val durationSeconds: Int = 15,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
) {
    fun taskCategory(): TaskCategory =
        TaskCategory.entries.firstOrNull { it.name == category } ?: TaskCategory.VIDEO
}

@Entity(
    tableName = "task_completions",
    indices = [Index(value = ["workerId", "taskId"], unique = true)]
)
data class TaskCompletionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val workerId: Long,
    val taskId: Long,
    val rewardEarned: Double,
    val completedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "referrals")
data class ReferralEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val referrerWorkerId: Long,
    val referrerName: String,
    val referrerPhone: String,
    val referredWorkerId: Long,
    val referredName: String,
    val referredPhone: String,
    val bonusAmount: Double = 50.0,
    val status: String = ReferralStatus.PENDING.name,
    val createdAt: Long = System.currentTimeMillis()
) {
    fun referralStatus(): ReferralStatus =
        ReferralStatus.entries.firstOrNull { it.name == status } ?: ReferralStatus.PENDING
}

@Entity(tableName = "withdrawals")
data class WithdrawalEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val workerId: Long,
    val workerName: String,
    val workerPhone: String,
    val amount: Double,
    val method: String,
    val accountNumber: String,
    val verifiedReferralsAtRequest: Int,
    val status: String = WithdrawalStatus.PENDING.name,
    val adminNote: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    fun withdrawalStatus(): WithdrawalStatus =
        WithdrawalStatus.entries.firstOrNull { it.name == status } ?: WithdrawalStatus.PENDING
}
