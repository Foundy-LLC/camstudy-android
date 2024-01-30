package io.foundy.feature.auth.data.source

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import javax.inject.Inject

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")

class DataStoreAuthDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) : AuthLocalDataSource {

    override suspend fun markAsUserInitialInfoExists(userId: String) {
        val key = intPreferencesKey(userId)
        context.dataStore.edit {
            // 탈퇴하고 다시 가입하는 경우 uid가 바뀌기 때문에 기기에 저장된 이전의 아이디 로컬 정보는 고려하지 않아도 된다.
            it.putAll(key to 1)
        }
    }

    override suspend fun existsUserInitialInfo(userId: String): Boolean {
        val key = intPreferencesKey(userId)
        val preferences = context.dataStore.data.first()
        return preferences.contains(key)
    }
}
