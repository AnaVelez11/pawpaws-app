package app.pawpaws.features.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.pawpaws.core.utils.RequestResult
import app.pawpaws.core.utils.ValidatedField
import app.pawpaws.data.datastore.SessionDataStore
import app.pawpaws.domain.model.enums.Rol
import app.pawpaws.domain.repository.UsuarioRepository
import app.pawpaws.domain.repository.ResourceProvider
import app.pawpaws.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val usuarioRepository: UsuarioRepository,
    private val sessionDataStore: SessionDataStore,
    private val resourceProvider: ResourceProvider
) : ViewModel() {

    private val _email    = MutableStateFlow(ValidatedField())
    val email: StateFlow<ValidatedField> = _email

    private val _password = MutableStateFlow(ValidatedField())
    val password: StateFlow<ValidatedField> = _password

    // Ahora el Success lleva el Rol para bifurcar navegación
    private val _loginResult = MutableStateFlow<RequestResult<Rol>>(RequestResult.Idle)
    val loginResult: StateFlow<RequestResult<Rol>> = _loginResult

    fun onEmailChange(value: String)    { _email.value    = ValidatedField(value) }
    fun onPasswordChange(value: String) { _password.value = ValidatedField(value) }

    fun login() {
        val emailValue    = _email.value.value.trim()
        val passwordValue = _password.value.value

        if (!emailValue.contains("@")) {
            _email.value = ValidatedField(emailValue, resourceProvider.getString(R.string.login_invalid_email))
            return
        }
        if (passwordValue.length < 6) {
            _password.value = ValidatedField(passwordValue, resourceProvider.getString(R.string.login_min_password))
            return
        }

        _loginResult.value = RequestResult.Loading

        viewModelScope.launch {
            val usuario = usuarioRepository.login(emailValue, passwordValue)
            if (usuario != null) {
                sessionDataStore.saveSession(usuario.id, usuario.rol)
                _loginResult.value = RequestResult.Success(usuario.rol)
            } else {
                _loginResult.value = RequestResult.Error(resourceProvider.getString(R.string.login_wrong_credentials))
            }
        }
    }

    fun resetResult() { _loginResult.value = RequestResult.Idle }
}