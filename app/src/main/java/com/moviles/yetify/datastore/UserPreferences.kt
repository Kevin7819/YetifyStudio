package com.moviles.yetify.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("user_prefs")

class UserPreferences(private val context: Context) {

    companion object {
        val USER_ID = intPreferencesKey("user_id")
        val TOKEN = stringPreferencesKey("token")
    }

    val userId: Flow<Int?> = context.dataStore.data.map { prefs ->
        prefs[USER_ID]
    }

    val token: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[TOKEN]
    }

    suspend fun saveUser(id: Int, token: String) {
        context.dataStore.edit { prefs ->
            prefs[USER_ID] = id
            prefs[TOKEN] = token
        }
    }

    suspend fun clearUser() {
        context.dataStore.edit { prefs ->
            prefs.clear()
        }
    }
}
