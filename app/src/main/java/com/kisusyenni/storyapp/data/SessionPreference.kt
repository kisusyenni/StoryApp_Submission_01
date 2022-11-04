package com.kisusyenni.storyapp.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.kisusyenni.storyapp.data.source.local.entity.Session
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SessionPreference private constructor(private val dataStore: DataStore<Preferences>) {
    fun getSession(): Flow<Session> {
        return dataStore.data.map { preferences ->
            Session(
                name = preferences[NAME_KEY] ?:"",
                token = preferences[TOKEN_KEY] ?:"",
                userId = preferences[USERID_KEY] ?:"",
            )
        }
    }

    suspend fun saveSession(session: Session) {
        dataStore.edit { preferences ->
            preferences[NAME_KEY] = session.name
            preferences[TOKEN_KEY] = session.token
            preferences[USERID_KEY] = session.userId
        }
    }

    suspend fun removeSession() {
        dataStore.edit { preferences ->
            preferences[NAME_KEY] = ""
            preferences[TOKEN_KEY] = ""
            preferences[USERID_KEY] = ""
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: SessionPreference? = null

        private val NAME_KEY = stringPreferencesKey("name")
        private val TOKEN_KEY = stringPreferencesKey("token")
        private val USERID_KEY = stringPreferencesKey("userId")

        fun getInstance(dataStore: DataStore<Preferences>): SessionPreference {
            return INSTANCE ?: synchronized(this) {
                val instance = SessionPreference(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}