package com.example.streamitv1

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "user_preferences")

object UserPreferences {
    private val USER_NAME_KEY = stringPreferencesKey("user_name")
    private val PASSWORD_KEY = stringPreferencesKey("password")

    fun getUserName(context: Context): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[USER_NAME_KEY]
        }
    }

    fun getPassword(context: Context): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[PASSWORD_KEY]
        }
    }

    suspend fun saveCredentials(context: Context, userName: String, password: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_NAME_KEY] = userName
            preferences[PASSWORD_KEY] = password
        }
    }
}