package com.example.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "bd_earning_session")

data class SessionState(
    val activeWorkerId: Long? = null,
    val isAdminLoggedIn: Boolean = false
)

class SessionPreferences(private val context: Context) {
    private object Keys {
        val ACTIVE_WORKER_ID = longPreferencesKey("active_worker_id")
        val IS_ADMIN_LOGGED_IN = booleanPreferencesKey("is_admin_logged_in")
    }

    val sessionFlow: Flow<SessionState> = context.dataStore.data.map { prefs ->
        val workerId = prefs[Keys.ACTIVE_WORKER_ID]?.takeIf { it > 0L }
        val adminLoggedIn = prefs[Keys.IS_ADMIN_LOGGED_IN] ?: false
        SessionState(activeWorkerId = workerId, isAdminLoggedIn = adminLoggedIn)
    }

    suspend fun setActiveWorkerId(workerId: Long?) {
        context.dataStore.edit { prefs ->
            if (workerId == null || workerId <= 0L) {
                prefs.remove(Keys.ACTIVE_WORKER_ID)
            } else {
                prefs[Keys.ACTIVE_WORKER_ID] = workerId
            }
        }
    }

    suspend fun setAdminLoggedIn(loggedIn: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[Keys.IS_ADMIN_LOGGED_IN] = loggedIn
        }
    }
}
