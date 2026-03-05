package app.pawpaws.features.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.pawpaws.core.utils.RequestResult
import app.pawpaws.core.utils.ValidatedField
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val _email = MutableStateFlow(ValidatedField())
    val email: StateFlow<ValidatedField> = _email

    private val _password = MutableStateFlow(ValidatedField())
    val password: StateFlow<ValidatedField> = _password

    private val _loginResult = MutableStateFlow<RequestResult<Unit>>(RequestResult.Idle)
    val loginResult: StateFlow<RequestResult<Unit>> = _loginResult

    fun onEmailChange(value: String) {
        _email.value = ValidatedField(value)
    }

    fun onPasswordChange(value: String) {
        _password.value = ValidatedField(value)
    }

    fun login() {

        val emailValue = _email.value.value
        val passwordValue = _password.value.value

        if (!emailValue.contains("@")) {
            _email.value = ValidatedField(emailValue, "Correo inválido")
            return
        }

        if (passwordValue.length < 6) {
            _password.value = ValidatedField(passwordValue, "Mínimo 6 caracteres")
            return
        }

        _loginResult.value = RequestResult.Loading

        viewModelScope.launch {
            delay(1500) // simulación servidor

            // Simulación simple
            if (emailValue == "test@test.com" && passwordValue == "123456") {
                _loginResult.value = RequestResult.Success(Unit)
            } else {
                _loginResult.value = RequestResult.Error("Credenciales incorrectas")
            }
        }
    }
}