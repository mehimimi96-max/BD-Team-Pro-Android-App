package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class EarningRepository(
    private val dao: AppDao,
    private val sessionPrefs: SessionPreferences
) {
    val configFlow: Flow<AppConfigEntity> = dao.observeConfig().map { it ?: AppConfigEntity() }
    val sessionFlow: Flow<SessionState> = sessionPrefs.sessionFlow
    val allWorkersFlow: Flow<List<WorkerEntity>> = dao.observeAllWorkers()
    val allTasksFlow: Flow<List<TaskEntity>> = dao.observeAllTasks()
    val activeTasksFlow: Flow<List<TaskEntity>> = dao.observeActiveTasks()
    val allReferralsFlow: Flow<List<ReferralEntity>> = dao.observeAllReferrals()
    val allWithdrawalsFlow: Flow<List<WithdrawalEntity>> = dao.observeAllWithdrawals()

    fun observeWorker(workerId: Long): Flow<WorkerEntity?> = dao.observeWorkerById(workerId)
    fun observeCompletedTaskIds(workerId: Long): Flow<List<Long>> = dao.observeCompletedTaskIds(workerId)
    fun observeWorkerReferrals(workerId: Long): Flow<List<ReferralEntity>> = dao.observeReferralsForWorker(workerId)
    fun observeWorkerWithdrawals(workerId: Long): Flow<List<WithdrawalEntity>> = dao.observeWithdrawalsForWorker(workerId)

    // --- Admin Auth & Config ---
    suspend fun setupAdmin(
        name: String,
        phone: String,
        password: String,
        whatsappNumber: String,
        facebookUrl: String
    ): Result<Unit> = runCatching {
        val cleanName = name.trim()
        val cleanPhone = phone.trim()
        val cleanPassword = password.trim()
        require(cleanName.isNotEmpty()) { "অ্যাডমিনের নাম লিখুন" }
        require(cleanPhone.length >= 6) { "সঠিক ফোন নম্বর দিন" }
        require(cleanPassword.length >= 4) { "পাসওয়ার্ড কমপক্ষে ৪ অক্ষরের হতে হবে" }

        dao.upsertConfig(
            AppConfigEntity(
                id = 1,
                adminName = cleanName,
                adminPhone = cleanPhone,
                adminPassword = cleanPassword,
                whatsappNumber = whatsappNumber.trim(),
                facebookUrl = normalizeUrlOrEmpty(facebookUrl),
                adminSetupCompleted = true
            )
        )
        sessionPrefs.setAdminLoggedIn(true)
    }

    suspend fun loginAdmin(phone: String, password: String): Result<Unit> = runCatching {
        val config = dao.getConfig()
        require(config != null && config.adminSetupCompleted) { "প্রথমে অ্যাডমিন অ্যাকাউন্ট সেটআপ করুন" }
        require(config.adminPhone == phone.trim() && config.adminPassword == password.trim()) {
            "ফোন নম্বর বা পাসওয়ার্ড সঠিক নয়"
        }
        sessionPrefs.setAdminLoggedIn(true)
    }

    suspend fun logoutAdmin() {
        sessionPrefs.setAdminLoggedIn(false)
    }

    suspend fun updateContactAndPassword(
        whatsappNumber: String,
        facebookUrl: String,
        newPassword: String?
    ): Result<Unit> = runCatching {
        val current = dao.getConfig() ?: error("অ্যাডমিন কনফিগারেশন পাওয়া যায়নি")
        val updatedPassword = if (!newPassword.isNullOrBlank()) {
            require(newPassword.trim().length >= 4) { "নতুন পাসওয়ার্ড কমপক্ষে ৪ অক্ষরের হতে হবে" }
            newPassword.trim()
        } else {
            current.adminPassword
        }
        dao.upsertConfig(
            current.copy(
                whatsappNumber = whatsappNumber.trim(),
                facebookUrl = normalizeUrlOrEmpty(facebookUrl),
                adminPassword = updatedPassword
            )
        )
    }

    // --- Worker Auth ---
    suspend fun registerWorker(
        name: String,
        phone: String,
        password: String,
        referralCodeUsed: String
    ): Result<WorkerEntity> = runCatching {
        val cleanName = name.trim()
        val cleanPhone = phone.trim()
        val cleanPassword = password.trim()
        val cleanRefCode = referralCodeUsed.trim().uppercase()

        require(cleanName.isNotEmpty()) { "আপনার পূর্ণ নাম লিখুন" }
        require(cleanPhone.length >= 6) { "সঠিক মোবাইল নম্বর লিখুন" }
        require(cleanPassword.length >= 4) { "পাসওয়ার্ড কমপক্ষে ৪ অক্ষরের হতে হবে" }
        require(dao.getWorkerByPhone(cleanPhone) == null) { "এই নম্বর দিয়ে ইতিমধ্যে অ্যাকাউন্ট খোলা আছে" }

        val referrer = if (cleanRefCode.isNotEmpty()) {
            dao.getWorkerByReferralCode(cleanRefCode)
                ?: error("প্রদত্ত রেফার কোডটি সঠিক নয়")
        } else null

        val generatedCode = generateReferralCode(cleanPhone)
        val newWorker = WorkerEntity(
            phone = cleanPhone,
            name = cleanName,
            password = cleanPassword,
            referralCode = generatedCode,
            referredByCode = referrer?.referralCode ?: "",
            isUnlocked = false // Locked by default until Admin approves!
        )
        val newId = dao.insertWorker(newWorker)
        val created = newWorker.copy(id = newId)

        if (referrer != null) {
            dao.insertReferral(
                ReferralEntity(
                    referrerWorkerId = referrer.id,
                    referrerName = referrer.name,
                    referrerPhone = referrer.phone,
                    referredWorkerId = created.id,
                    referredName = created.name,
                    referredPhone = created.phone,
                    bonusAmount = 50.0,
                    status = ReferralStatus.PENDING.name
                )
            )
        }
        sessionPrefs.setActiveWorkerId(created.id)
        created
    }

    suspend fun loginWorker(phone: String, password: String): Result<WorkerEntity> = runCatching {
        val cleanPhone = phone.trim()
        val cleanPassword = password.trim()
        val worker = dao.getWorkerByPhone(cleanPhone) ?: error("এই নম্বরের কোনো অ্যাকাউন্ট পাওয়া যায়নি")
        require(worker.password == cleanPassword) { "পাসওয়ার্ড সঠিক নয়" }
        sessionPrefs.setActiveWorkerId(worker.id)
        worker
    }

    suspend fun logoutWorker() {
        sessionPrefs.setActiveWorkerId(null)
    }

    // --- Admin Operations ---
    suspend fun addTask(
        category: TaskCategory,
        title: String,
        description: String,
        linkUrl: String,
        rewardAmount: Double,
        durationSeconds: Int
    ): Result<Unit> = runCatching {
        val cleanTitle = title.trim()
        val cleanUrl = normalizeUrl(linkUrl)
        require(cleanTitle.isNotEmpty()) { "কাজের শিরোনাম লিখুন" }
        require(cleanUrl.isNotEmpty()) { "সঠিক লিংক দিন" }
        require(rewardAmount in 1.0..10.0) { "প্রতি কাজের পুরস্কার ১ থেকে সর্বোচ্চ ১০ টাকার মধ্যে হতে হবে" }
        require(durationSeconds in 5..300) { "সময় ৫ থেকে ৩০০ সেকেন্ডের মধ্যে দিন" }

        dao.insertTask(
            TaskEntity(
                category = category.name,
                title = cleanTitle,
                description = description.trim(),
                linkUrl = cleanUrl,
                rewardAmount = rewardAmount,
                durationSeconds = durationSeconds,
                isActive = true
            )
        )
    }

    suspend fun toggleTaskActive(taskId: Long, isActive: Boolean): Result<Unit> = runCatching {
        dao.setTaskActive(taskId, isActive)
    }

    suspend fun deleteTask(taskId: Long): Result<Unit> = runCatching {
        dao.deleteTaskById(taskId)
    }

    suspend fun setWorkerUnlocked(workerId: Long, unlocked: Boolean): Result<Unit> = runCatching {
        dao.setWorkerUnlocked(workerId, unlocked)
    }

    suspend fun verifyReferral(referralId: Long): Result<Unit> = runCatching {
        dao.verifyReferralTransaction(referralId)
    }

    suspend fun rejectReferral(referralId: Long): Result<Unit> = runCatching {
        dao.rejectReferralTransaction(referralId)
    }

    suspend fun approveWithdrawal(withdrawalId: Long): Result<Unit> = runCatching {
        dao.approveWithdrawalTransaction(withdrawalId)
    }

    suspend fun rejectWithdrawal(withdrawalId: Long, reason: String): Result<Unit> = runCatching {
        dao.rejectWithdrawalTransaction(withdrawalId, reason)
    }

    // --- Worker Operations ---
    suspend fun completeTask(workerId: Long, taskId: Long): Result<Double> = runCatching {
        dao.completeTaskTransaction(workerId, taskId)
    }

    suspend fun requestWithdrawal(
        workerId: Long,
        amount: Double,
        method: String,
        accountNumber: String
    ): Result<Unit> = runCatching {
        val cleanAccount = accountNumber.trim()
        require(cleanAccount.length >= 6) { "সঠিক $method নম্বর লিখুন" }
        dao.requestWithdrawalTransaction(
            workerId = workerId,
            amount = amount,
            method = method,
            accountNumber = cleanAccount
        )
    }

    private fun generateReferralCode(phone: String): String {
        val digits = phone.filter { it.isDigit() }
        val suffix = if (digits.length >= 4) digits.takeLast(4) else digits.padStart(4, '0')
        val rand = (10..99).random()
        return "BD$suffix$rand"
    }

    private fun normalizeUrl(raw: String): String {
        val trimmed = raw.trim()
        if (trimmed.isEmpty()) return ""
        return if (trimmed.startsWith("http://", ignoreCase = true) ||
            trimmed.startsWith("https://", ignoreCase = true)
        ) {
            trimmed
        } else {
            "https://$trimmed"
        }
    }

    private fun normalizeUrlOrEmpty(raw: String): String {
        val trimmed = raw.trim()
        return if (trimmed.isEmpty()) "" else normalizeUrl(trimmed)
    }
}
