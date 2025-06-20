package com.moviles.yetify.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.moviles.yetify.models.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Locale

private val Context.dataStore by preferencesDataStore("user_prefs")

class UserPreferences(private val context: Context) {

    companion object {
        private val USER_ID = intPreferencesKey("user_id")
        private val USER_NAME = stringPreferencesKey("user_name")
        private val EMAIL = stringPreferencesKey("email")
        private val BIRTHDAY = stringPreferencesKey("birthday")
        private val REGISTRATION_DATE = stringPreferencesKey("registration_date")
        private val TOKEN = stringPreferencesKey("token")

        private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    }

    val userId: Flow<Int?> = context.dataStore.data.map { it[USER_ID] }
    val userName: Flow<String?> = context.dataStore.data.map { it[USER_NAME] }
    val email: Flow<String?> = context.dataStore.data.map { it[EMAIL] }
    val birthday: Flow<String?> = context.dataStore.data.map { it[BIRTHDAY] }
    val registrationDate: Flow<String?> = context.dataStore.data.map { it[REGISTRATION_DATE] }
    val token: Flow<String?> = context.dataStore.data.map { it[TOKEN] }

    suspend fun saveUser(user: User) {
        context.dataStore.edit { prefs ->
            prefs[USER_ID] = user.id
            prefs[USER_NAME] = user.userName
            prefs[EMAIL] = user.email
            prefs[BIRTHDAY] = dateFormat.format(user.birthday)
            prefs[REGISTRATION_DATE] = dateFormat.format(user.registrationDate)
            prefs[TOKEN] = user.token
        }
    }
    suspend fun saveToken(token: String) {
        context.dataStore.edit { prefs ->
            prefs[TOKEN] = token
        }
    }

    suspend fun clearUser() {
        context.dataStore.edit { it.clear() }
    }
}
