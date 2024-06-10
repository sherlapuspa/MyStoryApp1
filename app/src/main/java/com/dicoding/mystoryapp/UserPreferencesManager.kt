package com.dicoding.mystoryapp

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class UserPreferencesManager private constructor(private val dataStore: DataStore<Preferences>) {

    fun getLoginStatus(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[LOGIN_STATUS] ?: false
        }
    }

    suspend fun storeLoginStatus(loginSession: Boolean) {
        dataStore.edit { preferences ->
            preferences[LOGIN_STATUS] = loginSession
        }
    }

    fun getToken(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[TOKEN] ?: ""
        }
    }


    suspend fun storeToken(token: String) {
        dataStore.edit { preferences ->
            preferences[TOKEN] = token
        }
    }

    fun getName(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[NAME] ?: ""
        }
    }


    suspend fun storeName(name: String) {
        dataStore.edit { preferences ->
            preferences[NAME] = name
        }
    }

    suspend fun resetLoginData() {
        dataStore.edit { preferences ->
            preferences.remove(LOGIN_STATUS)
            preferences.remove(TOKEN)
            preferences.remove(NAME)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: UserPreferencesManager? = null

        fun getInstance(dataStore: DataStore<Preferences>): UserPreferencesManager {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreferencesManager(dataStore)
                INSTANCE = instance
                instance
            }
        }

        private val LOGIN_STATUS = booleanPreferencesKey("login_status")
        private val TOKEN = stringPreferencesKey("token")
        private val NAME = stringPreferencesKey("name")

    }
}