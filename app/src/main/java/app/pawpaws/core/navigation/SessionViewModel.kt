package app.pawpaws.core.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.pawpaws.data.datastore.SessionDataStore
import app.pawpaws.data.model.UserSession
import app.pawpaws.domain.model.enums.Rol
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface SessionState {
    data object Loading : SessionState
    data object NotAuthenticated : SessionState
    data class Authenticated(val session: UserSession) : SessionState
}

@HiltViewModel
class SessionViewModel @Inject constructor(
    private val sessionDataStore: SessionDataStore
) : ViewModel() {

    val sessionState: StateFlow<SessionState> = sessionDataStore.sessionFlow
        .map { session ->
            if (session != null) SessionState.Authenticated(session)
            else SessionState.NotAuthenticated
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SessionState.Loading
        )

    fun saveSession(userId: String, rol: Rol) {
        viewModelScope.launch {
            sessionDataStore.saveSession(userId, rol)
        }
    }

    fun clearSession() {
        viewModelScope.launch {
            sessionDataStore.clearSession()
        }
    }
}