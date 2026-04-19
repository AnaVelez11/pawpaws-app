package app.pawpaws.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import app.pawpaws.data.model.UserSession
import app.pawpaws.domain.model.enums.Rol
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")

@Singleton
class SessionDataStore @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    private object Keys {
        val USER_ID = stringPreferencesKey("user_id")
        val ROL    = stringPreferencesKey("rol")
    }

    val sessionFlow: Flow<UserSession?> = context.dataStore.data.map { prefs ->
        val userId = prefs[Keys.USER_ID]
        val rolStr = prefs[Keys.ROL]
        if (userId.isNullOrBlank() || rolStr.isNullOrBlank()) null
        else UserSession(userId = userId, rol = Rol.valueOf(rolStr))
    }

    suspend fun saveSession(userId: String, rol: Rol) {
        context.dataStore.edit { prefs ->
            prefs[Keys.USER_ID] = userId
            prefs[Keys.ROL]     = rol.name
        }
    }

    suspend fun clearSession() {
        context.dataStore.edit { it.clear() }
    }
}